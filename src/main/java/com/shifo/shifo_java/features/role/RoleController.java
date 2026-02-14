package com.shifo.shifo_java.features.role;

import com.shifo.shifo_java.features.role.dto.AssignPermissionsRequestDto;
import com.shifo.shifo_java.features.role.dto.CreateRoleDto;
import com.shifo.shifo_java.features.role.dto.RoleDto;
import com.shifo.shifo_java.features.role.dto.UpdateRoleDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rbac/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Role createRole(@Valid @RequestBody CreateRoleDto request) {
        return roleService.createRole(request);
    }

    @GetMapping
    public List<Role> getRoles() {
        return roleService.getRoles();
    }

    @GetMapping("/{id}")
    public Role getRole(@PathVariable Long id) {
        return roleService.getRole(id);
    }

    @PatchMapping("/{id}")
    public Role updateRole(@PathVariable Long id,
                           @Valid @RequestBody UpdateRoleDto dto) {
        return roleService.updateRole(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
    }

    @PutMapping("/{id}/permissions")
    public RoleDto assignPermissions(@PathVariable Long id,
                                     @RequestBody AssignPermissionsRequestDto request) {
        return roleService.assignPermissionsToRole(id, request.getPermissionIds());
    }

}
