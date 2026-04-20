package com.shifo.shifo_java.features.balance;

import com.shifo.shifo_java.features.appointment.Appointment;
import com.shifo.shifo_java.features.balance.model.EntityType;
import com.shifo.shifo_java.features.finance.transaction.Transaction;
import com.shifo.shifo_java.features.patient.Patient;
import com.shifo.shifo_java.features.patient.PatientRepository;
import com.shifo.shifo_java.features.payment.Payment;
import com.shifo.shifo_java.features.payment.model.PaymentKind;
import com.shifo.shifo_java.features.payment.model.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

    @Mock private BalanceRepository balanceRepository;
    @Mock private BalanceMapper balanceMapper;
    @Mock private PatientRepository patientRepository;

    @InjectMocks private BalanceService balanceService;

    @Test
    void shouldSkipRecordingWhenPaymentNotPaid() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setStatus(PaymentStatus.PENDING);

        balanceService.recordPayment(payment);

        verify(balanceRepository, never()).save(any());
        verify(balanceMapper, never()).fromPayment(any());
    }

    @Test
    void shouldSkipRecordingWhenBalanceAlreadyExistsForPayment() {
        Payment payment = new Payment();
        payment.setId(7L);
        payment.setStatus(PaymentStatus.PAID);

        when(balanceRepository.existsByEntityTypeAndEntityId(EntityType.PAYMENT, 7L)).thenReturn(true);

        balanceService.recordPayment(payment);

        verify(balanceRepository, never()).save(any());
        verify(balanceMapper, never()).fromPayment(any());
    }

    @Test
    void shouldSaveBalanceWhenPaidAndNotExists() {
        Payment payment = new Payment();
        payment.setId(7L);
        payment.setStatus(PaymentStatus.PAID);

        Balance balance = new Balance();

        when(balanceRepository.existsByEntityTypeAndEntityId(EntityType.PAYMENT, 7L)).thenReturn(false);
        when(balanceMapper.fromPayment(payment)).thenReturn(balance);

        balanceService.recordPayment(payment);

        verify(balanceRepository).save(balance);
    }

    @Test
    void shouldDeleteBalanceWhenHandlingPaymentRemoval() {
        Balance balance = new Balance();

        when(balanceRepository.findByEntityIdAndEntityType(5L, EntityType.PAYMENT))
                .thenReturn(Optional.of(balance));

        balanceService.handlePaymentStatusRemoved(5L);

        verify(balanceRepository).delete(balance);
    }

    @Test
    void shouldDoNothingWhenBalanceMissingForPaymentRemoval() {
        when(balanceRepository.findByEntityIdAndEntityType(5L, EntityType.PAYMENT))
                .thenReturn(Optional.empty());

        balanceService.handlePaymentStatusRemoved(5L);

        verify(balanceRepository, never()).delete(any());
    }

    @Test
    void shouldSaveTransactionBalanceWhenNotExists() {
        Transaction tx = new Transaction();
        tx.setId(20L);
        Balance balance = new Balance();

        when(balanceRepository.existsByEntityTypeAndEntityId(EntityType.TRANSACTION, 20L)).thenReturn(false);
        when(balanceMapper.fromTransaction(tx)).thenReturn(balance);

        balanceService.recordTransaction(tx);

        verify(balanceRepository).save(balance);
    }

    @Test
    void shouldSkipTransactionBalanceWhenExists() {
        Transaction tx = new Transaction();
        tx.setId(20L);

        when(balanceRepository.existsByEntityTypeAndEntityId(EntityType.TRANSACTION, 20L)).thenReturn(true);

        balanceService.recordTransaction(tx);

        verify(balanceRepository, never()).save(any());
        verify(balanceMapper, never()).fromTransaction(any());
    }

    @Test
    void shouldSkipReverseWhenPaymentNotPaid() {
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentKind(PaymentKind.DEBT);

        balanceService.reversePayment(payment);

        verify(patientRepository, never()).findById(any());
        verify(patientRepository, never()).save(any());
    }

    @Test
    void shouldSkipReverseForPaymentKindThatDoesNotAffectBalance() {
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaymentKind(PaymentKind.PAYMENT);
        payment.setPatientId(3L);

        balanceService.reversePayment(payment);

        verify(patientRepository, never()).findById(any());
        verify(patientRepository, never()).save(any());
    }

    @Test
    void shouldSkipReverseWhenNoTargetPatient() {
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaymentKind(PaymentKind.DEBT);
        payment.setPatientId(null);
        payment.setAppointment(null);

        balanceService.reversePayment(payment);

        verify(patientRepository, never()).findById(any());
    }

    @Test
    void shouldFallBackToAppointmentPatientWhenPatientIdMissing() {
        Patient patient = new Patient();
        patient.setId(9L);
        patient.setBalance(new BigDecimal("100.00"));

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);

        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaymentKind(PaymentKind.DEBT);
        payment.setPatientId(null);
        payment.setAppointment(appointment);
        payment.setAmount(new BigDecimal("30.00"));

        when(patientRepository.findById(9L)).thenReturn(Optional.of(patient));

        balanceService.reversePayment(payment);

        assertThat(patient.getBalance()).isEqualByComparingTo("130.00");
        verify(patientRepository).save(patient);
    }

    @Test
    void shouldSkipReverseWhenPatientNotFound() {
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaymentKind(PaymentKind.DEBT);
        payment.setPatientId(5L);
        payment.setAmount(new BigDecimal("10.00"));

        when(patientRepository.findById(5L)).thenReturn(Optional.empty());

        balanceService.reversePayment(payment);

        verify(patientRepository, never()).save(any());
    }

    @Test
    void shouldAddAmountBackForDebt() {
        Patient patient = new Patient();
        patient.setId(5L);
        patient.setBalance(new BigDecimal("50.00"));

        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaymentKind(PaymentKind.DEBT);
        payment.setPatientId(5L);
        payment.setAmount(new BigDecimal("25.00"));

        when(patientRepository.findById(5L)).thenReturn(Optional.of(patient));

        balanceService.reversePayment(payment);

        assertThat(patient.getBalance()).isEqualByComparingTo("75.00");
        verify(patientRepository).save(patient);
    }

    @Test
    void shouldAddAmountBackForBalanceDeduction() {
        Patient patient = new Patient();
        patient.setId(5L);
        patient.setBalance(new BigDecimal("50.00"));

        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaymentKind(PaymentKind.BALANCE_DEDUCTION);
        payment.setPatientId(5L);
        payment.setAmount(new BigDecimal("20.00"));

        when(patientRepository.findById(5L)).thenReturn(Optional.of(patient));

        balanceService.reversePayment(payment);

        assertThat(patient.getBalance()).isEqualByComparingTo("70.00");
    }

    @Test
    void shouldSubtractAmountForPrepayment() {
        Patient patient = new Patient();
        patient.setId(5L);
        patient.setBalance(new BigDecimal("100.00"));

        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaymentKind(PaymentKind.PREPAYMENT);
        payment.setPatientId(5L);
        payment.setAmount(new BigDecimal("40.00"));

        when(patientRepository.findById(5L)).thenReturn(Optional.of(patient));

        balanceService.reversePayment(payment);

        assertThat(patient.getBalance()).isEqualByComparingTo("60.00");
    }

    @Test
    void shouldSubtractAmountForDebtPayment() {
        Patient patient = new Patient();
        patient.setId(5L);
        patient.setBalance(new BigDecimal("100.00"));

        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaymentKind(PaymentKind.DEBT_PAYMENT);
        payment.setPatientId(5L);
        payment.setAmount(new BigDecimal("40.00"));

        when(patientRepository.findById(5L)).thenReturn(Optional.of(patient));

        balanceService.reversePayment(payment);

        assertThat(patient.getBalance()).isEqualByComparingTo("60.00");
    }

    @Test
    void shouldTreatNullBalanceAsZeroOnReverse() {
        Patient patient = new Patient();
        patient.setId(5L);
        patient.setBalance(null);

        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaymentKind(PaymentKind.DEBT);
        payment.setPatientId(5L);
        payment.setAmount(new BigDecimal("15.00"));

        when(patientRepository.findById(5L)).thenReturn(Optional.of(patient));

        balanceService.reversePayment(payment);

        assertThat(patient.getBalance()).isEqualByComparingTo("15.00");
    }

    @Test
    void shouldRoundReversedBalanceToTwoDecimalsHalfUp() {
        Patient patient = new Patient();
        patient.setId(5L);
        patient.setBalance(new BigDecimal("10.005"));

        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaymentKind(PaymentKind.DEBT);
        payment.setPatientId(5L);
        payment.setAmount(new BigDecimal("0.001"));

        when(patientRepository.findById(5L)).thenReturn(Optional.of(patient));

        balanceService.reversePayment(payment);

        assertThat(patient.getBalance()).isEqualByComparingTo("10.01");
        assertThat(patient.getBalance().scale()).isEqualTo(2);
    }
}
