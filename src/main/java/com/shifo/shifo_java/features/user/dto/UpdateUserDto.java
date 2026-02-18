package com.shifo.shifo_java.features.user.dto;

import com.shifo.shifo_java.features.role.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
public class UpdateUserDto {

    @Schema(description = "Email пользователя", example = "new.email@example.com")
    @Email(message = "Некорректный формат email")
    private String email;


    @Schema(description = "Имя пользователя", example = "Иван")
    @Size(min = 2, max = 50, message = "Имя должно содержать от 2 до 50 символов")
    private String firstName;


    @Schema(description = "Фамилия пользователя", example = "Иванов")
    @Size(min = 2, max = 50, message = "Фамилия должна содержать от 2 до 50 символов")
    private String lastName;


    @Schema(description = "Новый пароль пользователя", example = "NewP@ssw0rd123")
    @Size(min = 8, max = 100, message = "Пароль должен содержать от 8 до 100 символов")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]+$",
            message = "Пароль должен содержать как минимум одну заглавную букву, одну строчную букву и одну цифру"
    )
    private String password;

    @Schema(description = "Номер телефона пользователя", example = "+992900123456")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Некорректный формат номера телефона")
    private String phone;

    @Schema(description = "Роль пользователя", allowableValues = {"admin", "user"})
    @Pattern(regexp = "admin|user", message = "Роль должна быть либо admin, либо user")
    private Role role;

    @Schema(description = "Статус активности пользователя")
    private Boolean isActive;
}

