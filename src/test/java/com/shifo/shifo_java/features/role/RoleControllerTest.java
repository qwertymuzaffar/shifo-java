package com.shifo.shifo_java.features.role;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shifo.shifo_java.config.GlobalExceptionHandler;
import com.shifo.shifo_java.features.permission.dto.PermissionDto;
import com.shifo.shifo_java.features.role.dto.AssignPermissionsRequestDto;
import com.shifo.shifo_java.features.role.dto.CreateRoleDto;
import com.shifo.shifo_java.features.role.dto.RoleDto;
import com.shifo.shifo_java.features.role.dto.UpdateRoleDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RoleControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private FakeRoleService roleService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        roleService = new FakeRoleService();
        mockMvc = MockMvcBuilders.standaloneSetup(new RoleController(roleService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldCreateRole() throws Exception {
        CreateRoleDto request = new CreateRoleDto();
        request.setSlug("admin");
        request.setName("Administrator");
        request.setDescription("System administrator");

        roleService.createRoleResponse = roleDto(1L, "admin", "Administrator");

        mockMvc.perform(post("/api/rbac/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.slug").value("admin"))
                .andExpect(jsonPath("$.name").value("Administrator"));
    }

    @Test
    void shouldReturnAllRoles() throws Exception {
        roleService.getRolesResponse = List.of(
                roleDto(1L, "admin", "Administrator"),
                roleDto(2L, "doctor", "Doctor")
        );

        mockMvc.perform(get("/api/rbac/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].slug").value("admin"))
                .andExpect(jsonPath("$[1].slug").value("doctor"));
    }

    @Test
    void shouldReturnRoleById() throws Exception {
        roleService.getRoleResponse = roleDto(1L, "admin", "Administrator");

        mockMvc.perform(get("/api/rbac/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.slug").value("admin"));
    }

    @Test
    void shouldUpdateRole() throws Exception {
        UpdateRoleDto request = new UpdateRoleDto();
        request.setName("Updated role");

        roleService.updateRoleResponse = roleDto(1L, "admin", "Updated role");

        mockMvc.perform(patch("/api/rbac/roles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated role"));
    }

    @Test
    void shouldDeleteRole() throws Exception {
        mockMvc.perform(delete("/api/rbac/roles/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldAssignPermissions() throws Exception {
        AssignPermissionsRequestDto request = new AssignPermissionsRequestDto();
        request.setPermissionIds(List.of(10L, 11L));

        roleService.assignPermissionsResponse = roleDtoWithPermissions();

        mockMvc.perform(put("/api/rbac/roles/1/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.permissions[0].slug").value("users.read"))
                .andExpect(jsonPath("$.permissions[1].slug").value("users.write"));
    }

    @Test
    void shouldReturnBadRequestWhenPermissionIdsAreEmpty() throws Exception {
        AssignPermissionsRequestDto request = new AssignPermissionsRequestDto();
        request.setPermissionIds(List.of());

        mockMvc.perform(put("/api/rbac/roles/1/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.permissionIds").value("permissionIds cannot be empty"));
    }

    private RoleDto roleDto(Long id, String slug, String name) {
        return new RoleDto(
                id,
                slug,
                name,
                name + " description",
                List.of(),
                Instant.parse("2026-03-24T12:00:00Z"),
                Instant.parse("2026-03-24T13:00:00Z")
        );
    }

    private RoleDto roleDtoWithPermissions() {
        return new RoleDto(
                1L,
                "admin",
                "Administrator",
                "System administrator",
                List.of(
                        new PermissionDto(10L, "users.read", "Read users", "Read users permission", null, List.of(), Instant.parse("2026-03-24T12:00:00Z"), Instant.parse("2026-03-24T13:00:00Z")),
                        new PermissionDto(11L, "users.write", "Write users", "Write users permission", null, List.of(), Instant.parse("2026-03-24T12:00:00Z"), Instant.parse("2026-03-24T13:00:00Z"))
                ),
                Instant.parse("2026-03-24T12:00:00Z"),
                Instant.parse("2026-03-24T13:00:00Z")
        );
    }

    private static final class FakeRoleService extends RoleService {

        private RoleDto createRoleResponse;
        private List<RoleDto> getRolesResponse = List.of();
        private RoleDto getRoleResponse;
        private RoleDto updateRoleResponse;
        private RoleDto assignPermissionsResponse;

        private FakeRoleService() {
            super(null, null, null);
        }

        @Override
        public RoleDto createRole(CreateRoleDto dto) {
            return createRoleResponse;
        }

        @Override
        public List<RoleDto> getRoles() {
            return getRolesResponse;
        }

        @Override
        public RoleDto getRole(Long id) {
            return getRoleResponse;
        }

        @Override
        public RoleDto updateRole(Long id, UpdateRoleDto dto) {
            return updateRoleResponse;
        }

        @Override
        public void deleteRole(Long id) {
        }

        @Override
        public RoleDto assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
            return assignPermissionsResponse;
        }
    }
}
