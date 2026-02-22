package com.shifo.shifo_java.features.appointment.dto;

import com.shifo.shifo_java.features.appointment.model.AppointmentStatus;
import com.shifo.shifo_java.features.appointment.model.AppointmentType;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Schema(description = "DTO для обновления приёма (частичное обновление)")
public class UpdateAppointmentDto {

    @Schema(description = "Время приёма", example = "08:00")
    private LocalTime time;

    @Schema(description = "Дата приёма", example = "2023-08-01")
    private LocalDate date;

    @Schema(description = "ID пациента")
    private Long patientId;

    @Schema(description = "ID врача")
    private Long doctorId;

    @Min(value = 1, message = "Продолжительность должна быть больше 0")
    @Schema(description = "Продолжительность приёма", example = "30")
    private Integer duration;

    @Size(max = 5000, message = "Заметки слишком длинные")
    @Schema(description = "Заметки о приёме")
    private String notes;

    @Size(max = 5000, message = "Симптомы слишком длинные")
    @Schema(description = "Симптомы")
    private String symptoms;

    @Schema(description = "Тип приёма")
    private AppointmentType type;

    @Schema(
            description = "Статус приёма",
            example = "SCHEDULED"
    )
    private AppointmentStatus status;

    @Schema(
            description = "ID процедур",
            example = "[1,2,3]"
    )
    private List<Long> procedureIds;
}
