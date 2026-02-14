package com.shifo.shifo_java.features.role.dto;


import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AssignPermissionsRequestDto {

    @NotEmpty(message = "permissionIds cannot be empty")
    private List<Long> permissionIds;
}
