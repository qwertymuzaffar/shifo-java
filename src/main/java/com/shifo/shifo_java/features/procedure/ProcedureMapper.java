package com.shifo.shifo_java.features.procedure;

import com.shifo.shifo_java.features.procedure.dto.CreateProcedureDto;
import com.shifo.shifo_java.features.procedure.dto.ProcedureDto;
import com.shifo.shifo_java.features.procedure.dto.UpdateProcedureDto;
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

        dto.setIsActive(procedure.getDeletedAt() == null);

        dto.setCreatedAt(procedure.getCreatedAt());
        dto.setUpdatedAt(procedure.getUpdatedAt());

        return dto;
    }

    public Procedure toEntity(CreateProcedureDto dto) {
        if (dto == null) {
            return null;
        }

        Procedure procedure = new Procedure();
        procedure.setName(dto.getName());

        return procedure;
    }

    public void updateEntity(UpdateProcedureDto dto, Procedure procedure) {
        if (dto == null) {
            return;
        }

        procedure.setName(dto.getName());
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
