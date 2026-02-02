package com.shifo.shifo_java.features.transaction;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    @Query("""
                SELECT SUM(t.amount) 
                FROM Transaction t 
                WHERE t.type = :type
                  AND (:dateFrom IS NULL OR t.createdAt >= CONCAT(:dateFrom, 'T00:00:00'))
                  AND (:dateTo IS NULL OR t.createdAt <= CONCAT(:dateTo, 'T23:59:59'))
            """)
    Optional<BigDecimal> sumByType(
            TransactionType type,
            String dateFrom,
            String dateTo
    );
}

