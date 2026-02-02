package com.shifo.shifo_java.features.role.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
public class CreateRoleDto {

    @Schema(description = "Role slug (system name)", example = "doctor")
    @NotBlank(message = "Slug cannot be empty")
    @Pattern(
            regexp = "^[a-z0-9_.-]+$",
            message = "Slug must contain only lowercase letters, numbers, underscores, dots or hyphens"
    )
    @Size(min = 2, max = 64, message = "Slug must be between 2 and 64 characters")
    private String slug;

    @Schema(description = "Display name of the role", example = "Врач", required = false)
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Nullable
    private String name;

    @Schema(description = "Role description", required = false)
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    @Nullable
    private String description;
}

