package com.shifo.shifo_java.features.appointment.dto;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AppointmentDto {

    private Long id;

    private LocalTime time;

    private LocalDate date;

    private Long doctorId;

    private Long patientId;

    private Integer duration;

    private String notes;

    private String symptoms;

    private String status;  // AppointmentStatus as STRING

    private String type;    // AppointmentType as STRING

    private List<Long> procedureIds;  // IDs only for simplicity

    private String cancellationReason;

    private Instant createdAt;

    private Instant updatedAt;
}

