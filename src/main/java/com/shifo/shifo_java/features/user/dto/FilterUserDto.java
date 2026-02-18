package com.shifo.shifo_java.features.user.dto;

import com.shifo.shifo_java.common.dto.PaginationDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterUserDto extends PaginationDto {

    @Schema(description = "Поиск по имени")
    private String search;

    @Schema(description = "Поиск по роли")
    @Min(value = 1, message = "roleId должен быть больше 0")
    private Long roleId;

    @Schema(description = "Фильтр по статусу активности")
    private Boolean isActive;

    @Schema(description = "Поиск по email")
    private String email;
}


