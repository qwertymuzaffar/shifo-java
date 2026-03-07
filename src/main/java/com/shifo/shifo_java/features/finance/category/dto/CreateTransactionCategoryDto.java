package com.shifo.shifo_java.features.finance.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTransactionCategoryDto {

    @Schema(
            description = "Название категории",
            example = "Зарплата",
            maxLength = 100
    )
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Schema(
            description = "Описание категории",
            example = "Выплаты заработной платы сотрудникам",
            nullable = true
    )
    private String description;

    @Schema(
            description = "Активна ли категория",
            example = "true",
            defaultValue = "true",
            nullable = true
    )
    private Boolean isActive = true;

    @Schema(
            description = "Порядок сортировки",
            example = "1",
            defaultValue = "0",
            nullable = true
    )
    private Integer sortOrder = 0;
}
