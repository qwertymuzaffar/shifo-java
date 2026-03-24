package com.shifo.shifo_java.features.role;

import com.shifo.shifo_java.features.permission.PermissionMapper;
import com.shifo.shifo_java.features.role.dto.CreateRoleDto;
import com.shifo.shifo_java.features.role.dto.RoleDto;
import com.shifo.shifo_java.features.role.dto.UpdateRoleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleMapper {

    private final PermissionMapper permissionMapper;

    public Role toEntity(CreateRoleDto dto) {
        Role role = new Role();
        role.setSlug(dto.getSlug());
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        return role;
    }

    public void updateEntity(UpdateRoleDto dto, Role role) {
        if (dto.getSlug() != null) {
            role.setSlug(dto.getSlug());
        }
        if (dto.getName() != null) {
            role.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            role.setDescription(dto.getDescription());
        }
    }

    public RoleDto toDto(Role role) {

        return new RoleDto(
                role.getId(),
                role.getSlug(),
                role.getName(),
                role.getDescription(),
                role.getPermissions().stream()
                        .map(permissionMapper::toDto)
                        .toList(),
                role.getCreatedAt(),
                role.getUpdatedAt()
        );
    }
}
