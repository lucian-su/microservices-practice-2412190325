package com.zjgsu.scy.monolith.repository;
import com.zjgsu.scy.monolith.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CourseRepository extends JpaRepository<Course, Long> {}
