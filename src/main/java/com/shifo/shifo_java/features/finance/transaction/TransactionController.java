package com.shifo.shifo_java.features.finance.transaction;

import com.shifo.shifo_java.common.dto.PagedResponseDto;
import com.shifo.shifo_java.features.finance.transaction.dto.FilterTransactionDto;
import com.shifo.shifo_java.features.finance.transaction.dto.TransactionDto;
import lombok.RequiredArgsConstructor;
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
}
