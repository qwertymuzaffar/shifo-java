package com.shifo.shifo_java.features.finance.transaction;

import com.shifo.shifo_java.common.dto.PagedResponseDto;
import com.shifo.shifo_java.features.finance.transaction.dto.CreateTransactionDto;
import com.shifo.shifo_java.features.finance.transaction.dto.FilterTransactionDto;
import com.shifo.shifo_java.features.finance.transaction.dto.TransactionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public PagedResponseDto<TransactionDto> findAll(
            @ModelAttribute FilterTransactionDto filterDto
    ) {
        return transactionService.findAll(filterDto);
    }

    @PostMapping
    @Operation(
            summary = "Создать новую транзакцию",
            description = "Создает новую транзакцию прихода или расхода средств"
    )
    @ApiResponse(responseCode = "201", description = "Транзакция успешно создана")
    @ApiResponse(responseCode = "400", description = "Неверные данные")
    public ResponseEntity<Transaction> create(
            @Valid @RequestBody CreateTransactionDto dto
    ) {

        Transaction transaction = transactionService.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }
}
