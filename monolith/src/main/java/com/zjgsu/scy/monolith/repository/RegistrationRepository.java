package com.zjgsu.scy.monolith.repository;

import com.zjgsu.scy.monolith.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    boolean existsByActivityIdAndMemberNo(Long activityId, String memberNo);
    long countByActivityId(Long activityId);
    List<Registration> findByActivityIdOrderByIdAsc(Long activityId);
}
