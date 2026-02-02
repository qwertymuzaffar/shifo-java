package com.shifo.shifo_java.features.appointment.dto;


import com.shifo.shifo_java.common.dto.DateTimeDto;
import com.shifo.shifo_java.common.enums.AppointmentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.util.List;

@Getter
@Setter
public class CreateAppointmentDto {

    @Schema(description = "List of date/time slots")
    @NotEmpty(message = "Список дат и времени приёмов не может быть пустым")
    @Valid
    private List<DateTimeDto> datetimes;

    @Schema(description = "Patient ID", required = false)
    @Nullable
    @Min(value = 1, message = "ID пациента должен быть числом")
    private Integer patientId;

    @Schema(description = "Doctor ID")
    @NotNull(message = "ID врача не может быть пустым")
    @Min(value = 1, message = "ID врача должен быть числом")
    private Integer doctorId;

    @Schema(description = "Appointment duration", example = "30")
    @NotNull(message = "Продолжительность приёма не может быть пустой")
    @Min(value = 1, message = "Продолжительность должна быть числом")
    private Integer duration;

    @Schema(description = "Notes", required = false)
    @Nullable
    private String notes;

    @Schema(description = "Symptoms", required = false)
    @Nullable
    private String symptoms;

    @Schema(description = "Appointment type", required = false)
    @Nullable
    private AppointmentType type;

    @Schema(description = "Status", required = false)
    @Nullable
    private Integer status;

    @Schema(description = "Procedure IDs", required = false)
    @Nullable
    private List<Long> procedureIds;
}

