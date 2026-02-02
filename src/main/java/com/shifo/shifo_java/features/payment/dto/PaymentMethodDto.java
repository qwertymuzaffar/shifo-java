package com.shifo.shifo_java.features.payment.dto;


import com.shifo.shifo_java.common.enums.PaymentKind;
import com.shifo.shifo_java.common.enums.PaymentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentMethodDto {

    @Schema(description = "Amount")
    @Min(value = 1, message = "Amount must be at least 1")
    private Double amount;

    @Schema(description = "Payment type")
    @NotNull
    private PaymentType paymentType;

    @Schema(description = "Payment kind")
    private PaymentKind paymentKind;
}

