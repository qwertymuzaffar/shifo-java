package com.shifo.shifo_java.features.balance;

import com.shifo.shifo_java.features.balance.model.EntityType;
import com.shifo.shifo_java.features.finance.transaction.Transaction;
import com.shifo.shifo_java.features.finance.transaction.model.TransactionType;
import com.shifo.shifo_java.features.payment.Payment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;

@Component
public class BalanceMapper {

    public Balance fromPayment(Payment payment) {

        return Balance.builder()
                .entityId(payment.getId())
                .entityType(EntityType.PAYMENT)
                .amount(resolveSignedAmount(payment))
                .paymentMethod(payment.getPaymentType())
                .createdAt(Instant.now())
                .build();
    }

    public Balance fromTransaction(Transaction transaction) {

        BigDecimal amount = transaction.getAmount().abs();

        BigDecimal signedAmount =
                transaction.getType() == TransactionType.EXPENSE
                        ? amount.negate()
                        : amount;

        return Balance.builder()
                .entityId(transaction.getId())
                .entityType(EntityType.TRANSACTION)
                .amount(signedAmount)
                .paymentMethod(transaction.getPaymentMethod())
                .createdAt(Instant.now())
                .build();
    }

    private BigDecimal resolveSignedAmount(Payment payment) {

        return switch (payment.getPaymentKind()) {
            case PREPAYMENT, DEBT_PAYMENT -> payment.getAmount();
            case DEBT, BALANCE_DEDUCTION -> payment.getAmount().negate();
            default -> BigDecimal.ZERO;
        };
    }
}
