package com.shifo.shifo_java.features.auth;

import com.shifo.shifo_java.common.exceptions.UnauthorizedException;
import com.shifo.shifo_java.features.auth.dto.LoginResponse;
import com.shifo.shifo_java.features.auth.dto.RegisterRequest;
import com.shifo.shifo_java.features.auth.dto.UserProfileResponse;
import com.shifo.shifo_java.features.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthMapper {

    private final PasswordEncoder passwordEncoder;

    public User toEntity(RegisterRequest request) {
        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
    }

    public LoginResponse toLoginResponse(User user, String token) {
        return LoginResponse.builder()
                .user(toUserInfo(user))
                .access_token(token)
                .build();
    }

    public LoginResponse.UserInfo toUserInfo(User user) {
        if (user == null) {
            return null;
        }

        return LoginResponse.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roleId(user.getRole() != null ? user.getRole().getId() : null)
                .role(
                        user.getRole() != null
                                ? LoginResponse.RoleInfo.builder()
                                .id(user.getRole().getId())
                                .slug(user.getRole().getSlug())
                                .name(user.getRole().getName())
                                .build()
                                : null
                )
                .build();
    }

    public UserProfileResponse toProfile(User user) {
        if (user == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())

                .roleId(user.getRole() != null ? user.getRole().getId() : null)
                .role(user.getRole() != null ? user.getRole().getSlug() : null)
                .roleName(user.getRole() != null ? user.getRole().getName() : null)

                .permissions(
                        user.getRole() != null && user.getRole().getPermissions() != null
                                ? user.getRole().getPermissions()
                                .stream()
                                .map(p -> p.getSlug())
                                .toList()
                                : List.of()
                )

                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
