package com.shifo.shifo_java.features.auth;

import com.shifo.shifo_java.features.auth.dto.*;
import com.shifo.shifo_java.features.auth.AuthService;
import com.shifo.shifo_java.features.auth.dto.ApiResponse;
import com.shifo.shifo_java.features.auth.dto.LoginResponse;
import com.shifo.shifo_java.features.auth.dto.RegisterRequest;
import com.shifo.shifo_java.features.auth.dto.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<ApiResponse<Object>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", null));
    }

    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<UserProfileResponse> getProfile() {
        UserProfileResponse profile = authService.getProfile();
        return ResponseEntity.ok(profile);
    }
}
