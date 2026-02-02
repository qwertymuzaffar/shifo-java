package com.shifo.shifo_java.features.patient.dto;

import lombok.*;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreatePatientDto {

    @NotBlank(message = "Полное имя не может быть пустым")
    @Size(min = 2, max = 255, message = "Полное имя должно содержать от 2 до 255 символов")
    private String fullName;

    @NotBlank(message = "Номер телефона не может быть пустым")
    @Size(min = 5, max = 20, message = "Номер телефона должен содержать от 5 до 20 символов")
    private String phone;

    @NotBlank(message = "Дата рождения не может быть пустой")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private String birthDate;
}

