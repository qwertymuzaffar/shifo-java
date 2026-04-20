package com.shifo.shifo_java.features.balance;

import com.shifo.shifo_java.features.balance.model.EntityType;
import com.shifo.shifo_java.features.finance.transaction.Transaction;
import com.shifo.shifo_java.features.finance.transaction.model.TransactionType;
import com.shifo.shifo_java.features.payment.Payment;
import com.shifo.shifo_java.features.payment.model.PaymentKind;
import com.shifo.shifo_java.features.payment.model.PaymentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class BalanceMapperTest {

    private final BalanceMapper mapper = new BalanceMapper();

    @Test
    void shouldMapPrepaymentAsPositiveAmount() {
        Payment payment = paymentOf(1L, new BigDecimal("100.00"), PaymentKind.PREPAYMENT, PaymentType.CASH);

        Balance balance = mapper.fromPayment(payment);

        assertThat(balance.getEntityId()).isEqualTo(1L);
        assertThat(balance.getEntityType()).isEqualTo(EntityType.PAYMENT);
        assertThat(balance.getAmount()).isEqualByComparingTo("100.00");
        assertThat(balance.getPaymentMethod()).isEqualTo(PaymentType.CASH);
        assertThat(balance.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldMapDebtPaymentAsPositiveAmount() {
        Payment payment = paymentOf(2L, new BigDecimal("50.00"), PaymentKind.DEBT_PAYMENT, PaymentType.CARD);

        Balance balance = mapper.fromPayment(payment);

        assertThat(balance.getAmount()).isEqualByComparingTo("50.00");
    }

    @Test
    void shouldMapDebtAsNegativeAmount() {
        Payment payment = paymentOf(3L, new BigDecimal("75.00"), PaymentKind.DEBT, PaymentType.CASH);

        Balance balance = mapper.fromPayment(payment);

        assertThat(balance.getAmount()).isEqualByComparingTo("-75.00");
    }

    @Test
    void shouldMapBalanceDeductionAsNegativeAmount() {
        Payment payment = paymentOf(4L, new BigDecimal("20.00"), PaymentKind.BALANCE_DEDUCTION, PaymentType.CASH);

        Balance balance = mapper.fromPayment(payment);

        assertThat(balance.getAmount()).isEqualByComparingTo("-20.00");
    }

    @Test
    void shouldMapRegularPaymentAsZeroAmount() {
        Payment payment = paymentOf(5L, new BigDecimal("200.00"), PaymentKind.PAYMENT, PaymentType.CARD);

        Balance balance = mapper.fromPayment(payment);

        assertThat(balance.getAmount()).isEqualByComparingTo("0");
    }

    @Test
    void shouldMapIncomeTransactionAsPositiveAmount() {
        Transaction tx = transactionOf(10L, new BigDecimal("500.00"), TransactionType.INCOME, PaymentType.TRANSFER);

        Balance balance = mapper.fromTransaction(tx);

        assertThat(balance.getEntityId()).isEqualTo(10L);
        assertThat(balance.getEntityType()).isEqualTo(EntityType.TRANSACTION);
        assertThat(balance.getAmount()).isEqualByComparingTo("500.00");
        assertThat(balance.getPaymentMethod()).isEqualTo(PaymentType.TRANSFER);
        assertThat(balance.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldMapExpenseTransactionAsNegativeAmount() {
        Transaction tx = transactionOf(11L, new BigDecimal("300.00"), TransactionType.EXPENSE, PaymentType.CASH);

        Balance balance = mapper.fromTransaction(tx);

        assertThat(balance.getAmount()).isEqualByComparingTo("-300.00");
    }

    @Test
    void shouldUseAbsoluteValueEvenIfTransactionAmountIsNegative() {
        Transaction tx = transactionOf(12L, new BigDecimal("-150.00"), TransactionType.INCOME, PaymentType.CASH);

        Balance balance = mapper.fromTransaction(tx);

        assertThat(balance.getAmount()).isEqualByComparingTo("150.00");
    }

    private Payment paymentOf(Long id, BigDecimal amount, PaymentKind kind, PaymentType type) {
        Payment payment = new Payment();
        payment.setId(id);
        payment.setAmount(amount);
        payment.setPaymentKind(kind);
        payment.setPaymentType(type);
        return payment;
    }

    private Transaction transactionOf(Long id, BigDecimal amount, TransactionType type, PaymentType method) {
        Transaction tx = new Transaction();
        tx.setId(id);
        tx.setAmount(amount);
        tx.setType(type);
        tx.setPaymentMethod(method);
        return tx;
    }
}
