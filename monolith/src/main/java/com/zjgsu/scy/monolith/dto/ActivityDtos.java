package com.zjgsu.scy.monolith.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import com.zjgsu.scy.monolith.model.*;

public final class ActivityDtos {
    private ActivityDtos() {}
    public record CreateActivity(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 120) String location,
        @NotNull @Future LocalDateTime startsAt,
        @Min(1) @Max(200) int capacity) {}
    public record Register(
        @NotBlank @Pattern(regexp = "[A-Za-z0-9_-]{1,30}") String memberNo,
        @NotBlank @Size(max = 50) String memberName) {}
    public record ActivityView(Long id, String name, String location,
                               LocalDateTime startsAt, int capacity) {
        public static ActivityView from(Activity a) {
            return new ActivityView(a.getId(), a.getName(), a.getLocation(), a.getStartsAt(), a.getCapacity());
        }
    }
    public record RegistrationView(Long id, Long activityId, String memberNo,
                                   String memberName, LocalDateTime registeredAt) {
        public static RegistrationView from(Registration r) {
            return new RegistrationView(r.getId(), r.getActivity().getId(), r.getMemberNo(),
                                        r.getMemberName(), r.getRegisteredAt());
        }
    }
}
