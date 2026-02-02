package com.shifo.shifo_java.features.patient.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterPatientDto {

    // Search by full name or phone
    private String search;

    // Birth date range (ISO format YYYY-MM-DD)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private String birthDateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private String birthDateTo;

    // Pagination
    @Min(value = 1, message = "page must be >= 1")
    private Integer page = 1;

    @Min(value = 1, message = "limit must be >= 1")
    private Integer limit = 10;
}

