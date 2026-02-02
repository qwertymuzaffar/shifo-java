package com.shifo.shifo_java.features.appointment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DuplicateAppointmentDto {

    @Schema(description = "New date (YYYY-MM-DD)", example = "2024-07-01")
    @NotBlank(message = "Дата должна быть строкой")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Дата должна быть в формате YYYY-MM-DD")
    private String date;

    @Schema(description = "New time (HH:mm)", example = "10:00")
    @NotBlank(message = "Время должно быть строкой")
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "Время должно быть в формате HH:mm")
    private String time;
}

