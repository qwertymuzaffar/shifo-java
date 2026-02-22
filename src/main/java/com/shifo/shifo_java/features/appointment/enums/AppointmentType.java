package com.shifo.shifo_java.features.appointment.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Тип приёма")
public enum AppointmentType {

    CONSULTATION,
    FOLLOWUP,
    PROCEDURE,
    EMERGENCY
}
