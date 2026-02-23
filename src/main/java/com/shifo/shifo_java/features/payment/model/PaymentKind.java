package com.shifo.shifo_java.features.payment.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentKind {

    PAYMENT("payment"),
    DEBT("debt"),
    PREPAYMENT("prepayment"),
    DEBT_PAYMENT("debt_payment"),
    BALANCE_DEDUCTION("balance_deduction");

    private final String value;

    PaymentKind(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static PaymentKind fromValue(String value) {
        for (PaymentKind kind : values()) {
            if (kind.value.equalsIgnoreCase(value)) {
                return kind;
            }
        }
        throw new IllegalArgumentException("Unknown paymentKind: " + value);
    }
}
