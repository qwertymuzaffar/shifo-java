package com.shifo.shifo_java.features.appointment.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Статус приёма")
public enum AppointmentStatus {

    SCHEDULED,
    CANCELLED,
    COMPLETED,
    TEMPORARY,
    CANCELLED_FOREVER
}
