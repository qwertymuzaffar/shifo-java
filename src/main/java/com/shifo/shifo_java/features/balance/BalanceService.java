package com.shifo.shifo_java.features.balance;

import com.shifo.shifo_java.features.balance.BalanceTransaction;
import com.shifo.shifo_java.features.balance.model.BalanceTransactionType;
import com.shifo.shifo_java.features.balance.model.EntityType;
import com.shifo.shifo_java.features.finance.transaction.Transaction;
import com.shifo.shifo_java.features.finance.transaction.model.TransactionType;
import com.shifo.shifo_java.features.payment.Payment;
import com.shifo.shifo_java.features.payment.model.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceRepository balanceRepository;

    @Transactional
    public void recordPayment(Payment payment) {

        // Only ledger PAID payments
        if (payment.getStatus() != PaymentStatus.PAID) {
            return;
        }

        // Prevent duplicate entries if request retried
        boolean alreadyRecorded =
                balanceRepository.existsByEntityTypeAndEntityId(
                        EntityType.PAYMENT,
                        payment.getId()
                );

        if (alreadyRecorded) {
            return;
        }

        BalanceTransaction tx = mapFromPayment(payment);

        balanceRepository.save(tx);
    }

    private BalanceTransaction mapFromPayment(Payment payment) {

        return BalanceTransaction.builder()
                .patientId(payment.getPatientId())
                .entityId(payment.getId())
                .entityType(EntityType.PAYMENT)
                .transactionType(resolveType(payment))
                .amount(resolveSignedAmount(payment))
                .paymentMethod(payment.getPaymentType())
                .createdAt(Instant.now())
                .build();
    }

    private BalanceTransactionType resolveType(Payment payment) {
        return switch (payment.getPaymentKind()) {
            case PREPAYMENT -> BalanceTransactionType.PREPAYMENT;
            case DEBT -> BalanceTransactionType.DEBT_CREATED;
            case DEBT_PAYMENT -> BalanceTransactionType.DEBT_PAYMENT;
            case BALANCE_DEDUCTION -> BalanceTransactionType.SERVICE_CHARGE;
            default -> BalanceTransactionType.ADJUSTMENT;
        };
    }

    private BigDecimal resolveSignedAmount(Payment payment) {
        return switch (payment.getPaymentKind()) {
            case PREPAYMENT, DEBT_PAYMENT -> payment.getAmount();
            case DEBT, BALANCE_DEDUCTION -> payment.getAmount().negate();
            default -> BigDecimal.ZERO;
        };
    }

    @Transactional
    public void handlePaymentStatusRemoved(Long paymentId) {

        balanceRepository
                .findByEntityIdAndEntityType(paymentId, EntityType.PAYMENT)
                .ifPresent(balanceRepository::delete);
    }

    @Transactional
    public BalanceTransaction handleTransactionStatusChange(Transaction transaction) {
        // For transactions, we consider them "paid" when they are created
        // Check if balance record already exists for this transaction
        BalanceTransaction existingBalance = balanceRepository
                .findByEntityIdAndEntityType(transaction.getId(), EntityType.TRANSACTION)
                .orElse(null);

        if (existingBalance == null) {
            // For expense transactions -> negative amount
            // For income transactions -> positive amount
            BigDecimal amount = transaction.getAmount().abs();

            BigDecimal balanceAmount = transaction.getType() == TransactionType.EXPENSE
                    ? amount.negate()
                    : amount;

            BalanceTransaction balance = BalanceTransaction.builder()
                    .amount(balanceAmount)
                    .entityId(transaction.getId())
                    .entityType(EntityType.TRANSACTION)
                    .paymentMethod(transaction.getPaymentMethod())
                    .build();

            return balanceRepository.save(balance);
        }

        return null;
    }
}
