package com.shifo.shifo_java.features.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDto {

    @Schema(
            description = "Username (login)",
            example = "admin",
            required = true
    )
    @NotBlank(message = "Username cannot be empty")
    private String username;

    @Schema(
            description = "User password",
            example = "Admin123!",
            required = true
    )
    @NotBlank(message = "Password cannot be empty")
    private String password;
}

