package com.shifo.shifo_java.features.payment.dto;

import com.shifo.shifo_java.features.payment.model.PaymentKind;
import com.shifo.shifo_java.features.payment.model.PaymentStatus;
import com.shifo.shifo_java.features.payment.model.PaymentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePaymentDto {

    @Schema(description = "ID записи на прием (может быть изменен)")
    private Long appointmentId;

    @Schema(description = "ID пациента")
    private Long patientId;

    @DecimalMin(value = "1.00", message = "Сумма должна быть больше или равна 1")
    @Schema(description = "Сумма платежа")
    private BigDecimal amount;

    @Schema(description = "Тип платежной системы")
    private PaymentType paymentType;

    @Schema(description = "Категория платежа")
    private PaymentKind paymentKind;

    @Schema(description = "Статус платежа")
    private PaymentStatus status;

    @Schema(
            description = "Дата оплаты (ISO или YYYY-MM-DD)",
            example = "2025-12-04"
    )
    private Instant paidAt;
}
