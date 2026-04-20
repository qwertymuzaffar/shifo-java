package com.shifo.shifo_java.features.payment;

import com.shifo.shifo_java.features.payment.context.PaymentContext;
import com.shifo.shifo_java.features.payment.model.PaymentKind;
import com.shifo.shifo_java.features.payment.policy.PaymentPolicy;
import com.shifo.shifo_java.features.payment.policy.PaymentPolicyRegistry;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentPolicyRegistryTest {

    @Test
    void shouldReturnPolicyMatchingKind() {
        PaymentPolicy paymentPolicy = stubPolicy(PaymentKind.PAYMENT);
        PaymentPolicy debtPolicy = stubPolicy(PaymentKind.DEBT);

        PaymentPolicyRegistry registry = new PaymentPolicyRegistry(List.of(paymentPolicy, debtPolicy));

        assertThat(registry.get(PaymentKind.DEBT)).isSameAs(debtPolicy);
        assertThat(registry.get(PaymentKind.PAYMENT)).isSameAs(paymentPolicy);
    }

    @Test
    void shouldThrowWhenNoPolicyRegistered() {
        PaymentPolicyRegistry registry = new PaymentPolicyRegistry(List.of(stubPolicy(PaymentKind.PAYMENT)));

        assertThatThrownBy(() -> registry.get(PaymentKind.PREPAYMENT))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("PREPAYMENT");
    }

    private PaymentPolicy stubPolicy(PaymentKind kind) {
        return new PaymentPolicy() {
            @Override public PaymentKind supports() { return kind; }
            @Override public void validate(PaymentContext context) { }
            @Override public void enrich(PaymentContext context) { }
            @Override public void applySideEffects(PaymentContext context) { }
        };
    }
}
