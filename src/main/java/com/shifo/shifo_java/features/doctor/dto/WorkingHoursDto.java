package com.shifo.shifo_java.features.doctor.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkingHoursDto {

    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "format must be HH:mm")
    private String start;

    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "format must be HH:mm")
    private String end;

    private List<Integer> workingDays;
}

