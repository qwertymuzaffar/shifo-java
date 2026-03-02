package com.shifo.shifo_java.features.payment.dto;

import com.shifo.shifo_java.features.patient.dto.PatientDto;
import com.shifo.shifo_java.features.payment.model.PaymentKind;
import com.shifo.shifo_java.features.payment.model.PaymentStatus;
import com.shifo.shifo_java.features.payment.model.PaymentType;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {

    private Long id;

    private Long appointmentId;

    private PatientDto patient;

    private Long userId;

    private BigDecimal amount;

    private PaymentType paymentType;

    private PaymentStatus status;

    private PaymentKind paymentKind;

    private Instant paidAt;

    private Instant createdAt;

    private Instant updatedAt;
}
