package com.shifo.shifo_java.features.appointment.application.command;

import com.shifo.shifo_java.features.appointment.model.AppointmentType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record CreateAppointmentCommand(
        Long doctorId,
        Long patientId,
        LocalDate date,
        LocalTime time,
        Integer duration,
        AppointmentType type,
        List<Long> procedureIds
) {}
