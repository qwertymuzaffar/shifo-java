package com.shifo.shifo_java.features.payment.dto;

import com.shifo.shifo_java.features.payment.model.PaymentKind;
import com.shifo.shifo_java.features.payment.model.PaymentStatus;
import com.shifo.shifo_java.features.payment.model.PaymentType;
import com.shifo.shifo_java.features.payment.validation.PaymentCreateValidation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@PaymentCreateValidation
public class CreatePaymentDto {

    @Schema(description = "ID записи на прием (обязателен, кроме предоплаты)")
    private Long appointmentId;

    @Schema(description = "ID пациента (обязателен для предоплаты, если не указан appointmentId)")
    private Long patientId;

    @NotNull(message = "Сумма платежа обязательна")
    @DecimalMin(value = "1.00", message = "Сумма платежа должна быть больше или равна 1")
    @Schema(description = "Сумма платежа")
    private BigDecimal amount;

    @NotNull(message = "Тип платежа обязателен")
    @Schema(description = "Тип платежной системы")
    private PaymentType paymentType;

    @Schema(description = "Категория платежа")
    private PaymentKind paymentKind = PaymentKind.PAYMENT;

    @Schema(description = "Статус платежа")
    private PaymentStatus status = PaymentStatus.PENDING;

    @Schema(description = "Дата оплаты (ISO формат)")
    private Instant paidAt;
}
