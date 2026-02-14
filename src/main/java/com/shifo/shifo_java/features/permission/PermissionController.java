package com.shifo.shifo_java.features.permission;

import com.shifo.shifo_java.features.permission.dto.CreatePermissionDto;
import com.shifo.shifo_java.features.permission.dto.PermissionDto;
import com.shifo.shifo_java.features.permission.dto.UpdatePermissionDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public Permission createPermission(@Valid @RequestBody CreatePermissionDto dto) {
        return permissionService.createPermission(dto);
    }

    @GetMapping
    public List<PermissionDto> getPermissions() {
        return permissionService.getPermissions();
    }

    @GetMapping("/{id}")
    public PermissionDto getPermission(@PathVariable Long id) {
        return permissionService.getPermission(id);
    }

    @PatchMapping("/{id}")
    public PermissionDto updatePermission(@PathVariable Long id,
                                          @Valid @RequestBody UpdatePermissionDto dto) {
        return permissionService.updatePermission(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
    }
}
