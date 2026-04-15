package com.shifo.shifo_java.features.auth;

import com.shifo.shifo_java.common.exceptions.UnauthorizedException;
import com.shifo.shifo_java.features.auth.dto.LoginResponse;
import com.shifo.shifo_java.features.auth.dto.RegisterRequest;
import com.shifo.shifo_java.features.auth.dto.UserProfileResponse;
import com.shifo.shifo_java.features.permission.Permission;
import com.shifo.shifo_java.features.role.Role;
import com.shifo.shifo_java.features.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthMapperTest {

    private AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        authMapper = new AuthMapper(passwordEncoder);
    }

    @Test
    void shouldMapRegisterRequestToUserEntity() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");
        request.setEmail("john@example.com");
        request.setPassword("secret123");
        request.setPhone("+123456789");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setRoleId(10L);

        User user = authMapper.toEntity(request);

        assertThat(user.getUsername()).isEqualTo("john");
        assertThat(user.getEmail()).isEqualTo("john@example.com");
        assertThat(passwordEncoder.matches("secret123", user.getPassword())).isTrue();
        assertThat(user.getPhone()).isNull();
        assertThat(user.getFirstName()).isNull();
        assertThat(user.getLastName()).isNull();
        assertThat(user.getRole()).isNull();
    }

    @Test
    void shouldReturnNullUserInfoWhenUserIsNull() {
        assertThat(authMapper.toUserInfo(null)).isNull();
    }

    @Test
    void shouldMapUserToLoginResponseWithNestedRole() {
        Role role = new Role();
        role.setId(3L);
        role.setSlug("admin");
        role.setName("Administrator");

        User user = new User();
        user.setId(7L);
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(role);

        LoginResponse response = authMapper.toLoginResponse(user, "jwt-token");

        assertThat(response.getAccess_token()).isEqualTo("jwt-token");
        assertThat(response.getUser()).isNotNull();
        assertThat(response.getUser().getId()).isEqualTo(7L);
        assertThat(response.getUser().getUsername()).isEqualTo("john");
        assertThat(response.getUser().getEmail()).isEqualTo("john@example.com");
        assertThat(response.getUser().getFullName()).isEqualTo("John Doe");
        assertThat(response.getUser().getRoleId()).isEqualTo(3L);
        assertThat(response.getUser().getRole()).isEqualTo(
                LoginResponse.RoleInfo.builder()
                        .id(3L)
                        .slug("admin")
                        .name("Administrator")
                        .build()
        );
    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenProfileUserIsNull() {
        assertThatThrownBy(() -> authMapper.toProfile(null))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("User not authenticated");
    }

    @Test
    void shouldMapUserToProfileAndFlattenPermissions() {
        Permission read = Permission.builder().slug("users.read").build();
        Permission write = Permission.builder().slug("users.write").build();

        Role role = new Role();
        role.setId(5L);
        role.setSlug("manager");
        role.setName("Manager");
        role.setPermissions(List.of(read, write));

        Instant createdAt = Instant.parse("2026-03-23T12:00:00Z");
        Instant updatedAt = Instant.parse("2026-03-23T15:00:00Z");

        User user = new User();
        user.setId(11L);
        user.setUsername("alice");
        user.setEmail("alice@example.com");
        user.setFirstName("Alice");
        user.setLastName("Smith");
        user.setRole(role);
        user.setIsActive(true);
        user.setCreatedAt(createdAt);
        user.setUpdatedAt(updatedAt);

        UserProfileResponse response = authMapper.toProfile(user);

        assertThat(response.getId()).isEqualTo(11L);
        assertThat(response.getUsername()).isEqualTo("alice");
        assertThat(response.getEmail()).isEqualTo("alice@example.com");
        assertThat(response.getFullName()).isEqualTo("Alice Smith");
        assertThat(response.getRoleId()).isEqualTo(5L);
        assertThat(response.getRole()).isEqualTo("manager");
        assertThat(response.getRoleName()).isEqualTo("Manager");
        assertThat(response.getPermissions()).containsExactly("users.read", "users.write");
        assertThat(response.getIsActive()).isTrue();
        assertThat(response.getCreatedAt()).isEqualTo(createdAt);
        assertThat(response.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    void shouldMapProfileWithEmptyPermissionsWhenRoleIsMissing() {
        User user = new User();
        user.setId(12L);
        user.setUsername("no-role");
        user.setEmail("no-role@example.com");
        user.setIsActive(false);

        UserProfileResponse response = authMapper.toProfile(user);

        assertThat(response.getRoleId()).isNull();
        assertThat(response.getRole()).isNull();
        assertThat(response.getRoleName()).isNull();
        assertThat(response.getPermissions()).isEmpty();
        assertThat(response.getIsActive()).isFalse();
    }
}
