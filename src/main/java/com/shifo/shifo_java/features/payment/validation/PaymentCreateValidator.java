package com.shifo.shifo_java.features.payment.validation;

import com.shifo.shifo_java.features.payment.dto.CreatePaymentDto;
import com.shifo.shifo_java.features.payment.model.PaymentKind;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PaymentCreateValidator
        implements ConstraintValidator<PaymentCreateValidation, CreatePaymentDto> {

    @Override
    public boolean isValid(CreatePaymentDto dto, ConstraintValidatorContext ctx) {

        if (dto == null) return true;

        boolean isPrepayment = dto.getPaymentKind() == PaymentKind.PREPAYMENT;

        if (isPrepayment) {
            if (dto.getPatientId() == null && dto.getAppointmentId() == null) {
                ctx.disableDefaultConstraintViolation();
                ctx.buildConstraintViolationWithTemplate(
                        "Для предоплаты необходимо указать patientId или appointmentId"
                ).addConstraintViolation();
                return false;
            }
        } else {
            if (dto.getAppointmentId() == null) {
                ctx.disableDefaultConstraintViolation();
                ctx.buildConstraintViolationWithTemplate(
                        "appointmentId обязателен для данного типа платежа"
                ).addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}
