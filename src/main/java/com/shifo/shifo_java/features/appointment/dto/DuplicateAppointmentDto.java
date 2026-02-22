package com.shifo.shifo_java.features.appointment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class DuplicateAppointmentDto {

    @NotNull
    @Schema(example = "2024-07-01", description = "Новая дата")
    private LocalDate date;

    @NotNull
    @Schema(example = "10:00", description = "Новое время")
    private LocalTime time;
}

