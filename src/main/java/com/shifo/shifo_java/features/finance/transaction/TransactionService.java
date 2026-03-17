package com.shifo.shifo_java.features.finance.transaction;

import com.shifo.shifo_java.common.dto.PagedResponseDto;
import com.shifo.shifo_java.common.exceptions.BadRequestException;
import com.shifo.shifo_java.features.balance.BalanceService;
import com.shifo.shifo_java.features.finance.category.TransactionCategory;
import com.shifo.shifo_java.features.finance.category.repository.TransactionCategoryRepository;
import com.shifo.shifo_java.features.finance.transaction.dto.*;
import com.shifo.shifo_java.features.role.Role;
import com.shifo.shifo_java.features.role.RoleService;
import com.shifo.shifo_java.features.user.User;
import com.shifo.shifo_java.features.user.UserService;
import com.shifo.shifo_java.features.user.dto.UserDto;
import com.shifo.shifo_java.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;
    private final TransactionCategoryRepository categoryRepository;
    private final TransactionMapper mapper;
    private final SecurityUtils securityUtils;
    private final BalanceService balanceService;

    private final TransactionMapper transactionMapper;

    public PagedResponseDto<TransactionDto> findAll(
            FilterTransactionDto filter
    ) {

        User user = securityUtils.getCurrentUser();
        boolean isAdmin = user.getRole().getSlug().equals("ADMIN");

        Pageable pageable = PageRequest.of(
                filter.getPage() - 1,
                filter.getLimit(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Long userId = user.getId();

        Page<Transaction> page = repository.findAll(
                TransactionSpecification.build(filter, userId, isAdmin),
                pageable
        );

        return PagedResponseDto.<TransactionDto>builder()
                .items(page.getContent().stream().map(mapper::toDto).toList())
                .total(page.getTotalElements())
                .page(filter.getPage())
                .limit(filter.getLimit())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Transactional
    public TransactionDto create(CreateTransactionDto dto) {

        TransactionCategory category = getCategoryOrThrow(dto.getCategoryId());

        User user = securityUtils.getCurrentUser();

        Transaction transaction = transactionMapper.toEntity(dto, category, user);

        Transaction savedTransaction = repository.save(transaction);

        recordBalance(savedTransaction);

        return transactionMapper.toDto(savedTransaction);
    }

    private TransactionCategory getCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new BadRequestException(
                                "Category with id " + categoryId + " not found"
                        )
                );
    }

    private void recordBalance(Transaction transaction) {
        balanceService.recordTransaction(transaction);
    }
}
