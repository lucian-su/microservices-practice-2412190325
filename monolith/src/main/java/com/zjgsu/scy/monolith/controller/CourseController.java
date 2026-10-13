package com.zjgsu.scy.monolith.controller;
import com.zjgsu.scy.monolith.service.CourseService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseService service;
    public CourseController(CourseService service) { this.service = service; }
    @GetMapping
    public List<CourseService.CourseView> list() { return service.list(); }
}
