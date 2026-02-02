package com.shifo.shifo_java.features.doctor.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterDoctorDto {

    // Поиск по телефону
    private String search;

    // ID специализации
    @Positive(message = "specializationId must be a positive number")
    private Long specializationId;

    // ID комнаты
    @Positive(message = "roomId must be a positive number")
    private Long roomId;

    // Фильтр по активности
    private Boolean isActive;

    // Pagination
    @Min(value = 1, message = "page must be >= 1")
    private Integer page = 1;

    @Min(value = 1, message = "limit must be >= 1")
    private Integer limit = 10;
}

