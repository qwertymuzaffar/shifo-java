package com.shifo.shifo_java.features.payment.dto;

import com.shifo.shifo_java.common.enums.PaymentStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePaymentStatusDto {

    @NotNull(message = "ID платежа обязателен")
    @Positive(message = "ID платежа должен быть положительным числом")
    private Long id;

    @NotNull(message = "Статус платежа обязателен")
    private PaymentStatus status;
}

