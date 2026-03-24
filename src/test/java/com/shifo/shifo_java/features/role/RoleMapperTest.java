package com.shifo.shifo_java.features.role;

import com.shifo.shifo_java.features.permission.Permission;
import com.shifo.shifo_java.features.permission.PermissionMapper;
import com.shifo.shifo_java.features.role.dto.CreateRoleDto;
import com.shifo.shifo_java.features.role.dto.RoleDto;
import com.shifo.shifo_java.features.role.dto.UpdateRoleDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RoleMapperTest {

    private RoleMapper roleMapper;

    @BeforeEach
    void setUp() {
        roleMapper = new RoleMapper(new PermissionMapper());
    }

    @Test
    void shouldMapCreateRoleDtoToEntity() {
        CreateRoleDto dto = new CreateRoleDto();
        dto.setSlug("admin");
        dto.setName("Administrator");
        dto.setDescription("System administrator");

        Role role = roleMapper.toEntity(dto);

        assertThat(role.getSlug()).isEqualTo("admin");
        assertThat(role.getName()).isEqualTo("Administrator");
        assertThat(role.getDescription()).isEqualTo("System administrator");
    }

    @Test
    void shouldUpdateOnlyProvidedFields() {
        Role role = new Role();
        role.setSlug("doctor");
        role.setName("Doctor");
        role.setDescription("Doctor role");

        UpdateRoleDto dto = new UpdateRoleDto();
        dto.setName("Senior Doctor");

        roleMapper.updateEntity(dto, role);

        assertThat(role.getSlug()).isEqualTo("doctor");
        assertThat(role.getName()).isEqualTo("Senior Doctor");
        assertThat(role.getDescription()).isEqualTo("Doctor role");
    }

    @Test
    void shouldMapRoleToDtoWithPermissions() {
        Permission read = Permission.builder()
                .id(10L)
                .slug("users.read")
                .name("Read users")
                .description("Read users permission")
                .children(new HashSet<>())
                .build();

        Permission write = Permission.builder()
                .id(11L)
                .slug("users.write")
                .name("Write users")
                .description("Write users permission")
                .children(new HashSet<>())
                .build();

        Role role = new Role();
        role.setId(1L);
        role.setSlug("admin");
        role.setName("Administrator");
        role.setDescription("System administrator");
        role.setPermissions(new ArrayList<>(List.of(read, write)));
        role.setCreatedAt(Instant.parse("2026-03-24T12:00:00Z"));
        role.setUpdatedAt(Instant.parse("2026-03-24T13:00:00Z"));

        RoleDto dto = roleMapper.toDto(role);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getSlug()).isEqualTo("admin");
        assertThat(dto.getName()).isEqualTo("Administrator");
        assertThat(dto.getDescription()).isEqualTo("System administrator");
        assertThat(dto.getPermissions()).hasSize(2);
        assertThat(dto.getPermissions().get(0).getSlug()).isEqualTo("users.read");
        assertThat(dto.getPermissions().get(1).getSlug()).isEqualTo("users.write");
        assertThat(dto.getCreatedAt()).isEqualTo(Instant.parse("2026-03-24T12:00:00Z"));
        assertThat(dto.getUpdatedAt()).isEqualTo(Instant.parse("2026-03-24T13:00:00Z"));
    }
}
