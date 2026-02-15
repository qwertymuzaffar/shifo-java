package com.shifo.shifo_java.features.patient.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePatientDto {

    @NotBlank(message = "Полное имя не может быть пустым")
    @Size(min = 2, max = 255)
    private String fullName;

    @NotBlank(message = "Номер телефона не может быть пустым")
    @Pattern(
            regexp = "^(\\+992|992)?\\d{9}$",
            message = "Номер телефона должен быть валидным номером Таджикистана"
    )
    private String phone;

    @Size(max = 255)
    private String address;

    @NotNull(message = "Дата рождения не может быть пустой")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    private String emergencyContact;

    @Setter(AccessLevel.NONE)
    private String allergies;

    @JsonSetter("allergies")
    public void setAllergies(Object value) {
        if (value == null) {
            this.allergies = null;
            return;
        }

        if (value instanceof String str) {
            this.allergies = str;
        } else if (value instanceof List<?> list) {
            this.allergies = list.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
        } else {
            throw new IllegalArgumentException("Invalid allergies format");
        }
    }

    private String medicalHistory;
}




