package com.shifo.shifo_java.features.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserDto {

    @Schema(description = "Имя пользователя (логин)", example = "user123")
    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 4, max = 100, message = "Имя пользователя должно содержать от 4 до 100 символов")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$",
            message = "Имя пользователя может содержать только буквы, цифры и подчеркивания")
    private String username;


    @Schema(description = "Имя пользователя", example = "Иван")
    @Size(min = 2, max = 50, message = "Имя должно содержать от 2 до 50 символов")
    private String firstName;


    @Schema(description = "Фамилия пользователя", example = "Иванов")
    @Size(min = 2, max = 50, message = "Фамилия должна содержать от 2 до 50 символов")
    private String lastName;


    @Schema(description = "Email пользователя", example = "user@example.com")
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;


    @Schema(description = "Пароль пользователя", example = "P@ssw0rd123")
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 8, max = 100, message = "Пароль должен содержать от 8 до 100 символов")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]+$",
            message = "Пароль должен содержать как минимум одну заглавную букву, одну строчную букву и одну цифру"
    )
    private String password;


    @Schema(description = "ID роли пользователя", example = "1")
    @Min(value = 1, message = "roleId должен быть больше 0")
    private Long roleId;
}

