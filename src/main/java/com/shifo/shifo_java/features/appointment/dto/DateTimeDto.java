package com.shifo.shifo_java.features.appointment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class DateTimeDto {

    @NotNull(message = "Дата приёма не может быть пустой")
    @Schema(description = "Дата приёма", example = "2023-08-01")
    private LocalDate date;

    @NotNull(message = "Время приёма не может быть пустым")
    @Schema(description = "Время приёма", example = "08:00")
    private LocalTime time;

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }
}

