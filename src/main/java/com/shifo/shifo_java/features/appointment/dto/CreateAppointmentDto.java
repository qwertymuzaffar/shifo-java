package com.shifo.shifo_java.features.appointment.dto;

import com.shifo.shifo_java.features.appointment.model.AppointmentStatus;
import com.shifo.shifo_java.features.appointment.model.AppointmentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateAppointmentDto {

    @NotEmpty(message = "Список дат и времени приёмов не может быть пустым")
    @Valid
    @Schema(description = "Список дат и времени приёмов")
    private List<DateTimeDto> datetimes;

    @Schema(description = "ID пациента")
    private Long patientId;

    @NotNull(message = "ID врача не может быть пустым")
    @Schema(description = "ID врача", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long doctorId;

    @NotNull(message = "Продолжительность приёма не может быть пустой")
    @Min(value = 1, message = "Продолжительность должна быть больше 0")
    @Schema(description = "Продолжительность приёма", example = "30")
    private Integer duration;

    @Schema(description = "Заметки о приёме")
    private String notes;

    @Schema(description = "Симптомы")
    private String symptoms;

    @Schema(description = "Тип приёма")
    private AppointmentType type = AppointmentType.CONSULTATION;

    @Schema(description = "Статус приёма")
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    @Schema(description = "ID процедур", example = "[1,2,3]")
    private List<Long> procedureIds;
}


