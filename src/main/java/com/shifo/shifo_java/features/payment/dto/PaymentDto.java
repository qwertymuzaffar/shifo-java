package com.shifo.shifo_java.features.payment.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentDto {

    private Long id;

    private Long appointmentId;

    private BigDecimal amount;

    private String paymentType;   // PaymentType enum as STRING

    private String status;        // PaymentStatus enum as STRING

    private String paymentKind;   // PaymentKind enum as STRING

    private Instant createdAt;

    private Instant paidAt;

    private Instant updatedAt;

    private Instant deletedAt;
}
