package com.shifo.shifo_java.features.finance.category;

import com.shifo.shifo_java.features.finance.category.dto.TransactionCategoryDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionCategoryMapper {

    public TransactionCategoryDto toDto(TransactionCategory entity) {
        if (entity == null) {
            return null;
        }

        return TransactionCategoryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .isActive(entity.getIsActive())
                .sortOrder(entity.getSortOrder())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public List<TransactionCategoryDto> toDtoList(List<TransactionCategory> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }
}
