package com.shifo.shifo_java.features.payment.policy;

import com.shifo.shifo_java.common.exceptions.BadRequestException;
import com.shifo.shifo_java.features.patient.Patient;
import com.shifo.shifo_java.features.payment.Payment;
import com.shifo.shifo_java.features.payment.context.PaymentContext;
import com.shifo.shifo_java.features.payment.dto.CreatePaymentDto;
import com.shifo.shifo_java.features.payment.model.PaymentKind;
import com.shifo.shifo_java.features.payment.model.PaymentStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

@Component
public class PrepaymentPolicy implements PaymentPolicy {

    @Override
    public PaymentKind supports() {
        return PaymentKind.PREPAYMENT;
    }

    @Override
    public void validate(PaymentContext context) {
        if (context.getAppointment() != null) return;

        if (context.getDto().getPatientId() == null) {
            throw new BadRequestException("Для предоплаты необходимо указать пациента");
        }
    }

    @Override
    public void enrich(PaymentContext context) {
        context.setStatus(PaymentStatus.PAID);
        context.setPaidAt(
                Optional.ofNullable(context.getDto().getPaidAt())
                        .orElse(Instant.now())
        );
    }

    @Override
    public void applySideEffects(PaymentContext context) {
        Patient patient = context.getPatient();
        BigDecimal balance = Optional.ofNullable(patient.getBalance())
                .orElse(BigDecimal.ZERO);

        patient.setBalance(balance.add(context.getDto().getAmount()));
    }
}
