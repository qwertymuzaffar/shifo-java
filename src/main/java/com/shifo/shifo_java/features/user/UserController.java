package com.shifo.shifo_java.features.user;

import com.shifo.shifo_java.common.dto.PagedResponseDto;
import com.shifo.shifo_java.common.dto.PaginationDto;
import com.shifo.shifo_java.features.user.dto.FilterUserDto;
import com.shifo.shifo_java.features.user.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Users API")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService usersService;

    @GetMapping
    @Operation(summary = "Получить всех пользователей (только для админа)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список всех пользователей"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<PagedResponseDto<UserDto>> findAll(FilterUserDto filterDto) {
        return ResponseEntity.ok(usersService.findAll(filterDto));
    }
}
