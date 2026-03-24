package com.shifo.shifo_java.features.role;

import com.shifo.shifo_java.features.permission.Permission;
import com.shifo.shifo_java.features.permission.PermissionMapper;
import com.shifo.shifo_java.features.permission.PermissionRepository;
import com.shifo.shifo_java.features.role.dto.CreateRoleDto;
import com.shifo.shifo_java.features.role.dto.RoleDto;
import com.shifo.shifo_java.features.role.dto.UpdateRoleDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoleServiceTest {

    private final RoleRepositoryState roleRepositoryState = new RoleRepositoryState();
    private final PermissionRepositoryState permissionRepositoryState = new PermissionRepositoryState();

    private RoleService roleService;

    @BeforeEach
    void setUp() {
        roleService = new RoleService(
                roleRepository(),
                permissionRepository(),
                new RoleMapper(new PermissionMapper())
        );
    }

    @Test
    void shouldCreateRoleDtoWhenSlugAndNameAreUnique() {
        CreateRoleDto request = new CreateRoleDto();
        request.setSlug("admin");
        request.setName("Administrator");
        request.setDescription("System administrator");

        Role savedRole = new Role();
        savedRole.setId(1L);
        savedRole.setSlug("admin");
        savedRole.setName("Administrator");
        savedRole.setDescription("System administrator");
        savedRole.setPermissions(new ArrayList<>());
        roleRepositoryState.savedRoleToReturn = savedRole;

        RoleDto response = roleService.createRole(request);

        assertThat(roleRepositoryState.savedRole).isNotNull();
        assertThat(roleRepositoryState.savedRole.getSlug()).isEqualTo("admin");
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getSlug()).isEqualTo("admin");
        assertThat(response.getName()).isEqualTo("Administrator");
    }

    @Test
    void shouldThrowConflictWhenCreatingRoleWithDuplicateSlug() {
        CreateRoleDto request = new CreateRoleDto();
        request.setSlug("admin");

        Role existingRole = new Role();
        existingRole.setId(9L);
        existingRole.setSlug("admin");
        roleRepositoryState.roleBySlug = existingRole;

        assertThatThrownBy(() -> roleService.createRole(request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseException = (ResponseStatusException) exception;
                    assertThat(responseException.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(responseException.getReason()).isEqualTo("Role slug already exists");
                });
    }

    @Test
    void shouldThrowConflictWhenUpdatingRoleWithDuplicateName() {
        UpdateRoleDto request = new UpdateRoleDto();
        request.setName("Manager");

        Role currentRole = new Role();
        currentRole.setId(1L);
        currentRole.setSlug("doctor");
        currentRole.setName("Doctor");
        currentRole.setPermissions(new ArrayList<>());
        roleRepositoryState.roleById = currentRole;

        Role existingRole = new Role();
        existingRole.setId(2L);
        existingRole.setName("Manager");
        roleRepositoryState.roleByName = existingRole;

        assertThatThrownBy(() -> roleService.updateRole(1L, request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseException = (ResponseStatusException) exception;
                    assertThat(responseException.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(responseException.getReason()).isEqualTo("Role name already exists");
                });
    }

    @Test
    void shouldAssignPermissionsAndReturnDto() {
        Role role = new Role();
        role.setId(1L);
        role.setSlug("admin");
        role.setName("Administrator");
        role.setPermissions(new ArrayList<>());
        roleRepositoryState.roleByIdWithPermissions = role;

        Permission read = Permission.builder().id(10L).slug("users.read").name("Read users").build();
        Permission write = Permission.builder().id(11L).slug("users.write").name("Write users").build();
        permissionRepositoryState.permissions = List.of(read, write);

        RoleDto response = roleService.assignPermissionsToRole(1L, List.of(10L, 11L));

        assertThat(role.getPermissions()).containsExactly(read, write);
        assertThat(response.getPermissions()).hasSize(2);
        assertThat(response.getPermissions().get(0).getSlug()).isEqualTo("users.read");
        assertThat(response.getPermissions().get(1).getSlug()).isEqualTo("users.write");
    }

    @Test
    void shouldThrowBadRequestWhenSomePermissionsAreMissing() {
        Role role = new Role();
        role.setId(1L);
        role.setPermissions(new ArrayList<>());
        roleRepositoryState.roleByIdWithPermissions = role;

        Permission onlyOne = Permission.builder().id(10L).slug("users.read").build();
        permissionRepositoryState.permissions = List.of(onlyOne);

        assertThatThrownBy(() -> roleService.assignPermissionsToRole(1L, List.of(10L, 11L)))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseException = (ResponseStatusException) exception;
                    assertThat(responseException.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(responseException.getReason()).isEqualTo("Some permissions not found");
                });
    }

    private RoleRepository roleRepository() {
        return (RoleRepository) Proxy.newProxyInstance(
                RoleRepository.class.getClassLoader(),
                new Class[]{RoleRepository.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "save" -> {
                        roleRepositoryState.savedRole = (Role) args[0];
                        yield roleRepositoryState.savedRoleToReturn != null ? roleRepositoryState.savedRoleToReturn : roleRepositoryState.savedRole;
                    }
                    case "findAll" -> roleRepositoryState.roles;
                    case "findById" -> Optional.ofNullable(roleRepositoryState.roleById);
                    case "findByIdWithPermissions" -> Optional.ofNullable(roleRepositoryState.roleByIdWithPermissions);
                    case "findBySlug" -> Optional.ofNullable(roleRepositoryState.roleBySlug);
                    case "findByName" -> Optional.ofNullable(roleRepositoryState.roleByName);
                    case "existsById" -> roleRepositoryState.existsById;
                    case "deleteById" -> {
                        roleRepositoryState.deletedRoleId = (Long) args[0];
                        yield null;
                    }
                    case "toString" -> "RoleRepositoryTestProxy";
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> proxy == args[0];
                    default -> throw new UnsupportedOperationException(method.getName());
                }
        );
    }

    private PermissionRepository permissionRepository() {
        return (PermissionRepository) Proxy.newProxyInstance(
                PermissionRepository.class.getClassLoader(),
                new Class[]{PermissionRepository.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "findAllById" -> permissionRepositoryState.permissions;
                    case "toString" -> "PermissionRepositoryTestProxy";
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> proxy == args[0];
                    default -> throw new UnsupportedOperationException(method.getName());
                }
        );
    }

    private static final class RoleRepositoryState {
        private Role savedRole;
        private Role savedRoleToReturn;
        private List<Role> roles = List.of();
        private Role roleById;
        private Role roleByIdWithPermissions;
        private Role roleBySlug;
        private Role roleByName;
        private boolean existsById;
        private Long deletedRoleId;
    }

    private static final class PermissionRepositoryState {
        private List<Permission> permissions = List.of();
    }
}
