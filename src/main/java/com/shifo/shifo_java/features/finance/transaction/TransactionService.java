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
    public Transaction create(CreateTransactionDto dto) {

        // Validate category exists
        TransactionCategory category = categoryRepository
                .findById(dto.getCategoryId())
                .orElseThrow(() ->
                        new BadRequestException(
                                "Category with id " + dto.getCategoryId() + " not found"
                        )
                );

        User user = securityUtils.getCurrentUser();

        // Create entity
        Transaction transaction = Transaction.builder()
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

        Transaction savedTransaction = repository.save(transaction);

        // Automatically create balance record
        balanceService.handleTransactionStatusChange(savedTransaction);

        return savedTransaction;
    }
}
