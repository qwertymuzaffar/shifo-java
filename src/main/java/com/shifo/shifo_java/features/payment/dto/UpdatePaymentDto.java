package com.shifo.shifo_java.features.payment.dto;

import com.shifo.shifo_java.common.enums.PaymentKind;
import com.shifo.shifo_java.common.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@Getter
@Setter
public class UpdatePaymentDto {

    @Schema(description = "Payment status: PAID, PENDING, FAILED", required = false)
    @Nullable
    private PaymentStatus status;

    @Schema(description = "Payment date", example = "2023-01-01T12:00:00Z", required = false)
    @Nullable
    private LocalDateTime paidAt;

    @Schema(description = "Payment category", required = false)
    @Nullable
    private PaymentKind paymentKind;
}

