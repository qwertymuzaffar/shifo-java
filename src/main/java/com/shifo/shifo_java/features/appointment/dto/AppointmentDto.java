package com.shifo.shifo_java.features.appointment.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.shifo.shifo_java.features.appointment.model.AppointmentStatus;
import com.shifo.shifo_java.features.appointment.model.AppointmentType;
import com.shifo.shifo_java.features.doctor.dto.DoctorDto;
import com.shifo.shifo_java.features.patient.dto.PatientDto;
import com.shifo.shifo_java.features.procedure.dto.ProcedureDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDto {

    private Long id;

    private LocalTime time;

    private LocalDate date;

    private DoctorDto doctor;

    private PatientDto patient;

    private Integer duration;

    private String notes;

    private String symptoms;

    private AppointmentStatus status;

    private AppointmentType type;

    private List<ProcedureDto> procedures = new ArrayList<>();

    private String cancellationReason;

    private Instant createdAt;

    private Instant updatedAt;
}


