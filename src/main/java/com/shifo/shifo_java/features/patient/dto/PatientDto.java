package com.shifo.shifo_java.features.patient.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PatientDto {

    private Long id;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Pattern(
            regexp = "^(\\+992|992)?\\d{9}$",
            message = "Номер телефона должен быть валидным номером Таджикистана"
    )
    private String phone;

    private String address;

    // Stored as JSON string
    private String emergencyContact;

    private List<String> allergies;

    private String medicalHistory;

    private LocalDate birthDate;

    private Integer status;

    private BigDecimal balance;

    private Instant createdAt;

    private Instant updatedAt;
}



