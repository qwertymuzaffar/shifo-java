package com.shifo.shifo_java.features.procedure;

import com.shifo.shifo_java.common.exceptions.NotFoundException;
import com.shifo.shifo_java.features.permission.dto.PermissionDto;
import com.shifo.shifo_java.features.procedure.dto.CreateProcedureDto;
import com.shifo.shifo_java.features.procedure.dto.ProcedureDto;
import com.shifo.shifo_java.features.procedure.dto.UpdateProcedureDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProcedureService {

    private final ProcedureRepository procedureRepository;
    private final ProcedureMapper procedureMapper;

    @Transactional(readOnly = true)
    public List<ProcedureDto> findAll() {
        return procedureRepository.findAll()
                .stream()
                .map(procedureMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProcedureDto findOne(Long id) {
        Procedure procedure = getProcedureOrThrow(id);
        return procedureMapper.toDto(procedure);
    }

    public ProcedureDto create(CreateProcedureDto dto) {
        Procedure procedure = procedureMapper.toEntity(dto);
        Procedure savedProcedure = procedureRepository.save(procedure);

        return procedureMapper.toDto(savedProcedure);
    }

    public ProcedureDto update(Long id, UpdateProcedureDto dto) {
        Procedure procedure = getProcedureOrThrow(id);
        procedureMapper.updateEntity(dto, procedure);

        return procedureMapper.toDto(procedure);
    }

    public void remove(Long id) {
        Procedure procedure = getProcedureOrThrow(id);
        procedureRepository.delete(procedure);
    }

    private Procedure getProcedureOrThrow(Long id) {
        return procedureRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Procedure with id " + id + " not found"
                ));
    }
}

