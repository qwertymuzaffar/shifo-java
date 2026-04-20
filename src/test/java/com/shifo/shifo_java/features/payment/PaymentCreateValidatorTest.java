package com.shifo.shifo_java.features.payment;

import com.shifo.shifo_java.features.payment.dto.CreatePaymentDto;
import com.shifo.shifo_java.features.payment.model.PaymentKind;
import com.shifo.shifo_java.features.payment.validation.PaymentCreateValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaymentCreateValidatorTest {

    private PaymentCreateValidator validator;
    private ConstraintValidatorContext context;
    private ConstraintViolationBuilder builder;

    @BeforeEach
    void setUp() {
        validator = new PaymentCreateValidator();
        context = mock(ConstraintValidatorContext.class);
        builder = mock(ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    }

    @Test
    void shouldReturnTrueWhenDtoNull() {
        assertThat(validator.isValid(null, context)).isTrue();
    }

    @Test
    void shouldAllowPrepaymentWithPatientId() {
        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setPaymentKind(PaymentKind.PREPAYMENT);
        dto.setPatientId(5L);

        assertThat(validator.isValid(dto, context)).isTrue();
    }

    @Test
    void shouldAllowPrepaymentWithAppointmentId() {
        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setPaymentKind(PaymentKind.PREPAYMENT);
        dto.setAppointmentId(7L);

        assertThat(validator.isValid(dto, context)).isTrue();
    }

    @Test
    void shouldRejectPrepaymentWithoutEither() {
        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setPaymentKind(PaymentKind.PREPAYMENT);
        dto.setPatientId(null);
        dto.setAppointmentId(null);

        assertThat(validator.isValid(dto, context)).isFalse();
        verify(context).buildConstraintViolationWithTemplate("Для предоплаты необходимо указать patientId или appointmentId");
        verify(builder).addConstraintViolation();
    }

    @Test
    void shouldAllowNonPrepaymentWithAppointmentId() {
        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setPaymentKind(PaymentKind.PAYMENT);
        dto.setAppointmentId(7L);

        assertThat(validator.isValid(dto, context)).isTrue();
    }

    @Test
    void shouldRejectNonPrepaymentWithoutAppointmentId() {
        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setPaymentKind(PaymentKind.DEBT_PAYMENT);
        dto.setAppointmentId(null);

        assertThat(validator.isValid(dto, context)).isFalse();
        verify(context).buildConstraintViolationWithTemplate("appointmentId обязателен для данного типа платежа");
        verify(builder).addConstraintViolation();
    }
}
