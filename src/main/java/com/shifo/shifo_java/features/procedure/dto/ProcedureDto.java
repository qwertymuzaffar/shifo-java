package com.shifo.shifo_java.features.procedure.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProcedureDto {

    private Long id;

    @NotBlank
    private String name;

    private String description;

    private Integer duration;

    private BigDecimal cost;

    private Boolean isActive;

    private Instant createdAt;

    private Instant updatedAt;
}

