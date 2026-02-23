package com.shifo.shifo_java.features.payment.dto;

import com.shifo.shifo_java.features.payment.model.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePaymentStatusDto {

    @NotNull(message = "ID платежа обязателен")
    @Schema(description = "ID платежа", example = "10")
    private Long id;

    @NotNull(message = "Статус платежа обязателен")
    @Schema(
            description = "Статус платежа: paid (оплачен), pending (в ожидании), failed (неудачный)",
            example = "paid"
    )
    private PaymentStatus status;
}
