package com.shifo.shifo_java.features.payment.policy;

import com.shifo.shifo_java.common.exceptions.BadRequestException;
import com.shifo.shifo_java.features.payment.context.PaymentContext;
import com.shifo.shifo_java.features.payment.model.PaymentKind;
import com.shifo.shifo_java.features.payment.model.PaymentStatus;
import org.springframework.stereotype.Component;

@Component
public class DebtPolicy implements PaymentPolicy {

    @Override
    public PaymentKind supports() {
        return PaymentKind.DEBT;
    }

    @Override
    public void validate(PaymentContext context) {
        if (context.getAppointment() == null) {
            throw new BadRequestException("Debt must be linked to appointment");
        }
    }

    @Override
    public void enrich(PaymentContext context) {
        context.setStatus(PaymentStatus.PENDING);
        context.setPaidAt(null);
    }

    @Override
    public void applySideEffects(PaymentContext context) {
        // intentionally empty
    }
}
