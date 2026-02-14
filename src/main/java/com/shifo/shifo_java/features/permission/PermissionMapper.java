package com.shifo.shifo_java.features.permission;

import com.shifo.shifo_java.features.permission.dto.PermissionDto;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapper {

    public PermissionDto toDto(Permission permission) {
        return new PermissionDto(
                permission.getId(),
                permission.getSlug(),
                permission.getName(),
                permission.getDescription(),
                permission.getParent() != null ? permission.getParent().getId() : null,
                permission.getChildren()
                        .stream()
                        .map(Permission::getId)
                        .toList(),
                permission.getCreatedAt(),
                permission.getUpdatedAt()
        );
    }
}
