package com.shifo.shifo_java.features.role.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AssignPermissionsDto {

    @Schema(
            description = "IDs of permissions to assign to the role",
            example = "[1, 2, 3]"
    )
    @NotEmpty(message = "permissionIds must not be empty")
    private List<
            @NotNull(message = "Permission ID cannot be null")
            @Min(value = 1, message = "Permission ID must be a positive number")
                    Integer
            > permissionIds;
}

