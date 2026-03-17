package com.shifo.shifo_java.features.finance.transaction.dto;

import com.shifo.shifo_java.features.finance.category.TransactionCategory;
import com.shifo.shifo_java.features.finance.category.dto.TransactionCategoryDto;
import com.shifo.shifo_java.features.payment.model.PaymentType;
import com.shifo.shifo_java.features.user.User;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class TransactionDto {

    private Long id;
    private String type;
    private PaymentType paymentMethod;
    private BigDecimal amount;

    private TransactionCategoryDto categoryEntity;
    private LocalDate date;

    private String comment;
    private String description;
    private String recipient;
    private String notes;

    private User user;
    private String userFullName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
