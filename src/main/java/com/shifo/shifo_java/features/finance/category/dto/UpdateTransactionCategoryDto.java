package com.shifo.shifo_java.features.finance.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTransactionCategoryDto {

    @Schema(
            description = "Название категории",
            example = "Зарплата",
            maxLength = 100
    )
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Schema(
            description = "Описание категории",
            example = "Выплаты заработной платы сотрудникам"
    )
    private String description;

    @Schema(
            description = "Активна ли категория",
            example = "true"
    )
    private Boolean isActive;

    @Schema(
            description = "Порядок сортировки",
            example = "1"
    )
    private Integer sortOrder;
}
