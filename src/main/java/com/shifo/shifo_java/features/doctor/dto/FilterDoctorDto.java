package com.shifo.shifo_java.features.doctor.dto;

import com.shifo.shifo_java.common.dto.PaginationDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Фильтр для поиска врачей")
public class FilterDoctorDto extends PaginationDto {

    @Schema(description = "Поиск по номеру телефона", example = "99890")
    private String search;

    @Schema(description = "ID специализации", example = "3")
    @Positive(message = "specializationId должен быть положительным числом")
    private Long specializationId;

    @Schema(description = "ID комнаты", example = "5")
    @Positive(message = "roomId должен быть положительным числом")
    private Long roomId;

    @Schema(description = "Фильтр по статусу активности", example = "true")
    private Boolean isActive;
}


