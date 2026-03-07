package com.shifo.shifo_java.features.finance.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCategoryDto {

    @Schema(description = "Category ID", example = "1")
    private Long id;

    @Schema(description = "Category name", example = "Salary")
    private String name;

    @Schema(description = "Category description", example = "Monthly salary payments")
    private String description;

    @Schema(description = "Is category active", example = "true")
    private Boolean isActive;

    @Schema(description = "Sorting order", example = "0")
    private Integer sortOrder;

    @Schema(description = "Creation timestamp")
    private Instant createdAt;

    @Schema(description = "Last update timestamp")
    private Instant updatedAt;
}
