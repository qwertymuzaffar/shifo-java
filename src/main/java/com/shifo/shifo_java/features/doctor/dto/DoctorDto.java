package com.shifo.shifo_java.features.doctor.dto;

import com.shifo.shifo_java.features.specialization.Specialization;
import com.shifo.shifo_java.features.specialization.dto.SpecializationDto;
import com.shifo.shifo_java.features.user.User;
import com.shifo.shifo_java.features.user.dto.UserDto;
import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DoctorDto {

    private Long id;

    private UserDto user;

    private Boolean isActive;

    private Integer status;

    private SpecializationDto specialization;

    private Integer experience;

    private Integer consultationFee;

    private WorkingHoursDto workingHours;

    // Computed fields (matching @Transient fields)
    private String fullName;
    private String firstName;
    private String lastName;

    private Instant createdAt;

    private Instant updatedAt;
}
