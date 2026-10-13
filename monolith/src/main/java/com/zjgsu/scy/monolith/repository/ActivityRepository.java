package com.zjgsu.scy.monolith.repository;

import com.zjgsu.scy.monolith.model.Activity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    // Lock the activity row before checking capacity to serialize registrations.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Activity a where a.id = :id")
    Optional<Activity> findForRegistration(@Param("id") Long id);
}
