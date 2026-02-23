package com.shifo.shifo_java.features.payment.dto;

import com.shifo.shifo_java.features.payment.model.PaymentKind;
import com.shifo.shifo_java.features.payment.model.PaymentType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodDto {

    @NotNull
    @DecimalMin(value = "1.00", message = "Сумма должна быть >= 1")
    private BigDecimal amount;

    @NotNull
    private PaymentType paymentType;

    private PaymentKind paymentKind = PaymentKind.PAYMENT;

    private Instant paidAt;
}
