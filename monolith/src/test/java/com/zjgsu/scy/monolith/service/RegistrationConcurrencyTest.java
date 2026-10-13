package com.zjgsu.scy.monolith.service;

import com.zjgsu.scy.monolith.dto.ActivityDtos.*;
import com.zjgsu.scy.monolith.exception.BusinessConflictException;
import com.zjgsu.scy.monolith.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RegistrationConcurrencyTest {
    @Autowired ActivityService service;
    @Autowired RegistrationRepository registrations;
    @Autowired ActivityRepository activities;
    @Test void concurrentRequestsCannotOverbookLastSeat() throws Exception {
        Long id = service.create(new CreateActivity("最后一个名额", "活动室", LocalDateTime.now().plusDays(1), 1)).id();
        try (var executor = Executors.newFixedThreadPool(2)) {
            CountDownLatch ready = new CountDownLatch(2);
            CountDownLatch start = new CountDownLatch(1);
            Callable<Boolean> first = attempt(id, "first", ready, start);
            Callable<Boolean> second = attempt(id, "second", ready, start);
            Future<Boolean> a = executor.submit(first);
            Future<Boolean> b = executor.submit(second);
            try {
                assertTrue(ready.await(5, TimeUnit.SECONDS));
            } finally {
                start.countDown();
            }
            int successes = (a.get(10, TimeUnit.SECONDS) ? 1 : 0) + (b.get(10, TimeUnit.SECONDS) ? 1 : 0);
            assertEquals(1, successes);
            assertEquals(1, registrations.countByActivityId(id));
        } finally {
            registrations.deleteAll(registrations.findByActivityIdOrderByIdAsc(id));
            activities.deleteById(id);
        }
    }
    private Callable<Boolean> attempt(Long id, String member, CountDownLatch ready, CountDownLatch start) {
        return () -> {
            ready.countDown();
            if (!start.await(5, TimeUnit.SECONDS)) throw new IllegalStateException("timeout");
            try {
                service.register(id, new Register(member, member));
                return true;
            } catch (BusinessConflictException expected) {
                assertEquals("活动人数已满", expected.getMessage());
                return false;
            }
        };
    }
}
