package com.shifo.shifo_java.features.finance.transaction;

import com.shifo.shifo_java.features.finance.transaction.dto.TransactionDto;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionDto toDto(Transaction entity) {
        return TransactionDto.builder()
                .id(entity.getId())
                .type(entity.getType().name().toLowerCase())
                .paymentMethod(entity.getPaymentMethod().name().toLowerCase())
                .amount(entity.getAmount())
                .category(entity.getCategory().getName())
                .date(entity.getDate())
                .comment(entity.getComment())
                .description(entity.getDescription())
                .recipient(entity.getRecipient())
                .notes(entity.getNotes())
                .userId(entity.getUser().getId())
                .userFullName(entity.getUser().getFullName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
