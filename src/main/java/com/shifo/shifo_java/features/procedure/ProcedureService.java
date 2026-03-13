package com.shifo.shifo_java.features.procedure;

import com.shifo.shifo_java.common.exceptions.NotFoundException;
import com.shifo.shifo_java.features.procedure.dto.CreateProcedureDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProcedureService {

    private final ProcedureRepository procedureRepository;

    @Transactional(readOnly = true)
    public List<Procedure> findAll() {
        return procedureRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Procedure findOne(Long id) {
        return getProcedureOrThrow(id);
    }

    public Procedure create(String name) {
        Procedure procedure = new Procedure();
        procedure.setName(name);
        return procedureRepository.save(procedure);
    }

    public Procedure update(Long id, String name) {
        Procedure procedure = getProcedureOrThrow(id);
        procedure.setName(name);
        return procedureRepository.save(procedure);
    }

    public void remove(Long id) {
        Procedure procedure = getProcedureOrThrow(id);
        procedureRepository.delete(procedure);
    }

    private Procedure getProcedureOrThrow(Long id) {
        return procedureRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Procedure with id " + id + " not found"));
    }
}

