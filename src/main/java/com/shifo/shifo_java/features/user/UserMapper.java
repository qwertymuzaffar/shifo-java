package com.shifo.shifo_java.features.user;

import com.shifo.shifo_java.features.doctor.dto.UpdateDoctorDto;
import com.shifo.shifo_java.features.user.dto.UpdateUserDto;
import com.shifo.shifo_java.features.user.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDto mapUserToDto(User user) {
        if (user == null) {
            return null;
        }

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setFullName(user.getFullName());
        dto.setRoleId(user.getRole() != null ? user.getRole().getId() : null);
        dto.setIsActive(user.getIsActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    public UpdateUserDto fromDoctorUpdate(UpdateDoctorDto dto) {

        UpdateUserDto userDto = new UpdateUserDto();

        userDto.setFirstName(dto.getFirstName());
        userDto.setLastName(dto.getLastName());
        userDto.setPhone(dto.getPhone());
        userDto.setEmail(dto.getEmail());
        userDto.setPassword(dto.getPassword());

        return userDto;
    }

    public boolean hasUserChanges(UpdateDoctorDto dto) {
        return dto.getFirstName() != null ||
                dto.getLastName() != null ||
                dto.getPhone() != null ||
                dto.getEmail() != null ||
                dto.getPassword() != null;
    }
}
