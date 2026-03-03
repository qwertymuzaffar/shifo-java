package com.shifo.shifo_java.features.finance.transaction.dto;

import com.shifo.shifo_java.features.finance.transaction.model.TransactionType;
import com.shifo.shifo_java.features.payment.model.PaymentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateTransactionDto {

    @Schema(
            description = "Тип операции: приход или расход",
            example = "INCOME"
    )
    @NotNull(message = "Тип операции обязателен")
    private TransactionType type;

    @Schema(
            description = "Тип оплаты: наличные, карта, ДС, Алиф, ЭО",
            example = "CASH"
    )
    @NotNull(message = "Тип оплаты обязателен")
    private PaymentType paymentMethod;

    @Schema(
            description = "Сумма транзакции",
            example = "100.50",
            minimum = "0.01"
    )
    @NotNull(message = "Сумма обязательна")
    @DecimalMin(value = "0.01", message = "Сумма должна быть больше 0")
    private BigDecimal amount;

    @Schema(
            description = "ID категории транзакции",
            example = "1",
            minimum = "1"
    )
    @NotNull(message = "Категория обязательна")
    @Positive(message = "ID категории должен быть положительным числом")
    private Long categoryId;

    @Schema(
            description = "Дата транзакции",
            example = "2024-01-01"
    )
    @NotNull(message = "Дата обязательна")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    @Schema(
            description = "Комментарий к транзакции",
            example = "Оплата за консультацию"
    )
    private String comment;

    @Schema(
            description = "Краткое описание траты",
            example = "Покупка медикаментов"
    )
    private String description;

    @Schema(
            description = "Получатель (кому произведена оплата)",
            example = "Поставщик медикаментов"
    )
    private String recipient;

    @Schema(
            description = "Дополнительная информация (примечания)",
            example = "Срочная поставка"
    )
    private String notes;
}
