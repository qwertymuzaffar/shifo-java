package com.shifo.shifo_java.features.payment.policy;

import com.shifo.shifo_java.features.payment.model.PaymentKind;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentPolicyRegistry {

    private final List<PaymentPolicy> policies;

    public PaymentPolicy get(PaymentKind kind) {
        return policies.stream()
                .filter(p -> p.supports() == kind)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No policy for " + kind));
    }
}
