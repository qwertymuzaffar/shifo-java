package com.shifo.shifo_java.features.patient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class UpdatePatientDto {

    @Schema(
            description = "Full name of the patient",
            example = "John Doe"
    )
    @Nullable
    @Size(min = 2, max = 255, message = "Full name must contain between 2 and 255 characters")
    private String fullName;

    @Schema(
            description = "Phone number (Tajikistan format)",
            example = "+992901234567"
    )
    @Nullable
    @Pattern(
            regexp = "^(\\+992|992)?\\d{9}$",
            message = "Номер телефона должен быть валидным номером Таджикистана"
    )
    private String phone;

    @Schema(
            description = "Address of the patient",
            example = "Dushanbe, Rudaki Ave 123"
    )
    @Nullable
    @Size(max = 255)
    private String address;

    @Schema(
            description = "Emergency contact (JSON string)",
            example = "{\"name\":\"Jane Doe\",\"phone\":\"+992901112233\"}"
    )
    @Nullable
    private String emergencyContact;

    @Schema(
            description = "List of patient allergies",
            example = "[\"Penicillin\", \"Peanuts\"]"
    )
    @Nullable
    private String allergies;

    @Schema(
            description = "Medical history notes"
    )
    @Nullable
    private String medicalHistory;

    @Schema(
            description = "Birth date of the patient",
            example = "1990-01-01"
    )
    @Nullable
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;
}


