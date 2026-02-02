package com.shifo.shifo_java.features.patient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
public class UpdatePatientDto {

    @Schema(description = "Full name of the patient", example = "John Doe", required = false)
    @Size(min = 2, max = 255, message = "Full name must contain between 2 and 255 characters")
    @Nullable
    private String fullName;

    @Schema(description = "Phone number of the patient", example = "+1234567890", required = false)
    @Size(min = 5, max = 20, message = "Phone number must contain between 5 and 20 characters")
    @Nullable
    private String phone;

    @Schema(description = "Birth date of the patient (ISO format)", example = "1990-01-01", required = false)
    @Nullable
    private String birthDate;

    @Schema(description = "Last visit date (ISO format)", example = "2023-01-01T12:00:00Z", required = false)
    @Nullable
    private String lastVisit;
}

