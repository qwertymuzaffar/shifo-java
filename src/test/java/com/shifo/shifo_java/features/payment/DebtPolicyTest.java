package com.shifo.shifo_java.features.payment;

import com.shifo.shifo_java.common.exceptions.BadRequestException;
import com.shifo.shifo_java.features.appointment.Appointment;
import com.shifo.shifo_java.features.patient.Patient;
import com.shifo.shifo_java.features.payment.context.PaymentContext;
import com.shifo.shifo_java.features.payment.dto.CreatePaymentDto;
import com.shifo.shifo_java.features.payment.model.PaymentKind;
import com.shifo.shifo_java.features.payment.model.PaymentStatus;
import com.shifo.shifo_java.features.payment.policy.DebtPolicy;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DebtPolicyTest {

    private final DebtPolicy policy = new DebtPolicy();

    @Test
    void shouldSupportDebtKind() {
        assertThat(policy.supports()).isEqualTo(PaymentKind.DEBT);
    }

    @Test
    void shouldThrowWhenAppointmentMissing() {
        PaymentContext ctx = new PaymentContext(new CreatePaymentDto(), PaymentKind.DEBT, null, new Patient());

        assertThatThrownBy(() -> policy.validate(ctx))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("appointment");
    }

    @Test
    void shouldPassValidationWithAppointment() {
        PaymentContext ctx = new PaymentContext(new CreatePaymentDto(), PaymentKind.DEBT, new Appointment(), new Patient());

        policy.validate(ctx);
    }

    @Test
    void shouldEnrichWithPendingStatusAndNoPaidAt() {
        PaymentContext ctx = new PaymentContext(new CreatePaymentDto(), PaymentKind.DEBT, new Appointment(), new Patient());

        policy.enrich(ctx);

        assertThat(ctx.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(ctx.getPaidAt()).isNull();
    }

    @Test
    void shouldDoNothingOnSideEffects() {
        PaymentContext ctx = new PaymentContext(new CreatePaymentDto(), PaymentKind.DEBT, new Appointment(), new Patient());

        policy.applySideEffects(ctx);
    }
}
