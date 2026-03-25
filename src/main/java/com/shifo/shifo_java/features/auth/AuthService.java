package com.shifo.shifo_java.features.auth;

import com.shifo.shifo_java.features.auth.dto.LoginRequest;
import com.shifo.shifo_java.features.auth.dto.LoginResponse;
import com.shifo.shifo_java.features.auth.dto.RegisterRequest;
import com.shifo.shifo_java.features.auth.dto.UserProfileResponse;
import com.shifo.shifo_java.features.user.User;
import com.shifo.shifo_java.common.exceptions.UnauthorizedException;
import com.shifo.shifo_java.features.user.repository.UserRepository;
import com.shifo.shifo_java.security.SecurityUtils;
import com.shifo.shifo_java.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthValidator authValidator;
    private final UserRepository userRepository;
    private final AuthMapper authMapper;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final SecurityUtils securityUtils;

    @Transactional
    public UserProfileResponse register(RegisterRequest request) {
        authValidator.validateRegister(request);

        User user = authMapper.toEntity(request);
        return authMapper.toProfile(userRepository.save(user));
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new UnauthorizedException("Account is disabled");
        }

        String token = tokenService.generateToken(user);

        return authMapper.toLoginResponse(user, token);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile() {
        User user = securityUtils.getCurrentUser();
        return authMapper.toProfile(user);
    }
}
