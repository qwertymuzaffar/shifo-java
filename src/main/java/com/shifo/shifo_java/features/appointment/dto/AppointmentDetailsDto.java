package com.shifo.shifo_java.features.appointment.dto;

import com.shifo.shifo_java.features.appointment.model.AppointmentStatus;
import com.shifo.shifo_java.features.appointment.model.AppointmentType;
import com.shifo.shifo_java.features.doctor.dto.DoctorDto;
import com.shifo.shifo_java.features.patient.dto.PatientDto;
import com.shifo.shifo_java.features.payment.dto.PaymentDto;
import com.shifo.shifo_java.features.procedure.dto.ProcedureDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDetailsDto {

    private Long id;
    private LocalDate date;
    private LocalTime time;
    private Integer duration;
    private AppointmentStatus status;
    private AppointmentType type;

    private String notes;
    private String symptoms;
    private String cancellationReason;

    private DoctorDto doctor;
    private PatientDto patient;

    private List<ProcedureDto> procedures = new ArrayList<>();
    private List<PaymentDto> payments = new ArrayList<>();

    private BigDecimal totalPaymentAmount;

    private Instant createdAt;
    private Instant updatedAt;
}
