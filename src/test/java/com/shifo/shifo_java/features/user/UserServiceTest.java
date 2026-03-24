package com.shifo.shifo_java.features.user;

import com.shifo.shifo_java.common.dto.PagedResponseDto;
import com.shifo.shifo_java.features.role.Role;
import com.shifo.shifo_java.features.user.dto.FilterUserDto;
import com.shifo.shifo_java.features.user.dto.UpdateUserDto;
import com.shifo.shifo_java.features.user.dto.UserDto;
import com.shifo.shifo_java.features.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Proxy;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserServiceTest {

    private final RepositoryState repositoryState = new RepositoryState();

    private RecordingPasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new RecordingPasswordEncoder();
        userService = new UserService(userRepository(), new UserMapper(), passwordEncoder);
    }

    @Test
    void shouldUpdateUserWithEncodedPasswordAndMappedFields() {
        User user = new User();
        user.setId(1L);
        user.setEmail("old@example.com");
        user.setFirstName("Old");
        user.setLastName("Name");
        user.setPhone("+12345678901");
        user.setIsActive(true);
        repositoryState.userById = user;

        UpdateUserDto dto = new UpdateUserDto();
        dto.setEmail("new@example.com");
        dto.setFirstName("New");
        dto.setLastName("Surname");
        dto.setPhone("+998901234567");
        dto.setIsActive(false);
        dto.setPassword("NewPassword1");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("john");
        savedUser.setEmail("new@example.com");
        savedUser.setFirstName("New");
        savedUser.setLastName("Surname");
        savedUser.setPhone("+998901234567");
        savedUser.setIsActive(false);
        savedUser.setCreatedAt(Instant.parse("2026-03-24T10:00:00Z"));
        savedUser.setUpdatedAt(Instant.parse("2026-03-24T12:00:00Z"));
        repositoryState.savedUserToReturn = savedUser;

        UserDto response = userService.update(1L, dto);

        assertThat(passwordEncoder.rawPassword).isEqualTo("NewPassword1");
        assertThat(repositoryState.savedUser.getEmail()).isEqualTo("new@example.com");
        assertThat(repositoryState.savedUser.getPassword()).isEqualTo("encoded-NewPassword1");
        assertThat(repositoryState.savedUser.getFirstName()).isEqualTo("New");
        assertThat(repositoryState.savedUser.getLastName()).isEqualTo("Surname");
        assertThat(repositoryState.savedUser.getPhone()).isEqualTo("+998901234567");
        assertThat(repositoryState.savedUser.getIsActive()).isFalse();
        assertThat(response.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void shouldThrowWhenUpdatingUserWithDuplicateEmail() {
        User user = new User();
        user.setId(1L);
        user.setEmail("old@example.com");
        repositoryState.userById = user;

        User duplicateEmailUser = new User();
        duplicateEmailUser.setId(2L);
        duplicateEmailUser.setEmail("taken@example.com");
        repositoryState.userByEmail = duplicateEmailUser;

        UpdateUserDto dto = new UpdateUserDto();
        dto.setEmail("taken@example.com");

        assertThatThrownBy(() -> userService.update(1L, dto))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseException = (ResponseStatusException) exception;
                    assertThat(responseException.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(responseException.getReason()).isEqualTo("Email already in use");
                });
    }

    @Test
    void shouldFindUserByIdWithRole() {
        Role role = new Role();
        role.setId(3L);

        User user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setRole(role);
        user.setIsActive(true);
        repositoryState.userByIdWithRole = user;

        UserDto response = userService.findOne(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("john");
        assertThat(response.getRoleId()).isEqualTo(3L);
    }

    @Test
    void shouldSoftDeleteUser() {
        repositoryState.softDeleteResult = 1;

        userService.remove(5L);

        assertThat(repositoryState.softDeletedId).isEqualTo(5L);
    }

    @Test
    void shouldThrowWhenSoftDeleteDoesNotAffectRows() {
        repositoryState.softDeleteResult = 0;

        assertThatThrownBy(() -> userService.remove(7L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseException = (ResponseStatusException) exception;
                    assertThat(responseException.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(responseException.getReason()).isEqualTo("users.errors.notFound");
                });
    }

    @Test
    void shouldReturnMappedPagedUsers() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setIsActive(true);

        PagedResponseDto<User> page = new PagedResponseDto<>();
        page.setItems(List.of(user));
        page.setPage(2);
        page.setLimit(5);
        page.setTotal(11L);
        page.setTotalPages(3);
        repositoryState.pagedUsers = page;

        PagedResponseDto<UserDto> response = userService.findAll(new FilterUserDto());

        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getUsername()).isEqualTo("john");
        assertThat(response.getPage()).isEqualTo(2);
        assertThat(response.getLimit()).isEqualTo(5);
        assertThat(response.getTotal()).isEqualTo(11L);
        assertThat(response.getTotalPages()).isEqualTo(3);
    }

    private UserRepository userRepository() {
        return (UserRepository) Proxy.newProxyInstance(
                UserRepository.class.getClassLoader(),
                new Class[]{UserRepository.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "findById" -> Optional.ofNullable(repositoryState.userById);
                    case "findByEmail" -> Optional.ofNullable(repositoryState.userByEmail);
                    case "save" -> {
                        repositoryState.savedUser = (User) args[0];
                        yield repositoryState.savedUserToReturn != null ? repositoryState.savedUserToReturn : repositoryState.savedUser;
                    }
                    case "findByIdWithRole" -> Optional.ofNullable(repositoryState.userByIdWithRole);
                    case "softDelete" -> {
                        repositoryState.softDeletedId = (Long) args[0];
                        yield repositoryState.softDeleteResult;
                    }
                    case "findAllWithFilter" -> repositoryState.pagedUsers;
                    case "toString" -> "UserRepositoryTestProxy";
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> proxy == args[0];
                    default -> throw new UnsupportedOperationException(method.getName());
                }
        );
    }

    private static final class RepositoryState {
        private User userById;
        private User userByEmail;
        private User savedUser;
        private User savedUserToReturn;
        private User userByIdWithRole;
        private Long softDeletedId;
        private int softDeleteResult;
        private PagedResponseDto<User> pagedUsers;
    }

    private static final class RecordingPasswordEncoder implements PasswordEncoder {
        private String rawPassword;

        @Override
        public String encode(CharSequence rawPassword) {
            this.rawPassword = rawPassword.toString();
            return "encoded-" + rawPassword;
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return encodedPassword.equals("encoded-" + rawPassword);
        }
    }
}
