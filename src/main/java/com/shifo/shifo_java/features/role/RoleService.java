package com.shifo.shifo_java.features.role;

import com.shifo.shifo_java.features.permission.Permission;
import com.shifo.shifo_java.features.permission.PermissionRepository;
import com.shifo.shifo_java.features.role.dto.CreateRoleDto;
import com.shifo.shifo_java.features.role.dto.RoleDto;
import com.shifo.shifo_java.features.role.dto.UpdateRoleDto;
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
    private final RoleMapper roleMapper;

    @Transactional
    public RoleDto createRole(CreateRoleDto dto) {
        validateUniqueFields(dto.getSlug(), dto.getName(), null);

        Role role = roleMapper.toEntity(dto);
        return roleMapper.toDto(roleRepo.save(role));
    }

    @Transactional(readOnly = true)
    public List<RoleDto> getRoles() {
        return roleRepo.findAll().stream()
                .map(roleMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public RoleDto getRole(Long id) {
        return roleMapper.toDto(getRoleEntity(id));
    }

    @Transactional(readOnly = true)
    public Role findOneBySlug(String slug) {
        return roleRepo.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Role not found"));
    }

    @Transactional
    public RoleDto updateRole(Long id, UpdateRoleDto dto) {
        Role role = getRoleEntity(id);
        validateUniqueFields(dto.getSlug(), dto.getName(), id);

        roleMapper.updateEntity(dto, role);
        return roleMapper.toDto(roleRepo.save(role));
    }

    @Transactional
    public void deleteRole(Long id) {
        if (!roleRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found");
        }
        roleRepo.deleteById(id);
    }

    @Transactional
    public RoleDto assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        Role role = getRoleEntityWithPermissions(roleId);

        List<Permission> permissions = permRepo.findAllById(permissionIds);

        if (permissions.size() != permissionIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Some permissions not found");
        }

        role.getPermissions().clear();
        role.getPermissions().addAll(permissions);

        return roleMapper.toDto(role);
    }

    private Role getRoleEntity(Long id) {
        return roleRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Role not found"));
    }

    private Role getRoleEntityWithPermissions(Long id) {
        return roleRepo.findByIdWithPermissions(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Role not found"));
    }

    private void validateUniqueFields(String slug, String name, Long currentRoleId) {
        if (slug != null) {
            roleRepo.findBySlug(slug)
                    .filter(role -> !role.getId().equals(currentRoleId))
                    .ifPresent(role -> {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Role slug already exists");
                    });
        }

        if (name != null) {
            roleRepo.findByName(name)
                    .filter(role -> !role.getId().equals(currentRoleId))
                    .ifPresent(role -> {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Role name already exists");
                    });
        }
    }
}
