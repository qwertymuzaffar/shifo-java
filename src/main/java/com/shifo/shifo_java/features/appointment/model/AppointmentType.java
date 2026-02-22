package com.shifo.shifo_java.features.appointment.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Тип приёма")
public enum AppointmentType {

    CONSULTATION("consultation"),
    FOLLOWUP("followup"),
    PROCEDURE("procedure"),
    EMERGENCY("emergency");

    private final String value;

    AppointmentType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static AppointmentType fromValue(String value) {
        for (AppointmentType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown AppointmentType: " + value);
    }

}
