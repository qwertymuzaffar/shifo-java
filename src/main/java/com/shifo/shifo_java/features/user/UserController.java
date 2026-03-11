package com.shifo.shifo_java.features.user;

import com.shifo.shifo_java.common.dto.PagedResponseDto;
import com.shifo.shifo_java.common.dto.PaginationDto;
import com.shifo.shifo_java.features.user.dto.FilterUserDto;
import com.shifo.shifo_java.features.user.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


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
    @PreAuthorize("hasAuthority('user.view')")
    public ResponseEntity<PagedResponseDto<UserDto>> findAll(FilterUserDto filterDto) {
        return ResponseEntity.ok(usersService.findAll(filterDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по ID (только для админа)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о пользователе"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PreAuthorize("hasAuthority('user.view')")
    public ResponseEntity<UserDto> findOne(
            @Parameter(description = "ID пользователя")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(usersService.findOne(id));
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить пользователя (только для админа)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно удален"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PreAuthorize("hasAuthority('user.delete')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID пользователя")
            @PathVariable Long id
    ) {
        usersService.remove(id);
        return ResponseEntity.ok().build();
    }

}
