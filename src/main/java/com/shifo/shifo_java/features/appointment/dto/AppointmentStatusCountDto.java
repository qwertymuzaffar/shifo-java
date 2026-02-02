package com.shifo.shifo_java.features.appointment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentStatusCountDto {
    private long planned;
    private long completed;
    private long cancelled;
}
