package com.shifo.shifo_java.features.finance.category;

import com.shifo.shifo_java.common.dto.PagedResponseDto;
import com.shifo.shifo_java.features.finance.category.dto.FilterTransactionCategoryDto;
import com.shifo.shifo_java.features.finance.category.dto.TransactionCategoryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transaction-categories")
@RequiredArgsConstructor
@Tag(name = "Transaction Categories")
public class TransactionCategoryController {

    private final TransactionCategoryService service;

    @GetMapping
    @Operation(summary = "Получить список категорий транзакций")
    @ApiResponse(
            responseCode = "200",
            description = "Список категорий успешно получен"
    )
    public PagedResponseDto<TransactionCategoryDto> findAll(
            @Valid FilterTransactionCategoryDto filterDto
    ) {
        return service.findAll(filterDto);
    }
}
