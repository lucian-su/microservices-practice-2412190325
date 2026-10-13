package com.zjgsu.scy.monolith.service;

import com.zjgsu.scy.monolith.dto.ActivityDtos.*;
import com.zjgsu.scy.monolith.exception.*;
import com.zjgsu.scy.monolith.model.Activity;
import com.zjgsu.scy.monolith.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ActivityServiceTest {
    @Autowired ActivityService service;
    @Autowired ActivityRepository activities;
    @Autowired RegistrationRepository registrations;
    private Long create(int capacity) {
        return service.create(new CreateActivity("桌游之夜", "活动室", LocalDateTime.now().plusDays(1), capacity)).id();
    }
    @Test void registrationSucceeds() {
        Long id = create(2);
        assertEquals("one", service.register(id, new Register("one", "成员甲")).memberNo());
        assertEquals(1, registrations.countByActivityId(id));
    }
    @Test void rejectsDuplicateAndLeavesCountUnchanged() {
        Long id = create(3);
        service.register(id, new Register("one", "成员甲"));
        var error = assertThrows(BusinessConflictException.class,
            () -> service.register(id, new Register("one", "成员甲")));
        assertTrue(error.getMessage().contains("重复"));
        assertEquals(1, registrations.countByActivityId(id));
    }
    @Test void rejectsFullActivity() {
        Long id = create(1);
        service.register(id, new Register("one", "成员甲"));
        assertEquals("活动人数已满", assertThrows(BusinessConflictException.class,
            () -> service.register(id, new Register("two", "成员乙"))).getMessage());
        assertEquals(1, registrations.countByActivityId(id));
    }
    @Test void rejectsStartedActivity() {
        Activity a = activities.saveAndFlush(new Activity("过去的活动", "活动室", LocalDateTime.now().minusDays(1), 2));
        assertThrows(BusinessConflictException.class, () -> service.register(a.getId(), new Register("one", "成员甲")));
    }
    @Test void rejectsMissingActivity() {
        assertThrows(NotFoundException.class, () -> service.register(-1L, new Register("one", "成员甲")));
    }
}
