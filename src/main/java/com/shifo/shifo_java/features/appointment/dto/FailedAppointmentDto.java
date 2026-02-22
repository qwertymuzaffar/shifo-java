package com.shifo.shifo_java.features.appointment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class FailedAppointmentDto {

    private LocalDate date;
    private LocalTime time;
    private String reason;
}

