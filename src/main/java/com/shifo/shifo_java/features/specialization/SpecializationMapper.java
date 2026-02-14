package com.shifo.shifo_java.features.specialization;

import com.shifo.shifo_java.features.specialization.dto.SpecializationDto;
import org.springframework.stereotype.Component;

@Component
public class SpecializationMapper {

    public SpecializationDto toDto(Specialization specialization) {
        if (specialization == null) {
            return null;
        }

        SpecializationDto dto = new SpecializationDto();
        dto.setId(specialization.getId());
        dto.setName(specialization.getName());
        return dto;
    }
}
