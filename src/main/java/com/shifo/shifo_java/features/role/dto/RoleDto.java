package com.shifo.shifo_java.features.role.dto;

import com.shifo.shifo_java.features.permission.dto.PermissionDto;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {

    private Long id;

    private String slug;

    private String name;

    private String description;

    private List<PermissionDto> permissions;

    private Instant createdAt;

    private Instant updatedAt;
}
