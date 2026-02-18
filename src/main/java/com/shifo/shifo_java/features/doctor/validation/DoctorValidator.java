package com.shifo.shifo_java.features.doctor.validation;

import com.shifo.shifo_java.common.exceptions.BadRequestException;
import com.shifo.shifo_java.features.doctor.dto.CreateDoctorDto;
import com.shifo.shifo_java.features.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DoctorValidator {

    private final UserRepository userRepository;

    public void validateForCreation(CreateDoctorDto dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("auth.errors.emailAlreadyInUse");
        }

        if (dto.getUsername() != null &&
                userRepository.existsByUsername(dto.getUsername())) {

            throw new BadRequestException("auth.errors.usernameAlreadyInUse");
        }
    }
}
