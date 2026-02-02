package com.shifo.shifo_java.features.role;

import com.shifo.shifo_java.features.permission.Permission;
import com.shifo.shifo_java.features.permission.PermissionRepository;
import com.shifo.shifo_java.features.role.dto.CreateRoleDto;
import com.shifo.shifo_java.features.role.dto.UpdateRoleDto;
import com.shifo.shifo_java.features.role.dto.AssignPermissionsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepo;
    private final PermissionRepository permRepo;

    // CREATE ROLE
    public Role createRole(CreateRoleDto dto) {
        Role role = new Role();
        role.setSlug(dto.getSlug());
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        return roleRepo.save(role);
    }

    // GET ALL ROLES
    public List<Role> getRoles() {
        return roleRepo.findAll();
    }

    // GET SINGLE ROLE
    public Role getRole(Long id) {
        return roleRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Role not found"));
    }

    // FIND BY SLUG
    public Role findOneBySlug(String slug) {
        return roleRepo.findBySlug(slug)
                .orElse(null);
    }

    // UPDATE ROLE
    public Role updateRole(Long id, UpdateRoleDto dto) {
        Role role = getRole(id);
        if (dto.getSlug() != null) role.setSlug(dto.getSlug());
        if (dto.getName() != null) role.setName(dto.getName());
        if (dto.getDescription() != null) role.setDescription(dto.getDescription());
        return roleRepo.save(role);
    }

    // DELETE ROLE
    public void deleteRole(Long id) {
        if (!roleRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found");
        }
        roleRepo.deleteById(id);
    }

    // ASSIGN PERMISSIONS TO ROLE
    @Transactional
    public Role assignPermissionsToRole(Long roleId, AssignPermissionsDto dto) {

        Role role = roleRepo.findById(roleId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Role not found"));

        List<Permission> perms =
                permRepo.findAllById(dto.getPermissionIds());

        role.setPermissions(perms);
        return roleRepo.save(role);
    }
}

