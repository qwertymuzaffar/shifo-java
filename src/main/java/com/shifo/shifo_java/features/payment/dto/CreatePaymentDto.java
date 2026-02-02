package com.shifo.shifo_java.features.payment.dto;

import com.shifo.shifo_java.common.enums.PaymentKind;
import com.shifo.shifo_java.common.enums.PaymentStatus;
import com.shifo.shifo_java.common.enums.PaymentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePaymentDto {

    @Schema(description = "ID of the appointment")
    @NotNull(message = "Appointment ID is required")
    private Long appointmentId;

    @Schema(description = "Amount of the payment")
    @Min(value = 1, message = "Payment amount must be at least 1")
    private Double amount;

    @Schema(
            description = "Payment type: dc, alif, eskhata, cash",
            example = "DC"
    )
    @NotNull(message = "Payment type is required")
    private PaymentType paymentType;

    @Schema(
            description = "Category: PAYMENT, DEBT, PREPAYMENT, DEBT_PAYMENT",
            example = "PAYMENT",
            required = false
    )
    private PaymentKind paymentKind;

    @Schema(
            description = "Status of the payment",
            example = "PENDING",
            defaultValue = "PENDING"
    )
    private PaymentStatus status = PaymentStatus.PENDING;
}
