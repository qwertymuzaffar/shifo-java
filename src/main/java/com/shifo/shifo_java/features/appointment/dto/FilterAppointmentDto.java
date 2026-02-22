package com.shifo.shifo_java.features.appointment.dto;

import com.shifo.shifo_java.common.dto.PaginationDto;
import com.shifo.shifo_java.features.appointment.enums.AppointmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Schema(description = "Фильтр для поиска приёмов")
public class FilterAppointmentDto extends PaginationDto {

    @Schema(
            description = "Фильтр по нескольким ID врачей",
            example = "[1,2,3]"
    )
    private List<Long> doctorIds;

    @Schema(description = "Фильтр по ID пациента")
    private Long patientId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "Дата начала диапазона", example = "2024-07-01")
    private LocalDate dateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "Дата конца диапазона", example = "2024-07-05")
    private LocalDate dateTo;

    @Schema(description = "Поиск по ФИО пациента или имени врача")
    private String search;

    @Schema(description = "Фильтр по статусу")
    private AppointmentStatus status;

    @Schema(description = "Показать только предстоящие записи")
    private Boolean upcoming;
}


