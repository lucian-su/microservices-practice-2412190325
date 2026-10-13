package com.zjgsu.scy.monolith.service;
import com.zjgsu.scy.monolith.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
public class CourseService {
    private final CourseRepository courses;
    public CourseService(CourseRepository courses) { this.courses = courses; }
    public record CourseView(Long id, String code, String name) {}
    @Transactional(readOnly = true)
    public List<CourseView> list() {
        return courses.findAll().stream()
            .map(c -> new CourseView(c.getId(), c.getCode(), c.getName())).toList();
    }
}
