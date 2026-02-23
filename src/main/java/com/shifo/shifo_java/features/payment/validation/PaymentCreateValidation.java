package com.shifo.shifo_java.features.payment.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PaymentCreateValidator.class)
@Documented
public @interface PaymentCreateValidation {

    String message() default "Invalid payment request";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
