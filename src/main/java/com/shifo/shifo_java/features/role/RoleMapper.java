package com.shifo.shifo_java.features.role;

import com.shifo.shifo_java.features.permission.Permission;
import com.shifo.shifo_java.features.role.dto.RoleDto;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    public RoleDto toDto(Role role) {

        return new RoleDto(
                role.getId(),
                role.getSlug(),
                role.getName(),
                role.getDescription(),
                role.getPermissions(),
                role.getCreatedAt(),
                role.getUpdatedAt()
        );
    }
}
