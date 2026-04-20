package com.shifo.shifo_java.features.payment;

import com.shifo.shifo_java.features.appointment.Appointment;
import com.shifo.shifo_java.features.patient.Patient;
import com.shifo.shifo_java.features.payment.context.PaymentContext;
import com.shifo.shifo_java.features.payment.dto.CreatePaymentDto;
import com.shifo.shifo_java.features.payment.factory.PaymentFactory;
import com.shifo.shifo_java.features.payment.model.PaymentKind;
import com.shifo.shifo_java.features.payment.model.PaymentStatus;
import com.shifo.shifo_java.features.payment.model.PaymentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentFactoryTest {

    private final PaymentFactory factory = new PaymentFactory();

    @Test
    void shouldBuildPaymentFromContextWithAppointment() {
        Appointment appointment = new Appointment();
        appointment.setId(7L);

        Patient patient = new Patient();
        patient.setId(3L);

        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setAmount(new BigDecimal("500.00"));
        dto.setPaymentType(PaymentType.CASH);

        PaymentContext ctx = new PaymentContext(dto, PaymentKind.PAYMENT, appointment, patient);
        ctx.setUserId(42L);
        ctx.setStatus(PaymentStatus.PAID);
        Instant paidAt = Instant.parse("2026-04-18T10:00:00Z");
        ctx.setPaidAt(paidAt);

        Payment payment = factory.create(ctx);

        assertThat(payment.getAppointment()).isSameAs(appointment);
        assertThat(payment.getPatientId()).isEqualTo(3L);
        assertThat(payment.getUserId()).isEqualTo(42L);
        assertThat(payment.getAmount()).isEqualByComparingTo("500.00");
        assertThat(payment.getPaymentType()).isEqualTo(PaymentType.CASH);
        assertThat(payment.getPaymentKind()).isEqualTo(PaymentKind.PAYMENT);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PAID);
        assertThat(payment.getPaidAt()).isEqualTo(paidAt);
    }

    @Test
    void shouldBuildPaymentWithoutAppointmentForPrepayment() {
        Patient patient = new Patient();
        patient.setId(9L);

        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setAmount(new BigDecimal("100.00"));
        dto.setPaymentType(PaymentType.CARD);

        PaymentContext ctx = new PaymentContext(dto, PaymentKind.PREPAYMENT, null, patient);
        ctx.setUserId(1L);
        ctx.setStatus(PaymentStatus.PAID);

        Payment payment = factory.create(ctx);

        assertThat(payment.getAppointment()).isNull();
        assertThat(payment.getPatientId()).isEqualTo(9L);
        assertThat(payment.getPaymentKind()).isEqualTo(PaymentKind.PREPAYMENT);
    }
}
