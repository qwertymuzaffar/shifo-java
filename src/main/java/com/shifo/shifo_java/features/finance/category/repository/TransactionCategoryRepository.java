package com.shifo.shifo_java.features.finance.category.repository;

import com.shifo.shifo_java.features.finance.category.TransactionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionCategoryRepository
        extends JpaRepository<TransactionCategory, Long>,
        TransactionCategoryRepositoryCustom {

    boolean existsByName(String name);

    @Query("""
            SELECT COUNT(t)
            FROM Transaction t
            WHERE t.category.id = :categoryId
            """)
    long countTransactionsByCategoryId(@Param("categoryId") Long categoryId);
}
