package com.shifo.shifo_java.features.finance.category.repository;

import com.shifo.shifo_java.features.finance.category.TransactionCategory;

import java.util.List;

public interface TransactionCategoryRepositoryCustom {

    List<TransactionCategory> findAllWithFilters(
            String search,
            Boolean isActive,
            int skip,
            int limit
    );

    long countWithFilters(
            String search,
            Boolean isActive
    );

}
