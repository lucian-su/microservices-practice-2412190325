package com.zjgsu.scy.monolith.controller;

import com.zjgsu.scy.monolith.dto.ActivityDtos.*;
import com.zjgsu.scy.monolith.service.ActivityService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {
    private final ActivityService service;
    public ActivityController(ActivityService service) { this.service = service; }
    @PostMapping
    public ResponseEntity<ActivityView> create(@Valid @RequestBody CreateActivity request) {
        ActivityView result = service.create(request);
        return ResponseEntity.created(URI.create("/api/activities/" + result.id())).body(result);
    }
    @GetMapping
    public List<ActivityView> list() { return service.list(); }
    @GetMapping("/{id}")
    public ActivityView get(@PathVariable Long id) { return service.get(id); }
    @PostMapping("/{id}/registrations")
    public ResponseEntity<RegistrationView> register(@PathVariable Long id,
                                                    @Valid @RequestBody Register request) {
        RegistrationView result = service.register(id, request);
        return ResponseEntity.created(URI.create("/api/activities/" + id + "/registrations/" + result.id()))
            .body(result);
    }
    @GetMapping("/{id}/registrations/{registrationId}")
    public RegistrationView registration(@PathVariable Long id, @PathVariable Long registrationId) {
        return service.registration(id, registrationId);
    }
    @GetMapping("/{id}/registrations")
    public List<RegistrationView> registrations(@PathVariable Long id) { return service.registrations(id); }
}
