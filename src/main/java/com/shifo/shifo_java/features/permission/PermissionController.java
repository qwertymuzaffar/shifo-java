package com.shifo.shifo_java.features.permission;

import com.shifo.shifo_java.features.permission.dto.CreatePermissionDto;
import com.shifo.shifo_java.features.permission.dto.PermissionDto;
import com.shifo.shifo_java.features.permission.dto.UpdatePermissionDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rbac/permissions")
@RequiredArgsConstructor
@Tag(name = "Permissions", description = "Permissions management")
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('user.create')")
    public Permission createPermission(@Valid @RequestBody CreatePermissionDto dto) {
        return permissionService.createPermission(dto);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('user.view')")
    public List<PermissionDto> getPermissions() {
        return permissionService.getPermissions();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user.view')")
    public PermissionDto getPermission(@PathVariable Long id) {
        return permissionService.getPermission(id);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('user.update')")
    public PermissionDto updatePermission(@PathVariable Long id,
                                          @Valid @RequestBody UpdatePermissionDto dto) {
        return permissionService.updatePermission(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('user.delete')")
    public void deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
    }
}
