package com.shifo.shifo_java.features.finance.transaction;

import com.shifo.shifo_java.features.finance.category.TransactionCategory;
import com.shifo.shifo_java.features.finance.category.TransactionCategoryMapper;
import com.shifo.shifo_java.features.finance.transaction.dto.CreateTransactionDto;
import com.shifo.shifo_java.features.finance.transaction.dto.TransactionDto;
import com.shifo.shifo_java.features.user.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TransactionMapper {

    private final TransactionCategoryMapper transactionCategoryMapper;

    public TransactionDto toDto(Transaction entity) {
        return TransactionDto.builder()
                .id(entity.getId())
                .type(entity.getType().name().toLowerCase())
                .paymentMethod(entity.getPaymentMethod())
                .amount(entity.getAmount())
                .categoryEntity(transactionCategoryMapper.toDto(entity.getCategory()))
                .date(entity.getDate())
                .comment(entity.getComment())
                .description(entity.getDescription())
                .recipient(entity.getRecipient())
                .notes(entity.getNotes())
                .user(entity.getUser())
                .userFullName(entity.getUser().getFullName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public Transaction toEntity(
            CreateTransactionDto dto,
            TransactionCategory category,
            User user
    ) {
        return Transaction.builder()
                .type(dto.getType())
                .paymentMethod(dto.getPaymentMethod())
                .amount(dto.getAmount())
                .category(category)
                .date(dto.getDate())
                .comment(dto.getComment())
                .description(dto.getDescription())
                .recipient(dto.getRecipient())
                .notes(dto.getNotes())
                .user(user)
                .build();
    }
}
