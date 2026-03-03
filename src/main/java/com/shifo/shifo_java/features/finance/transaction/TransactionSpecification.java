package com.shifo.shifo_java.features.finance.transaction;

import com.shifo.shifo_java.features.finance.transaction.dto.FilterTransactionDto;
import com.shifo.shifo_java.features.finance.transaction.model.TransactionType;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class TransactionSpecification {

    public static Specification<Transaction> build(
            FilterTransactionDto filter,
            Long userId,
            boolean isAdmin
    ) {

        return (root, query, cb) -> {

            root.fetch("user", JoinType.LEFT);
            root.fetch("category", JoinType.LEFT);

            Predicate predicate = cb.conjunction();

            // Role filtering
            if (!isAdmin) {
                LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
                LocalDateTime endOfDay = startOfDay.plusDays(1);

                predicate = cb.and(predicate,
                        cb.equal(root.get("user").get("id"), userId),
                        cb.greaterThanOrEqualTo(root.get("createdAt"), startOfDay),
                        cb.lessThan(root.get("createdAt"), endOfDay)
                );
            }

            // Type
            if (filter.getType() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("type"), filter.getType())
                );
            }

            // Payment Method
            if (filter.getPaymentMethod() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("paymentMethod"), filter.getPaymentMethod())
                );
            }

            // Category
            if (filter.getCategoryId() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("category").get("id"), filter.getCategoryId())
                );
            }

            // Search
            if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
                String pattern = "%" + filter.getSearch().toLowerCase() + "%";

                predicate = cb.and(predicate,
                        cb.or(
                                cb.like(cb.lower(root.get("description")), pattern),
                                cb.like(cb.lower(root.get("recipient")), pattern),
                                cb.like(cb.lower(root.get("comment")), pattern)
                        )
                );
            }

            // Date range (admin only)
            if (isAdmin && filter.getDateFrom() != null && filter.getDateTo() != null) {
                predicate = cb.and(predicate,
                        cb.between(root.get("createdAt"),
                                filter.getDateFrom(),
                                filter.getDateTo())
                );
            }

            return predicate;
        };
    }
}
