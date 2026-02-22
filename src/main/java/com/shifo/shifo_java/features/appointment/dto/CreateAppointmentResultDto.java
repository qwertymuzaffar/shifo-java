package com.shifo.shifo_java.features.appointment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CreateAppointmentResultDto {

    private List<AppointmentDto> successful;
    private List<FailedAppointmentDto> failed;
}

