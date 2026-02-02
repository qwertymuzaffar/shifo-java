package com.shifo.shifo_java.service;

import com.shifo.shifo_java.features.permission.dto.PermissionDto;
import com.shifo.shifo_java.features.role.dto.RoleDto;
import com.shifo.shifo_java.features.permission.Permission;
import com.shifo.shifo_java.common.exceptions.ResourceNotFoundException;
import com.shifo.shifo_java.repo.PermissionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RbacService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    // ---------------------------------------------------------
    // ROLE CRUD
    // ---------------------------------------------------------

    @Transactional
    public Role createRole(RoleDto dto) {
        Role role = new Role();
        role.setSlug(dto.getSlug());
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        return roleRepository.save(role);
    }

    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    public Role getRole(Long id) {
        return roleRepository.findByIdWithPermissions(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
    }

    public Optional<Role> findOneBySlug(String slug) {
        return roleRepository.findBySlug(slug);
    }

    @Transactional
    public Role updateRole(Long id, RoleDto dto) {
        Role role = getRole(id);

        if (dto.getName() != null) role.setName(dto.getName());
        if (dto.getDescription() != null) role.setDescription(dto.getDescription());
        if (dto.getSlug() != null) role.setSlug(dto.getSlug());

        return roleRepository.save(role);
    }

    @Transactional
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role not found");
        }
        roleRepository.deleteById(id);
    }

    // ---------------------------------------------------------
    // PERMISSION CRUD
    // ---------------------------------------------------------

    @Transactional
    public Permission createPermission(PermissionDto dto) {
        Permission permission = new Permission();
        permission.setSlug(dto.getSlug());
        permission.setName(dto.getName());
        permission.setDescription(dto.getDescription());

        if (dto.getParentId() != null) {
            Permission parent = permissionRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent permission not found"));
            permission.setParent(parent);
        }

        return permissionRepository.save(permission);
    }

    public List<Permission> getPermissions() {
        return permissionRepository.findRootPermissions();
    }

    public Permission getPermission(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
    }

    @Transactional
    public Permission updatePermission(Long id, PermissionDto dto) {
        Permission permission = getPermission(id);

        if (dto.getName() != null) permission.setName(dto.getName());
        if (dto.getSlug() != null) permission.setSlug(dto.getSlug());
        if (dto.getDescription() != null) permission.setDescription(dto.getDescription());

        return permissionRepository.save(permission);
    }

    @Transactional
    public void deletePermission(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Permission not found");
        }
        permissionRepository.deleteById(id);
    }

    // ---------------------------------------------------------
    // ASSIGN PERMISSIONS TO ROLE
    // ---------------------------------------------------------

    @Transactional
    public Role assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        Role role = roleRepository.findByIdWithPermissions(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        List<Permission> permissions = permissionRepository.findAllById(permissionIds);

        role.setPermissions(permissions);

        roleRepository.save(role);

        return getRole(roleId); // return with relations
    }
}

