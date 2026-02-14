package com.shifo.shifo_java.features.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserDto {

    @Schema(description = "Username (login)", example = "user123")
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 4, max = 100, message = "Username must contain between 4 and 100 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9_]+$",
            message = "Username may contain only letters, digits, and underscores"
    )
    private String username;

    @Schema(description = "First name", example = "Иван", required = false)
    @Size(min = 2, max = 50, message = "First name must contain between 2 and 50 characters")
    private String firstName;

    @Schema(description = "Last name", example = "Иванов", required = false)
    @Size(min = 2, max = 50, message = "Last name must contain between 2 and 50 characters")
    private String lastName;

    @Schema(description = "User email", example = "user@example.com")
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "User password", example = "P@ssw0rd123")
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, max = 100, message = "Password must contain between 8 and 100 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, and one digit"
    )
    private String password;

    @Schema(description = "User role ID", example = "1", required = false)
    @Min(value = 1, message = "roleId must be a positive integer")
    private Long roleId;
}

