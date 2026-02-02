package com.shifo.shifo_java.features.appointment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentTypeCountDto {
    private long consultation;
    private long followup;
    private long procedure;
    private long emergency;
}
