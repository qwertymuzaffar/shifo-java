package com.shifo.shifo_java.features.procedure;

import com.shifo.shifo_java.features.procedure.Procedure;
import com.shifo.shifo_java.features.procedure.dto.ProcedureDto;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProcedureMapper {

    public ProcedureDto toDto(Procedure procedure) {
        if (procedure == null) {
            return null;
        }

        ProcedureDto dto = new ProcedureDto();

        dto.setId(procedure.getId());
        dto.setName(procedure.getName());
        dto.setDescription(procedure.getDescription());
        dto.setDuration(procedure.getDuration());
        dto.setCost(procedure.getCost());

        // Derived field (soft delete aware)
        dto.setIsActive(procedure.getDeletedAt() == null);

        dto.setCreatedAt(procedure.getCreatedAt());
        dto.setUpdatedAt(procedure.getUpdatedAt());
        dto.setDeletedAt(procedure.getDeletedAt());

        return dto;
    }

    public List<ProcedureDto> toDtoList(List<Procedure> procedures) {
        if (procedures == null || procedures.isEmpty()) {
            return Collections.emptyList();
        }

        return procedures.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
