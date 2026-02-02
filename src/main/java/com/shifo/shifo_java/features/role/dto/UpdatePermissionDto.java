package com.shifo.shifo_java.features.role.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
public class UpdatePermissionDto {

    @Schema(description = "Permission slug (system identifier)", required = false)
    @Pattern(
            regexp = "^[a-z0-9_.-]+$",
            message = "Slug must contain only lowercase letters, numbers, underscores, dots or hyphens"
    )
    @Size(min = 2, max = 100, message = "Slug must be between 2 and 100 characters")
    @Nullable
    private String slug;

    @Schema(description = "Display name", required = false)
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Nullable
    private String name;

    @Schema(description = "Permission description", required = false)
    @Size(max = 255, message = "Description must not exceed 255 characters")
    @Nullable
    private String description;
}
