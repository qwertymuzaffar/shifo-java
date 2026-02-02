package com.shifo.shifo_java.features.procedure.dto;

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

    private String name;

    private String description;

    private Integer duration;

    private BigDecimal cost;

    private Boolean isActive;

    private Instant createdAt;

    private Instant updatedAt;

    private Instant deletedAt;
}

