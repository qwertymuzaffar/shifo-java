package com.shifo.shifo_java.features.permission;

import com.shifo.shifo_java.features.permission.dto.CreatePermissionDto;
import com.shifo.shifo_java.features.permission.dto.PermissionDto;
import com.shifo.shifo_java.features.permission.dto.UpdatePermissionDto;
import com.shifo.shifo_java.features.role.Role;
import com.shifo.shifo_java.features.role.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;
    private final RoleRepository roleRepository;

    public PermissionService(PermissionRepository permissionRepository, PermissionMapper permissionMapper, RoleRepository roleRepository) {
        this.permissionRepository = permissionRepository;
        this.permissionMapper = permissionMapper;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public Permission createPermission(CreatePermissionDto dto) {

        if (permissionRepository.existsBySlug(dto.getSlug())) {
            throw new IllegalArgumentException("Permission with this slug already exists");
        }

        Permission permission = new Permission();
        permission.setSlug(dto.getSlug());
        permission.setName(dto.getName());
        permission.setDescription(dto.getDescription());

        // Handle hierarchy (parent)
        if (dto.getParent().getId() != null) {
            Permission parent = permissionRepository.findById(dto.getParent().getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent permission not found"));

            permission.setParent(parent);
        }

        return permissionRepository.save(permission);
    }

    @Transactional(readOnly = true)
    public List<PermissionDto> getPermissions() {
        return permissionRepository.findAll()
                .stream()
                .map(permissionMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public PermissionDto getPermission(Long id) {

        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found"));

        return permissionMapper.toDto(permission);
    }

    @Transactional
    public PermissionDto updatePermission(Long id, UpdatePermissionDto dto) {

        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found"));

        if (dto.getSlug() != null) {
            permission.setSlug(dto.getSlug());
        }

        if (dto.getName() != null) {
            permission.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            permission.setDescription(dto.getDescription());
        }

        // parent handling
        if (dto.getParentId() != null) {
            if (dto.getParentId().equals(permission.getId())) {
                throw new IllegalArgumentException("Permission cannot be its own parent");
            }

            Permission parent = permissionRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent permission not found"));

            permission.setParent(parent);

        } else if (dto.getParentId() == null) {
            // explicit null → detach parent (move to root)
            permission.setParent(null);
        }

        return permissionMapper.toDto(permission);
    }

    @Transactional
    public void deletePermission(Long id) {

        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found"));

        // Optional safety check: prevent deleting if assigned to roles
        if (!permission.getRoles().isEmpty()) {
            throw new IllegalStateException("Cannot delete permission assigned to roles");
        }

        permissionRepository.delete(permission);
    }

    @Transactional
    public void assignPermissionsToRoleBySlug(Long roleId, List<String> slugs) {

        Role role = roleRepository.findByIdWithPermissions(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        List<Permission> permissions = permissionRepository.findBySlugIn(slugs);

        if (permissions.size() != slugs.size()) {
            throw new IllegalArgumentException("Some permissions not found in DB");
        }

        role.getPermissions().clear();
        role.getPermissions().addAll(permissions);
    }
}
