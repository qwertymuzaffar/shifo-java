package com.shifo.shifo_java.security;

import com.shifo.shifo_java.features.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtService jwtService;

    public String generateToken(User user) {
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
}
