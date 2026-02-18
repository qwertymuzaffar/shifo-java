package com.shifo.shifo_java.features.auth;

import com.shifo.shifo_java.features.auth.dto.LoginRequest;
import com.shifo.shifo_java.features.auth.dto.LoginResponse;
import com.shifo.shifo_java.features.auth.dto.RegisterRequest;
import com.shifo.shifo_java.features.auth.dto.UserProfileResponse;
import com.shifo.shifo_java.features.role.Role;
import com.shifo.shifo_java.features.user.User;
import com.shifo.shifo_java.common.exceptions.ConflictException;
import com.shifo.shifo_java.common.exceptions.UnauthorizedException;
import com.shifo.shifo_java.features.role.RoleRepository;
import com.shifo.shifo_java.features.user.repository.UserRepository;
import com.shifo.shifo_java.security.JwtService;
import com.shifo.shifo_java.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final MessageSource messageSource;
    private final SecurityUtils securityUtils;

    @Transactional
    public User register(RegisterRequest request) {
        List<String> errors = new ArrayList<>();

        if (userRepository.existsByUsername(request.getUsername())) {
            errors.add(getMessage("auth.errors.userAlreadyExists"));
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            errors.add(getMessage("auth.errors.emailAlreadyInUse"));
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ConflictException(getMessage("users.errors.invalidRole")));

        if (!errors.isEmpty()) {
            throw new ConflictException(String.join(", ", errors));
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .isActive(true)
                .build();

        return userRepository.save(user);
    }

    private String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());

        if (user.getRole() != null) {
            claims.put("role", user.getRole().getSlug());
        }

        return jwtService.generateToken(
                claims,
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getUsername())
                        .password(user.getPassword())
                        .authorities("USER")
                        .build()
        );
    }

    private LoginResponse.UserInfo mapToUserInfo(User user) {
        return LoginResponse.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roleId(user.getRole().getId())
                .role(user.getRole() != null
                        ? LoginResponse.RoleInfo.builder()
                        .id(user.getRole().getId())
                        .slug(user.getRole().getSlug())
                        .name(user.getRole().getName())
                        .build()
                        : null)
                .build();
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername().trim();
        String rawPassword = request.getPassword();

        User user = userRepository.findByUsername(username)
                .orElseThrow(this::invalidCredentials);

        if (!user.getIsActive()) {
            throw new UnauthorizedException(getMessage("auth.errors.accountBlocked"));
        }

        String storedPassword = user.getPassword();

        // BCrypt password
        if (!passwordEncoder.matches(rawPassword, storedPassword)) {
            throw invalidCredentials();
        }

        String token = generateToken(user);

        return LoginResponse.builder()
                .user(mapToUserInfo(user))
                .access_token(token)
                .build();
    }

    private UnauthorizedException invalidCredentials() {
        return new UnauthorizedException(getMessage("auth.errors.invalidCredentials"));
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile() {
        User user = securityUtils.getCurrentUser();

        if (user == null) {
            throw new UnauthorizedException(getMessage("auth.errors.userNotFound"));
        }

        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole() != null ? user.getRole().getSlug() : null)
                .roleName(user.getRole() != null ? user.getRole().getName() : null)
                .roleId(user.getRole().getId())
                .permissions(user.getRole() != null && user.getRole().getPermissions() != null
                        ? user.getRole().getPermissions().stream()
                        .map(p -> p.getSlug())
                        .collect(Collectors.toList())
                        : List.of())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, code, LocaleContextHolder.getLocale());
    }
}
