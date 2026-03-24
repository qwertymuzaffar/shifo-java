package com.shifo.shifo_java.features.auth;

import com.shifo.shifo_java.common.exceptions.ConflictException;
import com.shifo.shifo_java.features.auth.dto.RegisterRequest;
import com.shifo.shifo_java.features.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthValidatorTest {

    private final Map<String, String> messages = new HashMap<>();

    private boolean usernameExists;
    private boolean emailExists;

    private AuthValidator authValidator;
    private RegisterRequest request;

    @BeforeEach
    void setUp() {
        messages.put("auth.errors.userAlreadyExists", "User already exists");
        messages.put("auth.errors.emailAlreadyInUse", "Email already in use");

        authValidator = new AuthValidator(userRepository(), messageSource());

        request = new RegisterRequest();
        request.setUsername("john");
        request.setEmail("john@example.com");
    }

    @Test
    void shouldPassWhenUsernameAndEmailAreAvailable() {
        usernameExists = false;
        emailExists = false;

        assertThatCode(() -> authValidator.validateRegister(request))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowWhenUsernameAlreadyExists() {
        usernameExists = true;
        emailExists = false;

        assertThatThrownBy(() -> authValidator.validateRegister(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("User already exists");
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {
        usernameExists = false;
        emailExists = true;

        assertThatThrownBy(() -> authValidator.validateRegister(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Email already in use");
    }

    @Test
    void shouldThrowCombinedMessageWhenUsernameAndEmailAlreadyExist() {
        usernameExists = true;
        emailExists = true;

        assertThatThrownBy(() -> authValidator.validateRegister(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("User already exists, Email already in use");
    }

    private UserRepository userRepository() {
        return (UserRepository) Proxy.newProxyInstance(
                UserRepository.class.getClassLoader(),
                new Class[]{UserRepository.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "existsByUsername" -> usernameExists;
                    case "existsByEmail" -> emailExists;
                    case "toString" -> "UserRepositoryTestProxy";
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> proxy == args[0];
                    default -> throw new UnsupportedOperationException(method.getName());
                }
        );
    }

    private MessageSource messageSource() {
        return (MessageSource) Proxy.newProxyInstance(
                MessageSource.class.getClassLoader(),
                new Class[]{MessageSource.class},
                (proxy, method, args) -> {
                    if ("getMessage".equals(method.getName())) {
                        String code = (String) args[0];
                        String defaultMessage = (String) args[2];
                        Locale locale = (Locale) args[3];
                        return messages.getOrDefault(code + ":" + locale, messages.getOrDefault(code, defaultMessage));
                    }

                    if ("toString".equals(method.getName())) {
                        return "MessageSourceTestProxy";
                    }

                    if ("hashCode".equals(method.getName())) {
                        return System.identityHashCode(proxy);
                    }

                    if ("equals".equals(method.getName())) {
                        return proxy == args[0];
                    }

                    throw new UnsupportedOperationException(method.getName());
                }
        );
    }
}
