package com.shifo.shifo_java.features.transaction.dto;

import com.shifo.shifo_java.features.transaction.TransactionType;
import com.shifo.shifo_java.common.enums.PaymentMethod;
import com.shifo.shifo_java.common.enums.RelatedEntityType;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionDto {

    // Тип операции
    @NotNull(message = "Тип операции обязателен")
    private TransactionType type;

    // Тип оплаты
    @NotNull(message = "Тип оплаты обязателен")
    private PaymentMethod paymentMethod;

    // Сумма транзакции
    @NotNull(message = "Сумма обязательна")
    @DecimalMin(value = "0.5", message = "Сумма должна быть не менее 0.5")
    private BigDecimal amount;

    // Комментарий (необязательно)
    private String comment;

    // ID врача или пациента
    @Positive(message = "ID связанной сущности должен быть положительным числом")
    private Long relatedEntityId;

    // Тип связанной сущности
    private RelatedEntityType relatedEntityType;
}

