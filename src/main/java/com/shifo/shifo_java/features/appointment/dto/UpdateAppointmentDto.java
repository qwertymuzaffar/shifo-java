package com.shifo.shifo_java.features.appointment.dto;


import com.shifo.shifo_java.common.enums.AppointmentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.util.List;

@Getter
@Setter
public class UpdateAppointmentDto {

    @Schema(description = "Appointment time", example = "08:00")
    @NotBlank(message = "Время приёма не может быть пустым")
    @Pattern(
            regexp = "^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$",
            message = "Время приёма должно быть в формате HH:MM"
    )
    private String time;

    @Schema(description = "Appointment date", example = "2023-08-01")
    @NotBlank(message = "Дата приёма не может быть пустой")
    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "Дата приёма должна быть в формате YYYY-MM-DD"
    )
    private String date;

    @Schema(description = "Patient ID")
    @NotNull(message = "ID пациента не может быть пустым")
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

