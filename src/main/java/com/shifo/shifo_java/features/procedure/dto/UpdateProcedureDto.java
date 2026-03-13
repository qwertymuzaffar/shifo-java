package com.shifo.shifo_java.features.procedure.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProcedureDto {
    Long id;

    @NotBlank
    String name;
}
