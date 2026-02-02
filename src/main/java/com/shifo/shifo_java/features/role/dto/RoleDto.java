package com.shifo.shifo_java.features.role.dto;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RoleDto {

    private Long id;

    private String slug;

    private String name;

    private String description;

    private List<Long> permissionIds;

    private Instant createdAt;

    private Instant updatedAt;
}

