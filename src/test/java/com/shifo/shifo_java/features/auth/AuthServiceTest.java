package com.shifo.shifo_java.features.auth;

import com.shifo.shifo_java.common.exceptions.UnauthorizedException;
import com.shifo.shifo_java.features.auth.dto.LoginRequest;
import com.shifo.shifo_java.features.auth.dto.LoginResponse;
import com.shifo.shifo_java.features.auth.dto.RegisterRequest;
import com.shifo.shifo_java.features.auth.dto.UserProfileResponse;
import com.shifo.shifo_java.features.user.User;
import com.shifo.shifo_java.features.user.repository.UserRepository;
import com.shifo.shifo_java.security.SecurityUtils;
import com.shifo.shifo_java.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.lang.reflect.Proxy;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthServiceTest {

    private final RepositoryState repositoryState = new RepositoryState();

    private RecordingAuthValidator authValidator;
    private RecordingAuthMapper authMapper;
    private RecordingTokenService tokenService;
    private RecordingAuthenticationManager authenticationManager;
    private RecordingSecurityUtils securityUtils;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authValidator = new RecordingAuthValidator();
        authMapper = new RecordingAuthMapper();
        tokenService = new RecordingTokenService();
        authenticationManager = new RecordingAuthenticationManager();
        securityUtils = new RecordingSecurityUtils();

        authService = new AuthService(
                authValidator,
                userRepository(),
                authMapper,
                tokenService,
                authenticationManager,
                securityUtils
        );
    }

    @Test
    void shouldValidateMapAndSaveUserOnRegister() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");
        request.setEmail("john@example.com");

        User mappedUser = new User();
        mappedUser.setUsername("john");
        mappedUser.setEmail("john@example.com");
        authMapper.userFromRequest = mappedUser;

        UserProfileResponse expectedProfile = UserProfileResponse.builder()
                .username("john")
                .email("john@example.com")
                .build();
        authMapper.profileResponse = expectedProfile;

        UserProfileResponse response = authService.register(request);

        assertThat(authValidator.validatedRequest).isSameAs(request);
        assertThat(authMapper.toEntityRequest).isSameAs(request);
        assertThat(repositoryState.savedUser).isSameAs(mappedUser);
        assertThat(authMapper.profileUser).isSameAs(mappedUser);
        assertThat(response).isSameAs(expectedProfile);
    }

    @Test
    void shouldReturnLoginResponseForActiveUser() {
        LoginRequest request = new LoginRequest();
        request.setUsername("john");
        request.setPassword("secret123");

        User user = new User();
        user.setId(7L);
        user.setUsername("john");
        user.setPassword("encoded");
        user.setIsActive(true);
        repositoryState.userByUsername = user;

        tokenService.generatedToken = "jwt-token";
        authMapper.loginResponse = LoginResponse.builder()
                .access_token("jwt-token")
                .build();

        LoginResponse response = authService.login(request);

        assertThat(authenticationManager.lastAuthentication).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        assertThat(authenticationManager.lastAuthentication.getPrincipal()).isEqualTo("john");
        assertThat(authenticationManager.lastAuthentication.getCredentials()).isEqualTo("secret123");
        assertThat(tokenService.userForToken).isSameAs(user);
        assertThat(authMapper.loginUser).isSameAs(user);
        assertThat(authMapper.loginToken).isEqualTo("jwt-token");
        assertThat(response).isSameAs(authMapper.loginResponse);
    }

    @Test
    void shouldThrowWhenAuthenticatedUserIsNotFoundInRepository() {
        LoginRequest request = new LoginRequest();
        request.setUsername("ghost");
        request.setPassword("secret123");

        repositoryState.userByUsername = null;

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    void shouldThrowWhenUserAccountIsDisabled() {
        LoginRequest request = new LoginRequest();
        request.setUsername("blocked");
        request.setPassword("secret123");

        User user = new User();
        user.setUsername("blocked");
        user.setIsActive(false);
        repositoryState.userByUsername = user;

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Account is disabled");
    }

    @Test
    void shouldReturnMappedProfileForCurrentUser() {
        User currentUser = new User();
        currentUser.setUsername("alice");
        securityUtils.currentUser = currentUser;

        UserProfileResponse expectedProfile = UserProfileResponse.builder()
                .username("alice")
                .build();
        authMapper.profileResponse = expectedProfile;

        UserProfileResponse response = authService.getProfile();

        assertThat(authMapper.profileUser).isSameAs(currentUser);
        assertThat(response).isSameAs(expectedProfile);
    }

    private UserRepository userRepository() {
        return (UserRepository) Proxy.newProxyInstance(
                UserRepository.class.getClassLoader(),
                new Class[]{UserRepository.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "save" -> {
                        repositoryState.savedUser = (User) args[0];
                        yield repositoryState.savedUser;
                    }
                    case "findByUsername" -> Optional.ofNullable(repositoryState.userByUsername);
                    case "toString" -> "UserRepositoryTestProxy";
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> proxy == args[0];
                    default -> throw new UnsupportedOperationException(method.getName());
                }
        );
    }

    private static final class RepositoryState {
        private User savedUser;
        private User userByUsername;
    }

    private static final class RecordingAuthValidator extends AuthValidator {
        private RegisterRequest validatedRequest;

        private RecordingAuthValidator() {
            super(null, null);
        }

        @Override
        public void validateRegister(RegisterRequest request) {
            validatedRequest = request;
        }
    }

    private static final class RecordingAuthMapper extends AuthMapper {
        private RecordingAuthMapper() {
            super(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder());
        }

        private RegisterRequest toEntityRequest;
        private User userFromRequest;
        private User loginUser;
        private String loginToken;
        private LoginResponse loginResponse;
        private User profileUser;
        private UserProfileResponse profileResponse;

        @Override
        public User toEntity(RegisterRequest request) {
            toEntityRequest = request;
            return userFromRequest;
        }

        @Override
        public LoginResponse toLoginResponse(User user, String token) {
            loginUser = user;
            loginToken = token;
            return loginResponse;
        }

        @Override
        public UserProfileResponse toProfile(User user) {
            profileUser = user;
            return profileResponse;
        }
    }

    private static final class RecordingTokenService extends TokenService {
        private User userForToken;
        private String generatedToken;

        private RecordingTokenService() {
            super(null);
        }

        @Override
        public String generateToken(User user) {
            userForToken = user;
            return generatedToken;
        }
    }

    private static final class RecordingSecurityUtils extends SecurityUtils {
        private User currentUser;

        private RecordingSecurityUtils() {
            super(null);
        }

        @Override
        public User getCurrentUser() {
            return currentUser;
        }
    }

    private static final class RecordingAuthenticationManager implements AuthenticationManager {
        private Authentication lastAuthentication;

        @Override
        public Authentication authenticate(Authentication authentication) {
            lastAuthentication = authentication;
            return authentication;
        }
    }
}
