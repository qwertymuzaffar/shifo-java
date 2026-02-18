package com.shifo.shifo_java.features.patient.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PatientRegistrationStatus {

    PENDING("pending"),
    APPROVED("approved"),
    REJECTED("rejected");

    private final String value;

    PatientRegistrationStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static PatientRegistrationStatus fromValue(String value) {
        for (PatientRegistrationStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid PatientRegistrationStatus: " + value);
    }
}
