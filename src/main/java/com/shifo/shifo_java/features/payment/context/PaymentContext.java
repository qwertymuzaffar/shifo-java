package com.shifo.shifo_java.features.payment.context;

import com.shifo.shifo_java.features.appointment.Appointment;
import com.shifo.shifo_java.features.patient.Patient;
import com.shifo.shifo_java.features.payment.Payment;
import com.shifo.shifo_java.features.payment.dto.CreatePaymentDto;
import com.shifo.shifo_java.features.payment.model.PaymentKind;
import com.shifo.shifo_java.features.payment.model.PaymentStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@RequiredArgsConstructor
public class PaymentContext {
    private final CreatePaymentDto dto;
    private final PaymentKind kind;
    private final Appointment appointment;
    private final Patient patient;

    @Setter
    private Long userId;
    @Setter
    private PaymentStatus status;
    @Setter
    private Instant paidAt;
    @Setter
    private Payment payment;
}
