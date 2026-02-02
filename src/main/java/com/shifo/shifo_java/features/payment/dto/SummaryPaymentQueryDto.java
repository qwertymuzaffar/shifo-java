package com.shifo.shifo_java.features.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
public class SummaryPaymentQueryDto {

    @Schema(
            description = "Patient ID",
            example = "123",
            required = false
    )
    @Nullable
    private String patientId;

    @Schema(
            description = "Patient name (full or partial)",
            example = "Анвар",
            required = false
    )
    @Nullable
    private String patientName;

    @Schema(
            description = "Date from (YYYY-MM-DD)",
            example = "2025-01-01",
            required = false
    )
    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "dateFrom must match YYYY-MM-DD format"
    )
    @Nullable
    private String dateFrom;

    @Schema(
            description = "Date to (YYYY-MM-DD)",
            example = "2025-01-31",
            required = false
    )
    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "dateTo must match YYYY-MM-DD format"
    )
    @Nullable
    private String dateTo;
}

