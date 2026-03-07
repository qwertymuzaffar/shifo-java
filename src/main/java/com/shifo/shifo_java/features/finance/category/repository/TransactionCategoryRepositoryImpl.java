package com.shifo.shifo_java.features.finance.category.repository;

import com.shifo.shifo_java.features.finance.category.TransactionCategory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TransactionCategoryRepositoryImpl {

    private final EntityManager entityManager;

    public List<TransactionCategory> findAllWithFilters(
            String search,
            Boolean isActive,
            int skip,
            int limit
    ) {

        StringBuilder jpql = new StringBuilder(
                "SELECT c FROM TransactionCategory c WHERE 1=1 "
        );

        if (search != null && !search.isBlank()) {
            jpql.append("AND LOWER(c.name) LIKE LOWER(:search) ");
        }

        if (isActive != null) {
            jpql.append("AND c.isActive = :isActive ");
        }

        TypedQuery<TransactionCategory> query =
                entityManager.createQuery(jpql.toString(), TransactionCategory.class);

        if (search != null && !search.isBlank()) {
            query.setParameter("search", "%" + search + "%");
        }

        if (isActive != null) {
            query.setParameter("isActive", isActive);
        }

        query.setFirstResult(skip);
        query.setMaxResults(limit);

        return query.getResultList();
    }

    public long countWithFilters(String search, Boolean isActive) {

        StringBuilder jpql = new StringBuilder(
                "SELECT COUNT(c) FROM TransactionCategory c WHERE 1=1 "
        );

        if (search != null && !search.isBlank()) {
            jpql.append("AND LOWER(c.name) LIKE LOWER(:search) ");
        }

        if (isActive != null) {
            jpql.append("AND c.isActive = :isActive ");
        }

        TypedQuery<Long> query =
                entityManager.createQuery(jpql.toString(), Long.class);

        if (search != null && !search.isBlank()) {
            query.setParameter("search", "%" + search + "%");
        }

        if (isActive != null) {
            query.setParameter("isActive", isActive);
        }

        return query.getSingleResult();
    }
}

