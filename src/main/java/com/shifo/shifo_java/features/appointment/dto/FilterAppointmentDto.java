package com.shifo.shifo_java.features.appointment.dto;


import com.shifo.shifo_java.common.enums.AppointmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
public class FilterAppointmentDto {

    @Schema(description = "Filter by doctor ID", required = false)
    @Min(value = 1, message = "Doctor ID must be a positive number")
    @Nullable
    private Integer doctorId;

    @Schema(description = "Filter by patient ID", required = false)
    @Min(value = 1, message = "Patient ID must be a positive number")
    @Nullable
    private Integer patientId;

    @Schema(
            description = "Start date (YYYY-MM-DD)",
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
            description = "End date (YYYY-MM-DD)",
            example = "2025-01-01",
            required = false
    )
    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "dateTo must match YYYY-MM-DD format"
    )
    @Nullable
    private String dateTo;

    @Schema(description = "Search by patient full name or doctor name", required = false)
    @Nullable
    private String search;

    @Schema(description = "Filter by appointment status", required = false)
    @Nullable
    private AppointmentStatus status;
}

