package com.shifo.shifo_java.features.balance;

import com.shifo.shifo_java.features.balance.model.EntityType;
import com.shifo.shifo_java.features.finance.transaction.Transaction;
import com.shifo.shifo_java.features.payment.Payment;
import com.shifo.shifo_java.features.payment.model.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;

    public void recordPayment(Payment payment) {

        if (!isPaid(payment)) {
            return;
        }

        if (exists(EntityType.PAYMENT, payment.getId())) {
            return;
        }

        Balance balance = balanceMapper.fromPayment(payment);
        balanceRepository.save(balance);
    }

    public void handlePaymentStatusRemoved(Long paymentId) {

        balanceRepository
                .findByEntityIdAndEntityType(paymentId, EntityType.PAYMENT)
                .ifPresent(balanceRepository::delete);
    }

    public void recordTransaction(Transaction transaction) {

        if (exists(EntityType.TRANSACTION, transaction.getId())) {
            return;
        }

        Balance balance = balanceMapper.fromTransaction(transaction);
        balanceRepository.save(balance);
    }

    private boolean exists(EntityType type, Long entityId) {
        return balanceRepository.existsByEntityTypeAndEntityId(type, entityId);
    }

    private boolean isPaid(Payment payment) {
        return payment.getStatus() == PaymentStatus.PAID;
    }
}
