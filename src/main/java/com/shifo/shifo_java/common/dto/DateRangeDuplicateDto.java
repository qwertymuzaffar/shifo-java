package com.shifo.shifo_java.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DateRangeDuplicateDto {

    @Schema(description = "Copy from date (YYYY-MM-DD)", example = "2024-07-01")
    @NotBlank(message = "Дата должна быть строкой")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Дата должна быть в формате YYYY-MM-DD")
    private String copyDate;

    @Schema(description = "Copy to date (YYYY-MM-DD)", example = "2024-07-05")
    @NotBlank(message = "Дата должна быть строкой")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Дата должна быть в формате YYYY-MM-DD")
    private String dateTo;
}

