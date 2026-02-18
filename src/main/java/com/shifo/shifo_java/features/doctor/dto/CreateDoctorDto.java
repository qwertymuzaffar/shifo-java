package com.shifo.shifo_java.features.doctor.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDoctorDto {

    @NotBlank(message = "Имя не может быть пустым")
    private String firstName;

    private String lastName;

    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 4, max = 100)
    @Pattern(regexp = "^[a-zA-Z0-9_]+$")
    private String username;

    @NotBlank(message = "Email не может быть пустым")
    @Email
    private String email;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 8, max = 100)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]+$"
    )
    private String password;

    @NotBlank(message = "Телефон не может быть пустым")
    @Pattern(
            regexp = "^((\\+7|7)\\d{10}|(\\+998|998)\\d{9}|(\\+996|996)\\d{9}|(\\+992|992)\\d{9})$"
    )
    private String phone;

    @NotNull(message = "Специализация обязательна")
    @Positive(message = "specialization must be a positive number")
    private Long specializationId;

    private Boolean isActive;

    private Integer experience = 2;
    private Integer consultationFee = 100;

    private WorkingHoursDto workingHours;
}


