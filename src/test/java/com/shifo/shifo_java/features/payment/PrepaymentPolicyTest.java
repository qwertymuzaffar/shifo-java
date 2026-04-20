package com.shifo.shifo_java.features.payment;

import com.shifo.shifo_java.common.exceptions.BadRequestException;
import com.shifo.shifo_java.features.appointment.Appointment;
import com.shifo.shifo_java.features.patient.Patient;
import com.shifo.shifo_java.features.payment.context.PaymentContext;
import com.shifo.shifo_java.features.payment.dto.CreatePaymentDto;
import com.shifo.shifo_java.features.payment.model.PaymentKind;
import com.shifo.shifo_java.features.payment.model.PaymentStatus;
import com.shifo.shifo_java.features.payment.policy.PrepaymentPolicy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PrepaymentPolicyTest {

    private final PrepaymentPolicy policy = new PrepaymentPolicy();

    @Test
    void shouldSupportPrepaymentKind() {
        assertThat(policy.supports()).isEqualTo(PaymentKind.PREPAYMENT);
    }

    @Test
    void shouldPassValidationWhenAppointmentPresent() {
        PaymentContext ctx = new PaymentContext(new CreatePaymentDto(), PaymentKind.PREPAYMENT, new Appointment(), new Patient());

        policy.validate(ctx);
    }

    @Test
    void shouldThrowWhenNoAppointmentAndNoPatientId() {
        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setPatientId(null);
        PaymentContext ctx = new PaymentContext(dto, PaymentKind.PREPAYMENT, null, new Patient());

        assertThatThrownBy(() -> policy.validate(ctx))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void shouldEnrichWithPaidStatus() {
        PaymentContext ctx = new PaymentContext(new CreatePaymentDto(), PaymentKind.PREPAYMENT, null, new Patient());

        policy.enrich(ctx);

        assertThat(ctx.getStatus()).isEqualTo(PaymentStatus.PAID);
        assertThat(ctx.getPaidAt()).isNotNull();
    }

    @Test
    void shouldAddAmountToPatientBalance() {
        Patient patient = new Patient();
        patient.setBalance(new BigDecimal("50.00"));

        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setAmount(new BigDecimal("120.00"));

        PaymentContext ctx = new PaymentContext(dto, PaymentKind.PREPAYMENT, null, patient);

        policy.applySideEffects(ctx);

        assertThat(patient.getBalance()).isEqualByComparingTo("170.00");
    }

    @Test
    void shouldTreatNullBalanceAsZeroOnSideEffects() {
        Patient patient = new Patient();
        patient.setBalance(null);

        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setAmount(new BigDecimal("100.00"));

        PaymentContext ctx = new PaymentContext(dto, PaymentKind.PREPAYMENT, null, patient);

        policy.applySideEffects(ctx);

        assertThat(patient.getBalance()).isEqualByComparingTo("100.00");
    }
}
