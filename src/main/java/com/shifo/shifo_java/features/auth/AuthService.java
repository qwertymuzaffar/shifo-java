package com.shifo.shifo_java.features.auth;

import com.shifo.shifo_java.features.auth.dto.LoginDto;
import com.shifo.shifo_java.features.role.Role;
import com.shifo.shifo_java.features.role.RoleRepository;
import com.shifo.shifo_java.features.user.UserRepository;
import com.shifo.shifo_java.features.user.dto.CreateUserDto;
import com.shifo.shifo_java.features.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    // ----------------------------------------------------------
    // REGISTER
    // ----------------------------------------------------------
    public User register(CreateUserDto dto) {

        List<String> errors = new ArrayList<>();

        if (userRepository.existsByUsername(dto.getUsername())) {
            errors.add("Пользователь с таким именем уже существует");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            errors.add("Пользователь с таким email уже существует");
        }

        Role role = null;
        if (dto.getRoleId() != null) {
            role = roleRepository.findById(dto.getRoleId())
                    .orElse(null);

            if (role == null) {
                errors.add("Роль с таким ID не существует");
            }
        }

        if (!errors.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.join(", ", errors));
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setRole(role);
        user.setRoleId(dto.getRoleId());
        user.setIsActive(true);

        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        return userRepository.save(user);
    }

    // ----------------------------------------------------------
    // LOGIN
    // ----------------------------------------------------------
    public Map<String, Object> login(LoginDto dto) {

        String username = dto.getUsername().trim();
        String password = dto.getPassword().trim();

        User user = userRepository.findByUsernameWithRole(username)
                .orElse(null);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Неверное имя пользователя или пароль");
        }

        if (!user.getIsActive()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Пользователь заблокирован");
        }

        Map<String, Object> payload = Map.of(
                "sub", user.getId(),
                "username", user.getUsername(),
                "role", user.getRole().getSlug()
        );

        String token = jwtService.generateToken(payload);

        return Map.of(
                "user", Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "email", user.getEmail(),
                        "fullName", (user.getFirstName() + " " + user.getLastName()).trim(),
                        "role", user.getRole().getSlug(),
                        "roleId", user.getRoleId()
                ),
                "access_token", token
        );
    }

    // ----------------------------------------------------------
    // VALIDATE USER (used by security filters)
    // ----------------------------------------------------------
    public User validateUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElse(null);

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }

        return null;
    }

    // ----------------------------------------------------------
    // USER PROFILE
    // ----------------------------------------------------------
    public Map<String, Object> getProfile(Long userId) {

        User user = userRepository.findByIdWithRoleAndPermissions(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Пользователь не найден"
                ));

        return Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "fullName", (user.getFirstName() + " " + user.getLastName()).trim(),
                "role", user.getRole() != null ? user.getRole().getSlug() : null,
                "roleName", user.getRole() != null ? user.getRole().getName() : null,
                "roleId", user.getRoleId(),
                "permissions", user.getRole() != null
                        ? user.getRole().getPermissions().stream().map(p -> p.getSlug()).toList()
                        : List.of(),
                "isActive", user.getIsActive(),
                "createdAt", user.getCreatedAt(),
                "updatedAt", user.getUpdatedAt()
        );
    }
}

