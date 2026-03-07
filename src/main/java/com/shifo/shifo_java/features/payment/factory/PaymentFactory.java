package com.shifo.shifo_java.features.payment.factory;

import com.shifo.shifo_java.features.payment.Payment;
import com.shifo.shifo_java.features.payment.context.PaymentContext;
import org.springframework.stereotype.Component;

@Component
public class PaymentFactory {

    public Payment create(PaymentContext context) {

        return Payment.builder()
                .appointment(
                        context.getAppointment() != null
                                ? context.getAppointment()
                                : null
                )
                .patientId(context.getPatient().getId())
                .userId(context.getUserId())
                .amount(context.getDto().getAmount())
                .paymentType(context.getDto().getPaymentType())
                .paymentKind(context.getKind())
                .status(context.getStatus())
                .paidAt(context.getPaidAt())
                .build();
    }
}
