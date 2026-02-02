package com.shifo.shifo_java.features.doctor.dto;

import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DoctorDto {

    private Long id;

    private Long userId;

    private Boolean isActive;

    private Integer status;

    private Long specializationId;

    private Integer experience;

    private Integer consultationFee;

    // Store JSON string like {"start":"09:00","end":"17:00","workingDays":[1,2,3]}
    private String workingHours;

    // Computed fields (matching @Transient fields)
    private String fullName;
    private String firstName;
    private String lastName;

    private Instant createdAt;

    private Instant updatedAt;
}

