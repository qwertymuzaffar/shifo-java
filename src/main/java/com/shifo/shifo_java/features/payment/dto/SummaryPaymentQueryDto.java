package com.shifo.shifo_java.features.payment.dto;

import com.shifo.shifo_java.features.payment.model.PaymentKind;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SummaryPaymentQueryDto {

    @Schema(description = "ID пациента", example = "123")
    private Long patientId;

    @Schema(description = "Имя пациента (полное или часть)", example = "Анвар")
    private String patientName;

    @Schema(description = "Категория платежа", example = "payment")
    private PaymentKind paymentKind;

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
