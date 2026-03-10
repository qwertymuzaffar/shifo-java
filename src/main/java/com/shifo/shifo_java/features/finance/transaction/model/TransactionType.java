package com.shifo.shifo_java.features.finance.transaction.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TransactionType {

    EXPENSE,
    INCOME;

    @JsonCreator
    public static TransactionType from(String value) {
        if (value == null) {
            return null;
        }
        return TransactionType.valueOf(value.trim().toUpperCase());
    }
}
