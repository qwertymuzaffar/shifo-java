package com.shifo.shifo_java.features.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shifo.shifo_java.common.dto.PagedResponseDto;
import com.shifo.shifo_java.features.user.dto.FilterUserDto;
import com.shifo.shifo_java.features.user.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private FakeUserService userService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        userService = new FakeUserService();
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService))
                .build();
    }

    @Test
    void shouldReturnPagedUsers() throws Exception {
        PagedResponseDto<UserDto> response = PagedResponseDto.<UserDto>builder()
                .items(List.of(userDto(1L, "john"), userDto(2L, "alice")))
                .page(1)
                .limit(10)
                .total(2)
                .totalPages(1)
                .build();
        userService.findAllResponse = response;

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[0].username").value("john"))
                .andExpect(jsonPath("$.items[1].username").value("alice"))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.total").value(2));
    }

    @Test
    void shouldReturnUserById() throws Exception {
        userService.findOneResponse = userDto(1L, "john");

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/5"))
                .andExpect(status().isOk());
    }

    private UserDto userDto(Long id, String username) {
        UserDto dto = new UserDto();
        dto.setId(id);
        dto.setUsername(username);
        dto.setEmail(username + "@example.com");
        dto.setFirstName(username);
        dto.setLastName("Doe");
        dto.setFullName(username + " Doe");
        dto.setIsActive(true);
        dto.setCreatedAt(Instant.parse("2026-03-24T10:00:00Z"));
        dto.setUpdatedAt(Instant.parse("2026-03-24T12:00:00Z"));
        return dto;
    }

    private static final class FakeUserService extends UserService {

        private PagedResponseDto<UserDto> findAllResponse;
        private UserDto findOneResponse;

        private FakeUserService() {
            super(null, null, null);
        }

        @Override
        public PagedResponseDto<UserDto> findAll(FilterUserDto filterDto) {
            return findAllResponse;
        }

        @Override
        public UserDto findOne(Long id) {
            return findOneResponse;
        }

        @Override
        public void remove(Long id) {
        }
    }
}
