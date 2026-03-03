package com.shifo.shifo_java.features.finance.transaction.dto;

import com.shifo.shifo_java.features.finance.transaction.model.TransactionType;
import com.shifo.shifo_java.features.payment.model.PaymentType;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Data
public class FilterTransactionDto {

    @Schema(
            description = "Фильтр по типу операции",
            example = "INCOME"
    )
    private TransactionType type;

    @Schema(
            description = "Фильтр по типу оплаты",
            example = "CASH"
    )
    private PaymentType paymentMethod;

    @Schema(
            description = "Фильтр по ID категории транзакции",
            example = "1"
    )
    private Long categoryId;

    @Schema(
            description = "Поиск по описанию или получателю",
            example = "медикаменты"
    )
    private String search;

    @Schema(
            description = "Дата начала периода (YYYY-MM-DD)",
            example = "2024-01-01"
    )
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFrom;

    @Schema(
            description = "Дата окончания периода (YYYY-MM-DD)",
            example = "2024-12-31"
    )
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateTo;

    @Schema(
            description = "Номер страницы",
            example = "1",
            defaultValue = "1"
    )
    @Min(1)
    private Integer page = 1;

    @Schema(
            description = "Количество записей на странице",
            example = "10",
            defaultValue = "10"
    )
    @Min(1)
    private Integer limit = 10;
}

