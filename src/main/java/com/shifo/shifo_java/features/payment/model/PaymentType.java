package com.shifo.shifo_java.features.payment.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentType {

    DC("dc"),
    ALIF("alif"),
    ESKHATA("eskhata"),
    CASH("cash"),
    CARD("card"),
    TRANSFER("transfer");

    private final String value;

    PaymentType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static PaymentType fromValue(String value) {
        for (PaymentType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown paymentType: " + value);
    }
}
