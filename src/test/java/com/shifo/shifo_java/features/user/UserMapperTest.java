package com.shifo.shifo_java.features.user;

import com.shifo.shifo_java.features.doctor.dto.UpdateDoctorDto;
import com.shifo.shifo_java.features.role.Role;
import com.shifo.shifo_java.features.user.dto.UpdateUserDto;
import com.shifo.shifo_java.features.user.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    void shouldReturnNullWhenUserIsNull() {
        assertThat(userMapper.mapUserToDto(null)).isNull();
    }

    @Test
    void shouldMapUserToDto() {
        Role role = new Role();
        role.setId(3L);

        User user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setPhone("+998901234567");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(role);
        user.setIsActive(true);
        user.setCreatedAt(Instant.parse("2026-03-24T10:00:00Z"));
        user.setUpdatedAt(Instant.parse("2026-03-24T12:00:00Z"));

        UserDto dto = userMapper.mapUserToDto(user);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getUsername()).isEqualTo("john");
        assertThat(dto.getEmail()).isEqualTo("john@example.com");
        assertThat(dto.getPhone()).isEqualTo("+998901234567");
        assertThat(dto.getFirstName()).isEqualTo("John");
        assertThat(dto.getLastName()).isEqualTo("Doe");
        assertThat(dto.getFullName()).isEqualTo("John Doe");
        assertThat(dto.getRoleId()).isEqualTo(3L);
        assertThat(dto.getIsActive()).isTrue();
        assertThat(dto.getCreatedAt()).isEqualTo(Instant.parse("2026-03-24T10:00:00Z"));
        assertThat(dto.getUpdatedAt()).isEqualTo(Instant.parse("2026-03-24T12:00:00Z"));
    }

    @Test
    void shouldUpdateOnlyProvidedUserFields() {
        User user = new User();
        user.setFirstName("Old");
        user.setLastName("Name");
        user.setPhone("+11111111111");
        user.setIsActive(true);

        UpdateUserDto dto = new UpdateUserDto();
        dto.setFirstName("New");
        dto.setPhone("+998901234567");
        dto.setIsActive(false);

        userMapper.updateEntity(dto, user);

        assertThat(user.getFirstName()).isEqualTo("New");
        assertThat(user.getLastName()).isEqualTo("Name");
        assertThat(user.getPhone()).isEqualTo("+998901234567");
        assertThat(user.getIsActive()).isFalse();
    }

    @Test
    void shouldMapDoctorUpdateToUserUpdateDto() {
        UpdateDoctorDto dto = new UpdateDoctorDto();
        dto.setFirstName("Alice");
        dto.setLastName("Smith");
        dto.setPhone("+998901234567");
        dto.setEmail("alice@example.com");
        dto.setPassword("Password123");

        UpdateUserDto userDto = userMapper.fromDoctorUpdate(dto);

        assertThat(userDto.getFirstName()).isEqualTo("Alice");
        assertThat(userDto.getLastName()).isEqualTo("Smith");
        assertThat(userDto.getPhone()).isEqualTo("+998901234567");
        assertThat(userDto.getEmail()).isEqualTo("alice@example.com");
        assertThat(userDto.getPassword()).isEqualTo("Password123");
    }

    @Test
    void shouldDetectWhenDoctorDtoHasUserChanges() {
        UpdateDoctorDto dto = new UpdateDoctorDto();
        dto.setEmail("alice@example.com");

        assertThat(userMapper.hasUserChanges(dto)).isTrue();
    }

    @Test
    void shouldDetectWhenDoctorDtoHasNoUserChanges() {
        UpdateDoctorDto dto = new UpdateDoctorDto();

        assertThat(userMapper.hasUserChanges(dto)).isFalse();
    }
}
