package com.shifo.shifo_java.features.appointment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelAppointmentDto {

    @Schema(description = "Reason for cancelling the appointment")
    @NotBlank(message = "Reason cannot be empty")
    private String reason;
}

