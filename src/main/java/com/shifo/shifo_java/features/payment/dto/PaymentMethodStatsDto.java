package com.shifo.shifo_java.features.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMethodStatsDto {
    private String type;
    private long count;
    private BigDecimal amount;
}
