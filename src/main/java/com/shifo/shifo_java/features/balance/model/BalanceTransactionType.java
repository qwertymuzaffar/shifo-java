package com.shifo.shifo_java.features.balance.model;

public enum BalanceTransactionType {

    // Patient added money
    PREPAYMENT,

    // Service created debt
    DEBT_CREATED,

    // Patient paid existing debt
    DEBT_PAYMENT,

    // Money deducted for appointment/service
    SERVICE_CHARGE,

    // Manual correction (future admin feature)
    ADJUSTMENT
}
