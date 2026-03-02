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
public class DebtPaymentPolicy implements PaymentPolicy {

    private final BalanceService balanceService;

    public DebtPaymentPolicy(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @Override
    public PaymentKind supports() {
        return PaymentKind.DEBT_PAYMENT;
    }

    @Override
    public void validate(PaymentContext context) {
        if (context.getPatient() == null) {
            throw new BadRequestException("Debt payment requires patient");
        }
    }

    @Override
    public void enrich(PaymentContext context) {
        context.setStatus(PaymentStatus.PAID);
        context.setPaidAt(resolvePaidAt(context));
    }

    @Override
    public void applySideEffects(PaymentContext context) {
        // closes the debt / counts as received payment
        balanceService.recordPayment(context.getPayment());
    }

    private Instant resolvePaidAt(PaymentContext context) {
        return Optional.ofNullable(context.getDto().getPaidAt())
                .orElse(Instant.now());
    }
}
