package com.shifo.shifo_java.features.finance.transaction.dto;

import com.shifo.shifo_java.features.payment.model.PaymentType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionDto {

    private Long id;
    private String type;
    private PaymentType paymentMethod;
    private BigDecimal amount;

    private String category;
    private LocalDate date;

    private String comment;
    private String description;
    private String recipient;
    private String notes;

    private Long userId;
    private String userFullName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
