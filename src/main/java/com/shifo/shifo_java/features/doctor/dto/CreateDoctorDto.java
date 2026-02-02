package com.shifo.shifo_java.features.doctor.dto;

import com.shifo.shifo_java.features.specialization.Specialization;
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
    @Size(min = 4, max = 100, message = "Имя пользователя должно содержать от 4 до 100 символов")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Имя пользователя может содержать только буквы, цифры и подчеркивания")
    private String username;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 8, max = 100, message = "Пароль должен содержать от 8 до 100 символов")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]+$",
            message = "Пароль должен содержать как минимум одну заглавную букву, одну строчную букву и одну цифру"
    )
    private String password;

    @NotBlank(message = "Телефон не может быть пустым")
    @Pattern(
            regexp = "^((\\+7|7)\\d{10}|(\\+998|998)\\d{9}|(\\+996|996)\\d{9}|(\\+992|992)\\d{9})$",
            message = "Телефон должен быть валидным номером России, Узбекистана, Кыргызстана, Казахстана или Таджикистана"
    )
    private String phone;

    @NotNull(message = "Специализация обязательна")
    private Specialization specialization;

    private Long roomId;

    private Integer experience = 2;

    private Integer consultationFee = 100;

    private WorkingHoursDto workingHours = new WorkingHoursDto(
            "09:00",
            "18:00",
            java.util.List.of(1,2,3,4,5)
    );
}

