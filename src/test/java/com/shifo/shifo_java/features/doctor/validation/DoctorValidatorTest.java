package com.shifo.shifo_java.features.doctor.validation;

import com.shifo.shifo_java.common.exceptions.BadRequestException;
import com.shifo.shifo_java.features.doctor.dto.CreateDoctorDto;
import com.shifo.shifo_java.features.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoctorValidatorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DoctorValidator validator;

    private CreateDoctorDto dto(String email, String username) {
        CreateDoctorDto dto = new CreateDoctorDto();
        dto.setEmail(email);
        dto.setUsername(username);
        return dto;
    }

    @Test
    void shouldPassWhenEmailAndUsernameFree() {
        CreateDoctorDto dto = dto("doc@example.com", "docuser");
        when(userRepository.existsByEmail("doc@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("docuser")).thenReturn(false);

        assertThatCode(() -> validator.validateForCreation(dto)).doesNotThrowAnyException();
    }

    @Test
    void shouldPassWhenEmailFreeAndUsernameNull() {
        CreateDoctorDto dto = dto("doc@example.com", null);
        when(userRepository.existsByEmail("doc@example.com")).thenReturn(false);

        assertThatCode(() -> validator.validateForCreation(dto)).doesNotThrowAnyException();
        verify(userRepository, never()).existsByUsername(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void shouldThrowWhenEmailTaken() {
        CreateDoctorDto dto = dto("doc@example.com", "docuser");
        when(userRepository.existsByEmail("doc@example.com")).thenReturn(true);

        assertThatThrownBy(() -> validator.validateForCreation(dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("emailAlreadyInUse");
        verify(userRepository, never()).existsByUsername(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void shouldThrowWhenUsernameTaken() {
        CreateDoctorDto dto = dto("doc@example.com", "docuser");
        when(userRepository.existsByEmail("doc@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("docuser")).thenReturn(true);

        assertThatThrownBy(() -> validator.validateForCreation(dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("usernameAlreadyInUse");
    }
}
