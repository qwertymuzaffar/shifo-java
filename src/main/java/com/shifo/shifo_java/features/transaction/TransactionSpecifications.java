package com.shifo.shifo_java.features.transaction;

import com.shifo.shifo_java.common.enums.PaymentMethod;
import com.shifo.shifo_java.common.enums.RelatedEntityType;
import com.shifo.shifo_java.features.transaction.dto.FilterTransactionDto;
import org.springframework.data.jpa.domain.Specification;

public class TransactionSpecifications {

    public static Specification<Transaction> buildFilters(FilterTransactionDto filter) {
        return Specification.where(type(filter.getType()))
                .and(paymentMethod(filter.getPaymentMethod()))
                .and(dateFrom(filter.getDateFrom()))
                .and(dateTo(filter.getDateTo()))
                .and(relatedEntity(filter.getRelatedEntityId(), filter.getRelatedEntityType()));
    }

    private static Specification<Transaction> type(TransactionType type) {
        return (root, query, cb) ->
                type == null ? null : cb.equal(root.get("type"), type);
    }

    private static Specification<Transaction> paymentMethod(PaymentMethod pm) {
        return (root, query, cb) ->
                pm == null ? null : cb.equal(root.get("paymentMethod"), pm);
    }

    private static Specification<Transaction> dateFrom(String date) {
        return (root, query, cb) ->
                date == null ? null : cb.greaterThanOrEqualTo(root.get("createdAt"), date + "T00:00:00");
    }

    private static Specification<Transaction> dateTo(String date) {
        return (root, query, cb) ->
                date == null ? null : cb.lessThanOrEqualTo(root.get("createdAt"), date + "T23:59:59");
    }

    private static Specification<Transaction> relatedEntity(Long id, RelatedEntityType type) {
        return (root, query, cb) -> {
            if (id == null || type == null) return null;
            return cb.and(
                    cb.equal(root.get("relatedEntityId"), id),
                    cb.equal(root.get("relatedEntityType"), type)
            );
        };
    }
}

