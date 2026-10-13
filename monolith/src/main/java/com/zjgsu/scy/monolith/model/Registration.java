package com.zjgsu.scy.monolith.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registrations", uniqueConstraints =
    @UniqueConstraint(name = "uk_activity_member", columnNames = {"activity_id", "member_no"}))
public class Registration {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;
    @Column(name = "member_no", nullable = false, length = 30)
    private String memberNo;
    @Column(nullable = false, length = 50)
    private String memberName;
    @Column(nullable = false)
    private LocalDateTime registeredAt;

    protected Registration() {}
    public Registration(Activity activity, String memberNo, String memberName) {
        this.activity = activity; this.memberNo = memberNo;
        this.memberName = memberName; this.registeredAt = LocalDateTime.now();
    }
    public Long getId() { return id; }
    public Activity getActivity() { return activity; }
    public String getMemberNo() { return memberNo; }
    public String getMemberName() { return memberName; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
}
