package com.shifo.shifo_java.features.specialization.dto;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SpecializationDto {

    private Long id;
    private String name;
    private Long doctorCount;
}
