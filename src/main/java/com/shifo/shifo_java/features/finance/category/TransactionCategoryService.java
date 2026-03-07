package com.shifo.shifo_java.features.finance.category;

import com.shifo.shifo_java.common.dto.PagedResponseDto;
import com.shifo.shifo_java.features.finance.category.dto.CreateTransactionCategoryDto;
import com.shifo.shifo_java.features.finance.category.dto.FilterTransactionCategoryDto;
import com.shifo.shifo_java.features.finance.category.dto.TransactionCategoryDto;
import com.shifo.shifo_java.features.finance.category.repository.TransactionCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
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

    public TransactionCategory create(CreateTransactionCategoryDto dto) {

        if (repository.existsByName(dto.getName())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Категория с названием \"" + dto.getName() + "\" уже существует"
            );
        }

        TransactionCategory category = new TransactionCategory();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        category.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);

        return repository.save(category);
    }

    public void remove(Long id) {

        TransactionCategory category = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Категория не найдена"));

        long transactionCount = repository.countTransactionsByCategoryId(id);

        if (transactionCount > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Нельзя удалить категорию, которая используется в "
                            + transactionCount +
                            " транзакциях. Сначала деактивируйте категорию."
            );
        }

        repository.delete(category);
    }
}
