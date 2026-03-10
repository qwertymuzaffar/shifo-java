package com.shifo.shifo_java.features.balance.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum EntityType {
    PAYMENT,
    TRANSACTION;

    @JsonCreator
    public static EntityType from(String value) {
        return EntityType.valueOf(value.toUpperCase());
    }
}
