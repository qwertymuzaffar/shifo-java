package com.shifo.shifo_java.features.payment.policy;

import com.shifo.shifo_java.common.exceptions.BadRequestException;
import com.shifo.shifo_java.features.patient.Patient;
import com.shifo.shifo_java.features.payment.context.PaymentContext;
import com.shifo.shifo_java.features.payment.model.PaymentKind;
import com.shifo.shifo_java.features.payment.model.PaymentStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

@Component
public class BalanceDeductionPolicy implements PaymentPolicy {

    @Override
    public PaymentKind supports() {
        return PaymentKind.BALANCE_DEDUCTION;
    }

    @Override
    public void validate(PaymentContext context) {

        BigDecimal balance = Optional.ofNullable(context.getPatient().getBalance())
                .orElse(BigDecimal.ZERO);

        if (balance.compareTo(context.getDto().getAmount()) < 0) {
            throw new BadRequestException("Недостаточно средств");
        }
    }

    @Override
    public void enrich(PaymentContext context) {
        context.setStatus(
                Optional.ofNullable(context.getDto().getStatus())
                        .orElse(PaymentStatus.PENDING)
        );

        if (context.getStatus() == PaymentStatus.PAID) {
            context.setPaidAt(
                    Optional.ofNullable(context.getDto().getPaidAt())
                            .orElse(Instant.now())
            );
        }
    }

    @Override
    public void applySideEffects(PaymentContext context) {
        if (context.getStatus() != PaymentStatus.PAID) return;

        Patient patient = context.getPatient();
        BigDecimal balance = Optional.ofNullable(patient.getBalance())
                .orElse(BigDecimal.ZERO);

        patient.setBalance(balance.subtract(context.getDto().getAmount()));
    }
}
