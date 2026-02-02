package com.shifo.shifo_java.features.patient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TotalPatientsDto {
    private long total;
    private String growth;
}
