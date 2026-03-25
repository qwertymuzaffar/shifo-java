package com.shifo.shifo_java.features.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shifo.shifo_java.config.GlobalExceptionHandler;
import com.shifo.shifo_java.features.auth.dto.LoginRequest;
import com.shifo.shifo_java.features.auth.dto.LoginResponse;
import com.shifo.shifo_java.features.auth.dto.RegisterRequest;
import com.shifo.shifo_java.features.auth.dto.UserProfileResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private FakeAuthService authService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        authService = new FakeAuthService();
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(authService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldRegisterUser() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");
        request.setEmail("john@example.com");
        request.setPassword("secret123");
        request.setRoleId(1L);
        authService.registerResponse = UserProfileResponse.builder()
                .username("john")
                .email("john@example.com")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.data.username").value("john"))
                .andExpect(jsonPath("$.data.email").value("john@example.com"));
    }

    @Test
    void shouldReturnBadRequestWhenRegisterPayloadIsInvalid() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("invalid-email");
        request.setPassword("123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").value("Username is required"))
                .andExpect(jsonPath("$.email").value("Email must be valid"))
                .andExpect(jsonPath("$.password").value("Password must be at least 6 characters"))
                .andExpect(jsonPath("$.roleId").value("Role ID is required"));
    }

    @Test
    void shouldLoginUser() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("john");
        request.setPassword("secret123");

        authService.loginResponse = LoginResponse.builder()
                .access_token("jwt-token")
                .user(LoginResponse.UserInfo.builder()
                        .id(7L)
                        .username("john")
                        .email("john@example.com")
                        .fullName("John Doe")
                        .roleId(1L)
                        .role(LoginResponse.RoleInfo.builder()
                                .id(1L)
                                .slug("admin")
                                .name("Administrator")
                                .build())
                        .build())
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("jwt-token"))
                .andExpect(jsonPath("$.user.username").value("john"))
                .andExpect(jsonPath("$.user.role.slug").value("admin"));
    }

    @Test
    void shouldReturnCurrentUserProfile() throws Exception {
        authService.profileResponse = UserProfileResponse.builder()
                .id(5L)
                .username("alice")
                .email("alice@example.com")
                .fullName("Alice Smith")
                .role("manager")
                .roleName("Manager")
                .roleId(2L)
                .permissions(List.of("users.read", "users.write"))
                .isActive(true)
                .createdAt(Instant.parse("2026-03-23T12:00:00Z"))
                .updatedAt(Instant.parse("2026-03-23T15:00:00Z"))
                .build();

        mockMvc.perform(get("/api/auth/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.role").value("manager"))
                .andExpect(jsonPath("$.permissions[0]").value("users.read"))
                .andExpect(jsonPath("$.permissions[1]").value("users.write"));
    }

    private static final class FakeAuthService extends AuthService {

        private UserProfileResponse registerResponse;
        private LoginResponse loginResponse;
        private UserProfileResponse profileResponse;

        private FakeAuthService() {
            super(null, null, null, null, null, null);
        }

        @Override
        public UserProfileResponse register(RegisterRequest request) {
            return registerResponse;
        }

        @Override
        public LoginResponse login(LoginRequest request) {
            return loginResponse;
        }

        @Override
        public UserProfileResponse getProfile() {
            return profileResponse;
        }
    }
}
