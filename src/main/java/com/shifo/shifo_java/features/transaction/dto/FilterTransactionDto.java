package com.shifo.shifo_java.features.transaction.dto;

import com.shifo.shifo_java.features.transaction.TransactionType;
import com.shifo.shifo_java.common.enums.PaymentMethod;
import com.shifo.shifo_java.common.enums.RelatedEntityType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class FilterTransactionDto {

    // Фильтр по типу операции
    private TransactionType type;

    // Фильтр по типу оплаты
    private PaymentMethod paymentMethod;

    // Дата начала диапазона
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String dateFrom;

    // Дата конца диапазона
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String dateTo;

    // ID пациента или врача
    @Positive(message = "relatedEntityId должен быть положительным числом")
    private Long relatedEntityId;

    // Тип связанной сущности
    private RelatedEntityType relatedEntityType;

    // Пагинация
    @Min(value = 1, message = "page должно быть минимум 1")
    private Integer page = 1;

    @Min(value = 1, message = "limit должно быть минимум 1")
    private Integer limit = 10;
}

