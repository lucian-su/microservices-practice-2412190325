package com.zjgsu.scy.monolith.repository;

import com.zjgsu.scy.monolith.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ActivityRepositoryTest {
    @Autowired ActivityRepository activities;
    @Autowired RegistrationRepository registrations;
    @Autowired CourseRepository courses;
    @Autowired EntityManager entityManager;
    @Test void savesAndReloadsActivityAndRegistration() {
        Activity a = activities.save(new Activity("桌游之夜", "活动室", LocalDateTime.now().plusDays(1), 2));
        registrations.saveAndFlush(new Registration(a, "2412190325", "施晨煜"));
        entityManager.clear();
        assertEquals("桌游之夜", activities.findById(a.getId()).orElseThrow().getName());
        var r = registrations.findByActivityIdOrderByIdAsc(a.getId()).getFirst();
        assertEquals("施晨煜", r.getMemberName());
        assertEquals(a.getId(), r.getActivity().getId());
        assertEquals(1, registrations.countByActivityId(a.getId()));
    }
    @Test void databaseRejectsDuplicateMember() {
        Activity a = activities.save(new Activity("狼人杀", "活动室", LocalDateTime.now().plusDays(1), 3));
        registrations.saveAndFlush(new Registration(a, "same", "成员甲"));
        assertThrows(DataIntegrityViolationException.class,
            () -> registrations.saveAndFlush(new Registration(a, "same", "成员乙")));
    }
    @Test void savesAndReadsCourseForClassPractice() {
        Course course = courses.saveAndFlush(new Course("MS04", "微服务架构与实践"));
        entityManager.clear();
        assertEquals("微服务架构与实践", courses.findById(course.getId()).orElseThrow().getName());
    }
}
