package com.shifo.shifo_java.features.finance.category;

import com.shifo.shifo_java.common.dto.PagedResponseDto;
import com.shifo.shifo_java.features.finance.category.dto.CreateTransactionCategoryDto;
import com.shifo.shifo_java.features.finance.category.dto.FilterTransactionCategoryDto;
import com.shifo.shifo_java.features.finance.category.dto.TransactionCategoryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transaction-categories")
@RequiredArgsConstructor
@Tag(name = "Transaction Categories")
public class TransactionCategoryController {

    private final TransactionCategoryService categoryService;

    @GetMapping
    @Operation(summary = "Получить список категорий транзакций")
    @ApiResponse(
            responseCode = "200",
            description = "Список категорий успешно получен"
    )
    public PagedResponseDto<TransactionCategoryDto> findAll(
            @Valid FilterTransactionCategoryDto filterDto
    ) {
        return categoryService.findAll(filterDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать новую категорию транзакций")
    @ApiResponse(responseCode = "201", description = "Категория успешно создана")
    @ApiResponse(responseCode = "400", description = "Неверные данные")
    public TransactionCategory create(
            @Valid @RequestBody CreateTransactionCategoryDto dto
    ) {
        return categoryService.create(dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить категорию транзакций (мягкое удаление)")
    @ApiResponse(responseCode = "200", description = "Категория удалена")
    @ApiResponse(responseCode = "404", description = "Категория не найдена")
    @ApiResponse(responseCode = "400", description = "Категория используется в транзакциях")
    public void remove(@PathVariable Long id) {
        categoryService.remove(id);
    }
}
