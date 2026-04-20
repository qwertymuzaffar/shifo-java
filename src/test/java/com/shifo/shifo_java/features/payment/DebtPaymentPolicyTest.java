package com.shifo.shifo_java.features.payment;

import com.shifo.shifo_java.common.exceptions.BadRequestException;
import com.shifo.shifo_java.features.balance.BalanceService;
import com.shifo.shifo_java.features.patient.Patient;
import com.shifo.shifo_java.features.payment.context.PaymentContext;
import com.shifo.shifo_java.features.payment.dto.CreatePaymentDto;
import com.shifo.shifo_java.features.payment.model.PaymentKind;
import com.shifo.shifo_java.features.payment.model.PaymentStatus;
import com.shifo.shifo_java.features.payment.policy.DebtPaymentPolicy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DebtPaymentPolicyTest {

    @Mock private BalanceService balanceService;

    @InjectMocks private DebtPaymentPolicy policy;

    @Test
    void shouldSupportDebtPaymentKind() {
        assertThat(policy.supports()).isEqualTo(PaymentKind.DEBT_PAYMENT);
    }

    @Test
    void shouldThrowWhenPatientMissing() {
        PaymentContext ctx = new PaymentContext(new CreatePaymentDto(), PaymentKind.DEBT_PAYMENT, null, null);

        assertThatThrownBy(() -> policy.validate(ctx))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("patient");
    }

    @Test
    void shouldPassValidationWithPatient() {
        PaymentContext ctx = new PaymentContext(new CreatePaymentDto(), PaymentKind.DEBT_PAYMENT, null, new Patient());

        policy.validate(ctx);
    }

    @Test
    void shouldEnrichWithPaidStatus() {
        PaymentContext ctx = new PaymentContext(new CreatePaymentDto(), PaymentKind.DEBT_PAYMENT, null, new Patient());

        policy.enrich(ctx);

        assertThat(ctx.getStatus()).isEqualTo(PaymentStatus.PAID);
        assertThat(ctx.getPaidAt()).isNotNull();
    }

    @Test
    void shouldRecordPaymentOnSideEffects() {
        Payment payment = new Payment();
        PaymentContext ctx = new PaymentContext(new CreatePaymentDto(), PaymentKind.DEBT_PAYMENT, null, new Patient());
        ctx.setPayment(payment);

        policy.applySideEffects(ctx);

        verify(balanceService).recordPayment(payment);
    }
}
