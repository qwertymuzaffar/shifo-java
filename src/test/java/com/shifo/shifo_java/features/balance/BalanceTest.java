package com.shifo.shifo_java.features.balance;

import com.shifo.shifo_java.features.balance.model.EntityType;
import com.shifo.shifo_java.features.finance.transaction.Transaction;
import com.shifo.shifo_java.features.payment.Payment;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BalanceTest {

    @Test
    void shouldReturnPaymentAsRelatedEntityForPaymentType() {
        Payment payment = new Payment();
        payment.setId(1L);

        Balance balance = new Balance();
        balance.setEntityType(EntityType.PAYMENT);
        balance.setPayment(payment);

        assertThat(balance.getRelatedEntity()).isSameAs(payment);
    }

    @Test
    void shouldReturnTransactionAsRelatedEntityForTransactionType() {
        Transaction tx = new Transaction();
        tx.setId(2L);

        Balance balance = new Balance();
        balance.setEntityType(EntityType.TRANSACTION);
        balance.setTransaction(tx);

        assertThat(balance.getRelatedEntity()).isSameAs(tx);
    }

    @Test
    void shouldReturnNullRelatedEntityWhenEntityTypeNull() {
        Balance balance = new Balance();
        balance.setEntityType(null);

        assertThat(balance.getRelatedEntity()).isNull();
    }

    @Test
    void shouldReturnPaymentEntityTypeName() {
        Balance balance = new Balance();
        balance.setEntityType(EntityType.PAYMENT);

        assertThat(balance.getEntityTypeName()).isEqualTo("payment");
    }

    @Test
    void shouldReturnTransactionEntityTypeNameForNonPayment() {
        Balance balance = new Balance();
        balance.setEntityType(EntityType.TRANSACTION);

        assertThat(balance.getEntityTypeName()).isEqualTo("transaction");
    }
}
