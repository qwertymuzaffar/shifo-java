package com.shifo.shifo_java.features.payment.policy;

import com.shifo.shifo_java.features.payment.context.PaymentContext;
import com.shifo.shifo_java.features.payment.model.PaymentKind;

public interface PaymentPolicy {

    PaymentKind supports();

    void validate(PaymentContext context);

    void enrich(PaymentContext context);

    void applySideEffects(PaymentContext context);
}
