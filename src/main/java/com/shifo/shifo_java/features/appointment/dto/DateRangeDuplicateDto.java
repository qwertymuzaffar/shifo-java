package com.shifo.shifo_java.features.appointment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class DateRangeDuplicateDto {

    @NotNull
    @Schema(example = "2024-07-01", description = "Дата копирования с")
    private LocalDate copyDate;

    @NotNull
    @Schema(example = "2024-07-05", description = "Дата копирования до")
    private LocalDate dateTo;

    @Schema(description = "ID приёмов для копирования")
    private List<Long> appointmentIds;
}

