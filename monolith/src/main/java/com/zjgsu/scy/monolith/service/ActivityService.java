package com.zjgsu.scy.monolith.service;

import com.zjgsu.scy.monolith.dto.ActivityDtos.*;
import com.zjgsu.scy.monolith.exception.*;
import com.zjgsu.scy.monolith.model.*;
import com.zjgsu.scy.monolith.repository.*;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ActivityService {
    private final ActivityRepository activities;
    private final RegistrationRepository registrations;
    public ActivityService(ActivityRepository activities, RegistrationRepository registrations) {
        this.activities = activities; this.registrations = registrations;
    }
    @Transactional
    public ActivityView create(CreateActivity request) {
        return ActivityView.from(activities.save(new Activity(request.name().strip(),
            request.location().strip(), request.startsAt(), request.capacity())));
    }
    public List<ActivityView> list() {
        return activities.findAll(Sort.by("id")).stream().map(ActivityView::from).toList();
    }
    public ActivityView get(Long id) { return ActivityView.from(requireActivity(id)); }
    public List<RegistrationView> registrations(Long id) {
        requireActivity(id);
        return registrations.findByActivityIdOrderByIdAsc(id).stream().map(RegistrationView::from).toList();
    }
    public RegistrationView registration(Long activityId, Long registrationId) {
        requireActivity(activityId);
        Registration registration = registrations.findById(registrationId)
            .filter(r -> r.getActivity().getId().equals(activityId))
            .orElseThrow(() -> new NotFoundException("报名记录不存在: " + registrationId));
        return RegistrationView.from(registration);
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public RegistrationView register(Long id, Register request) {
        Activity activity = activities.findForRegistration(id)
            .orElseThrow(() -> new NotFoundException("活动不存在: " + id));
        if (!activity.getStartsAt().isAfter(LocalDateTime.now()))
            throw new BusinessConflictException("活动已开始，不能报名");
        if (registrations.existsByActivityIdAndMemberNo(id, request.memberNo()))
            throw new BusinessConflictException("该成员已报名，请勿重复报名");
        if (registrations.countByActivityId(id) >= activity.getCapacity())
            throw new BusinessConflictException("活动人数已满");
        return RegistrationView.from(registrations.saveAndFlush(
            new Registration(activity, request.memberNo(), request.memberName().strip())));
    }
    private Activity requireActivity(Long id) {
        return activities.findById(id).orElseThrow(() -> new NotFoundException("活动不存在: " + id));
    }
}
