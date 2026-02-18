package com.shifo.shifo_java.features.doctor.dto;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateDoctorDto {

    // ---------------- USER FIELDS ----------------

    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String password;

    // add any other updatable User fields here


    // ---------------- DOCTOR FIELDS ----------------

    private Long specializationId;
    private Long roomId;
    private Integer experience;
    private Integer consultationFee;

    @Valid
    private WorkingHoursDto workingHours;
}
