package com.shifo.shifo_java.features.balance;

import com.shifo.shifo_java.features.balance.model.EntityType;
import com.shifo.shifo_java.features.finance.transaction.Transaction;
import com.shifo.shifo_java.features.patient.Patient;
import com.shifo.shifo_java.features.patient.PatientRepository;
import com.shifo.shifo_java.features.payment.Payment;
import com.shifo.shifo_java.features.payment.model.PaymentKind;
import com.shifo.shifo_java.features.payment.model.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Transactional
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;
    private final PatientRepository patientRepository;

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

    public void reversePayment(Payment payment) {

        if (payment.getStatus() != PaymentStatus.PAID) {
            return;
        }

        PaymentKind kind = payment.getPaymentKind();

        if (!affectsBalance(kind)) {
            return;
        }

        Long targetPatientId =
                payment.getPatientId() != null
                        ? payment.getPatientId()
                        : payment.getAppointment() != null
                        ? payment.getAppointment().getPatient().getId()
                        : null;

        if (targetPatientId == null) {
            return;
        }

        Patient patient = patientRepository.findById(targetPatientId).orElse(null);

        if (patient == null) {
            return;
        }

        BigDecimal currentBalance = patient.getBalance() != null
                ? patient.getBalance()
                : BigDecimal.ZERO;

        BigDecimal delta = (kind == PaymentKind.DEBT || kind == PaymentKind.BALANCE_DEDUCTION)
                ? payment.getAmount()
                : payment.getAmount().negate();

        patient.setBalance(currentBalance.add(delta).setScale(2, RoundingMode.HALF_UP));
        patientRepository.save(patient);
    }

    private boolean affectsBalance(PaymentKind kind) {
        return kind == PaymentKind.DEBT
                || kind == PaymentKind.PREPAYMENT
                || kind == PaymentKind.DEBT_PAYMENT
                || kind == PaymentKind.BALANCE_DEDUCTION;
    }

    private boolean exists(EntityType type, Long entityId) {
        return balanceRepository.existsByEntityTypeAndEntityId(type, entityId);
    }

    private boolean isPaid(Payment payment) {
        return payment.getStatus() == PaymentStatus.PAID;
    }
}
