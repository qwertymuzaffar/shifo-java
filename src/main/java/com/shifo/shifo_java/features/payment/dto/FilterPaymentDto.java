package com.shifo.shifo_java.features.payment.dto;

import com.shifo.shifo_java.common.enums.PaymentStatus;
import com.shifo.shifo_java.common.enums.PaymentType;
import com.shifo.shifo_java.common.dto.PaginationDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
public class FilterPaymentDto extends PaginationDto {

    @Schema(description = "Appointment ID", example = "123", required = false)
    @Nullable
    private String appointmentId;

    @Schema(
            description = "Payment system type",
            example = "DC",
            required = false
    )
    @Nullable
    private PaymentType paymentType;

    @Schema(
            description = "Payment status",
            example = "PAID",
            required = false
    )
    @Nullable
    private PaymentStatus status;

    @Schema(description = "Search query", required = false)
    @Nullable
    private String search;

    @Schema(
            description = "Date from (YYYY-MM-DD)",
            example = "2025-01-01",
            required = false
    )
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "dateFrom must match YYYY-MM-DD format")
    @Nullable
    private String dateFrom;

    @Schema(
            description = "Date to (YYYY-MM-DD)",
            example = "2025-01-31",
            required = false
    )
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "dateTo must match YYYY-MM-DD format")
    @Nullable
    private String dateTo;
}
