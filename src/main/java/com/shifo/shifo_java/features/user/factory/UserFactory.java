package com.shifo.shifo_java.features.user.factory;

import com.shifo.shifo_java.features.doctor.dto.CreateDoctorDto;
import com.shifo.shifo_java.features.role.Role;
import com.shifo.shifo_java.features.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {

    public User createDoctorUser(CreateDoctorDto dto, Role role) {
        return User.builder()
                .email(dto.getEmail())
                .username(dto.getUsername())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phone(dto.getPhone())
                .password(dto.getPassword())
                .role(role)
                .build();
    }
}

