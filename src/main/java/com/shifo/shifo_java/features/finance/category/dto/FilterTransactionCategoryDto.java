package com.shifo.shifo_java.features.finance.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterTransactionCategoryDto {

    @Schema(
            description = "Фильтр по активности категории",
            example = "true"
    )
    private Boolean isActive;

    @Schema(
            description = "Поиск по названию категории",
            example = "зарплата"
    )
    private String search;

    @Schema(
            description = "Номер страницы",
            example = "1",
            defaultValue = "1"
    )
    @Min(value = 1, message = "Page must be at least 1")
    private Integer page = 1;

    @Schema(
            description = "Количество записей на странице",
            example = "10",
            defaultValue = "10"
    )
    @Min(value = 1, message = "Limit must be at least 1")
    private Integer limit = 10;

    // ---- Defensive null handling (important) ----
    public Integer getPage() {
        return page == null ? 1 : page;
    }

    public Integer getLimit() {
        return limit == null ? 10 : limit;
    }
}
