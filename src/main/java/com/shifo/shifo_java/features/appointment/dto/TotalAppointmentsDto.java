package com.shifo.shifo_java.features.appointment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TotalAppointmentsDto {
    private long total;
    private String growth;
}
