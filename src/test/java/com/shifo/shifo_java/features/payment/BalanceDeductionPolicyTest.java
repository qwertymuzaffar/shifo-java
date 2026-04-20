package com.shifo.shifo_java.features.payment;

import com.shifo.shifo_java.common.exceptions.BadRequestException;
import com.shifo.shifo_java.features.patient.Patient;
import com.shifo.shifo_java.features.payment.context.PaymentContext;
import com.shifo.shifo_java.features.payment.dto.CreatePaymentDto;
import com.shifo.shifo_java.features.payment.model.PaymentKind;
import com.shifo.shifo_java.features.payment.model.PaymentStatus;
import com.shifo.shifo_java.features.payment.policy.BalanceDeductionPolicy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BalanceDeductionPolicyTest {

    private final BalanceDeductionPolicy policy = new BalanceDeductionPolicy();

    @Test
    void shouldSupportBalanceDeductionKind() {
        assertThat(policy.supports()).isEqualTo(PaymentKind.BALANCE_DEDUCTION);
    }

    @Test
    void shouldPassValidationWhenBalanceSufficient() {
        Patient patient = new Patient();
        patient.setBalance(new BigDecimal("200.00"));

        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setAmount(new BigDecimal("150.00"));

        PaymentContext ctx = new PaymentContext(dto, PaymentKind.BALANCE_DEDUCTION, null, patient);

        policy.validate(ctx);
    }

    @Test
    void shouldThrowWhenBalanceInsufficient() {
        Patient patient = new Patient();
        patient.setBalance(new BigDecimal("50.00"));

        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setAmount(new BigDecimal("150.00"));

        PaymentContext ctx = new PaymentContext(dto, PaymentKind.BALANCE_DEDUCTION, null, patient);

        assertThatThrownBy(() -> policy.validate(ctx))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Недостаточно");
    }

    @Test
    void shouldThrowWhenNullBalanceAndNonZeroAmount() {
        Patient patient = new Patient();
        patient.setBalance(null);

        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setAmount(new BigDecimal("10.00"));

        PaymentContext ctx = new PaymentContext(dto, PaymentKind.BALANCE_DEDUCTION, null, patient);

        assertThatThrownBy(() -> policy.validate(ctx))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void shouldEnrichWithDtoStatusOrPendingByDefault() {
        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setStatus(null);

        PaymentContext ctx = new PaymentContext(dto, PaymentKind.BALANCE_DEDUCTION, null, new Patient());

        policy.enrich(ctx);

        assertThat(ctx.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(ctx.getPaidAt()).isNull();
    }

    @Test
    void shouldEnrichWithPaidStatusAndPaidAtWhenDtoStatusPaid() {
        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setStatus(PaymentStatus.PAID);

        PaymentContext ctx = new PaymentContext(dto, PaymentKind.BALANCE_DEDUCTION, null, new Patient());

        policy.enrich(ctx);

        assertThat(ctx.getStatus()).isEqualTo(PaymentStatus.PAID);
        assertThat(ctx.getPaidAt()).isNotNull();
    }

    @Test
    void shouldSubtractFromBalanceOnlyWhenPaid() {
        Patient patient = new Patient();
        patient.setBalance(new BigDecimal("200.00"));

        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setAmount(new BigDecimal("50.00"));

        PaymentContext ctx = new PaymentContext(dto, PaymentKind.BALANCE_DEDUCTION, null, patient);
        ctx.setStatus(PaymentStatus.PAID);

        policy.applySideEffects(ctx);

        assertThat(patient.getBalance()).isEqualByComparingTo("150.00");
    }

    @Test
    void shouldNotTouchBalanceWhenNotPaid() {
        Patient patient = new Patient();
        patient.setBalance(new BigDecimal("200.00"));

        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setAmount(new BigDecimal("50.00"));

        PaymentContext ctx = new PaymentContext(dto, PaymentKind.BALANCE_DEDUCTION, null, patient);
        ctx.setStatus(PaymentStatus.PENDING);

        policy.applySideEffects(ctx);

        assertThat(patient.getBalance()).isEqualByComparingTo("200.00");
    }
}
