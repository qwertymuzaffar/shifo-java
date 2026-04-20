package com.shifo.shifo_java.features.payment;

import com.shifo.shifo_java.common.exceptions.BadRequestException;
import com.shifo.shifo_java.features.appointment.Appointment;
import com.shifo.shifo_java.features.balance.BalanceService;
import com.shifo.shifo_java.features.patient.Patient;
import com.shifo.shifo_java.features.payment.context.PaymentContext;
import com.shifo.shifo_java.features.payment.dto.CreatePaymentDto;
import com.shifo.shifo_java.features.payment.model.PaymentKind;
import com.shifo.shifo_java.features.payment.model.PaymentStatus;
import com.shifo.shifo_java.features.payment.policy.PaymentPolicyImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentPolicyImplTest {

    @Mock private BalanceService balanceService;

    @InjectMocks private PaymentPolicyImpl policy;

    @Test
    void shouldSupportPaymentKind() {
        assertThat(policy.supports()).isEqualTo(PaymentKind.PAYMENT);
    }

    @Test
    void shouldThrowWhenAppointmentMissing() {
        PaymentContext ctx = new PaymentContext(new CreatePaymentDto(), PaymentKind.PAYMENT, null, new Patient());

        assertThatThrownBy(() -> policy.validate(ctx))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("appointment");
    }

    @Test
    void shouldPassValidationWithAppointment() {
        PaymentContext ctx = new PaymentContext(new CreatePaymentDto(), PaymentKind.PAYMENT, new Appointment(), new Patient());

        policy.validate(ctx);
    }

    @Test
    void shouldEnrichWithPaidStatusAndNowWhenPaidAtMissing() {
        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setPaidAt(null);
        PaymentContext ctx = new PaymentContext(dto, PaymentKind.PAYMENT, new Appointment(), new Patient());

        Instant before = Instant.now();
        policy.enrich(ctx);
        Instant after = Instant.now();

        assertThat(ctx.getStatus()).isEqualTo(PaymentStatus.PAID);
        assertThat(ctx.getPaidAt()).isBetween(before, after);
    }

    @Test
    void shouldEnrichWithDtoPaidAtWhenProvided() {
        Instant dtoPaidAt = Instant.parse("2026-04-18T10:00:00Z");
        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setPaidAt(dtoPaidAt);

        PaymentContext ctx = new PaymentContext(dto, PaymentKind.PAYMENT, new Appointment(), new Patient());

        policy.enrich(ctx);

        assertThat(ctx.getPaidAt()).isEqualTo(dtoPaidAt);
    }

    @Test
    void shouldRecordPaymentOnSideEffects() {
        Payment payment = new Payment();
        PaymentContext ctx = new PaymentContext(new CreatePaymentDto(), PaymentKind.PAYMENT, new Appointment(), new Patient());
        ctx.setPayment(payment);

        policy.applySideEffects(ctx);

        verify(balanceService).recordPayment(payment);
    }
}
