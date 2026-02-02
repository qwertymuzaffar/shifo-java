package com.shifo.shifo_java.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DateTimeDto {

    @Schema(description = "Appointment date", example = "2023-08-01")
    @NotBlank(message = "Дата приёма не может быть пустой")
    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "Дата приёма должна быть в формате YYYY-MM-DD"
    )
    private String date;

    @Schema(description = "Appointment time", example = "08:00")
    @NotBlank(message = "Время приёма не может быть пустым")
    @Pattern(
            regexp = "^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$",
            message = "Время приёма должно быть в формате HH:MM"
    )
    private String time;
}

