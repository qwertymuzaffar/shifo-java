package com.shifo.shifo_java.features.doctor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DoctorUpdateFieldsDto {

    @Schema(description = "ID специализации врача")
    private Long specializationId;

    @Schema(description = "ID комнаты врача")
    private Long roomId;

    @Schema(description = "Опыт работы врача")
    private Integer experience;

    @Schema(description = "Стоимость консультации врача")
    private BigDecimal consultationFee;

    @Valid
    @Schema(description = "Рабочие часы врача")
    private WorkingHoursDto workingHours;
}

