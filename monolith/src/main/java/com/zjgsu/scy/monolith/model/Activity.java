package com.zjgsu.scy.monolith.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activities")
public class Activity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String name;
    @Column(nullable = false, length = 120)
    private String location;
    @Column(nullable = false)
    private LocalDateTime startsAt;
    @Column(nullable = false)
    private int capacity;

    protected Activity() {}
    public Activity(String name, String location, LocalDateTime startsAt, int capacity) {
        this.name = name; this.location = location;
        this.startsAt = startsAt; this.capacity = capacity;
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public LocalDateTime getStartsAt() { return startsAt; }
    public int getCapacity() { return capacity; }
}
