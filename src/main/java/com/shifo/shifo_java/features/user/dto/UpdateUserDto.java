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

    @Schema(description = "New email", example = "new.email@example.com", required = false)
    @Email(message = "Invalid email format")
    @Nullable
    private String email;

    @Schema(description = "First name", example = "Иван", required = false)
    @Size(min = 2, max = 50, message = "First name must contain between 2 and 50 characters")
    @Nullable
    private String firstName;

    @Schema(description = "Last name", example = "Иванов", required = false)
    @Size(min = 2, max = 50, message = "Last name must contain between 2 and 50 characters")
    @Nullable
    private String lastName;

    @Schema(description = "New password", example = "NewP@ssw0rd123", required = false)
    @Size(min = 8, max = 100, message = "Password must contain between 8 and 100 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, and one digit"
    )
    @Nullable
    private String password;

    @Nullable
    private String phone;

    @Schema(description = "User role", example = "admin", required = false)
    @Pattern(regexp = "admin|user", message = "Role must be either 'admin' or 'user'")
    @Nullable
    private Role role;

    @Schema(description = "Active status", example = "true", required = false)
    @Nullable
    private Boolean isActive;
}

