package com.shifo.shifo_java.features.patient.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PatientSource {

    TELEGRAM("telegram"),
    MANUAL("manual");

    private final String value;

    PatientSource(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static PatientSource fromValue(String value) {
        for (PatientSource source : values()) {
            if (source.value.equalsIgnoreCase(value)) {
                return source;
            }
        }
        throw new IllegalArgumentException("Invalid PatientSource: " + value);
    }
}

