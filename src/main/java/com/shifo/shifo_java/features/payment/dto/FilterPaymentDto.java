package com.shifo.shifo_java.features.payment.dto;

import com.shifo.shifo_java.common.dto.PaginationDto;
import com.shifo.shifo_java.features.payment.model.PaymentKind;
import com.shifo.shifo_java.features.payment.model.PaymentStatus;
import com.shifo.shifo_java.features.payment.model.PaymentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FilterPaymentDto extends PaginationDto {

    @Schema(description = "ID записи на прием")
    private Long appointmentId;

    @Schema(description = "ID пациента")
    private Long patientId;

    @Schema(description = "Тип платежной системы", example = "dc")
    private PaymentType paymentType;

    @Schema(description = "Статус платежа", example = "paid")
    private PaymentStatus status;

    @Schema(description = "Категория платежа", example = "payment")
    private PaymentKind paymentKind;

    @Schema(description = "Поиск")
    private String search;

    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "dateFrom должен быть в формате YYYY-MM-DD"
    )
    @Schema(description = "Дата с (YYYY-MM-DD)", example = "2025-01-01")
    private String dateFrom;

    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "dateTo должен быть в формате YYYY-MM-DD"
    )
    @Schema(description = "Дата по (YYYY-MM-DD)", example = "2025-01-31")
    private String dateTo;
}
