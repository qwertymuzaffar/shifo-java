package com.shifo.shifo_java.features.auth;

import com.shifo.shifo_java.common.exceptions.ConflictException;
import com.shifo.shifo_java.features.auth.dto.RegisterRequest;
import com.shifo.shifo_java.features.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthValidator {

    private final UserRepository userRepository;
    private final MessageSource messageSource;

    public void validateRegister(RegisterRequest request) {
        List<String> errors = new ArrayList<>();

        if (userRepository.existsByUsername(request.getUsername())) {
            errors.add(getMessage("auth.errors.userAlreadyExists"));
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            errors.add(getMessage("auth.errors.emailAlreadyInUse"));
        }

        if (!errors.isEmpty()) {
            throw new ConflictException(String.join(", ", errors));
        }
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, code, LocaleContextHolder.getLocale());
    }
}
