package com.shifo.shifo_java.features.finance.category;

import com.shifo.shifo_java.common.dto.PagedResponseDto;
import com.shifo.shifo_java.features.finance.category.dto.FilterTransactionCategoryDto;
import com.shifo.shifo_java.features.finance.category.dto.TransactionCategoryDto;
import com.shifo.shifo_java.features.finance.category.repository.TransactionCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionCategoryService {

    private final TransactionCategoryRepository repository;
    private final TransactionCategoryMapper mapper;

    public PagedResponseDto<TransactionCategoryDto> findAll(
            FilterTransactionCategoryDto filterDto
    ) {

        int page = filterDto.getPage();
        int limit = filterDto.getLimit();
        int skip = (page - 1) * limit;

        // Fetch filtered list
        List<TransactionCategory> categories =
                repository.findAllWithFilters(
                        filterDto.getSearch(),
                        filterDto.getIsActive(),
                        skip,
                        limit
                );

        long total =
                repository.countWithFilters(
                        filterDto.getSearch(),
                        filterDto.getIsActive()
                );

        List<TransactionCategoryDto> items =
                categories.stream()
                        .map(mapper::toDto)
                        .toList();

        int totalPages = (int) Math.ceil((double) total / limit);

        return PagedResponseDto.<TransactionCategoryDto>builder()
                .items(items)
                .page(page)
                .limit(limit)
                .total(total)
                .totalPages(totalPages)
                .build();
    }
}
