package com.shifo.shifo_java.features.appointment.domain;

import com.shifo.shifo_java.features.appointment.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class AppointmentAvailabilityChecker {

    private final AppointmentRepository appointmentRepository;

    public void ensureAvailable(
            Long doctorId,
            Long patientId,
            LocalDate date,
            LocalTime time,
            Integer duration
    ) {
        LocalTime endTime = time.plusMinutes(duration);

        if (appointmentRepository.existsDoctorOverlap(doctorId, date, time, endTime) == 1) {
            throw new IllegalStateException("Doctor already booked");
        }

        if (patientId != null &&
                appointmentRepository.existsPatientOverlap(patientId, date, time, endTime) == 1) {
            throw new IllegalStateException("Patient already booked");
        }
    }
}
