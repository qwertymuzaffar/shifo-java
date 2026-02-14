package com.shifo.shifo_java.features.patient.dto;

import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePatientDto {

    @Schema(
            description = "Full name of the patient",
            example = "John Doe"
    )
    @NotBlank(message = "Полное имя не может быть пустым")
    @Size(min = 2, max = 255, message = "Полное имя должно содержать от 2 до 255 символов")
    private String fullName;

    @Schema(
            description = "Phone number (Tajikistan format)",
            example = "+992901234567"
    )
    @NotBlank(message = "Номер телефона не может быть пустым")
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
            description = "Birth date of the patient",
            example = "1990-01-01"
    )
    @NotNull(message = "Дата рождения не может быть пустой")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;
}


