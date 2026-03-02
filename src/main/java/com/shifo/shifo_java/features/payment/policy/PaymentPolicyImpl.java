package com.shifo.shifo_java.features.payment.policy;

import com.shifo.shifo_java.common.exceptions.BadRequestException;
import com.shifo.shifo_java.features.balance.BalanceService;
import com.shifo.shifo_java.features.payment.Payment;
import com.shifo.shifo_java.features.payment.context.PaymentContext;
import com.shifo.shifo_java.features.payment.dto.CreatePaymentDto;
import com.shifo.shifo_java.features.payment.model.PaymentKind;
import com.shifo.shifo_java.features.payment.model.PaymentStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class PaymentPolicyImpl implements PaymentPolicy {

    private final BalanceService balanceService;

    public PaymentPolicyImpl(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @Override
    public PaymentKind supports() {
        return PaymentKind.PAYMENT;
    }

    @Override
    public void validate(PaymentContext context) {
        if (context.getAppointment() == null) {
            throw new BadRequestException("Payment requires appointment");
        }
    }

    @Override
    public void enrich(PaymentContext context) {
        context.setStatus(PaymentStatus.PAID);
        context.setPaidAt(resolvePaidAt(context));
    }

    @Override
    public void applySideEffects(PaymentContext context) {
        // revenue recognition
        balanceService.recordPayment(context.getPayment());
    }

    private Instant resolvePaidAt(PaymentContext context) {
        return Optional.ofNullable(context.getDto().getPaidAt())
                .orElse(Instant.now());
    }
}
