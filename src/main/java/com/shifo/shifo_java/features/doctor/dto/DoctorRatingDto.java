package com.shifo.shifo_java.features.doctor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorRatingDto {
    private String name;
    private String specialization;
    private long appointments;
    private long cancellations;
    private long completed;
    private String efficiency;
    private BigDecimal revenue;
}
