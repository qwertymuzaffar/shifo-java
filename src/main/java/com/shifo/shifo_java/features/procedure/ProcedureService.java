package com.shifo.shifo_java.features.procedure;

import com.shifo.shifo_java.common.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProcedureService {

    private final ProcedureRepository procedureRepository;

    public List<Procedure> findAll() {
        return procedureRepository.findAll();
    }

    public Procedure findOne(Long id) {
        return procedureRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Procedure not found"));
    }

    public Procedure create(String name) {
        Procedure procedure = new Procedure();
        procedure.setName(name);
        return procedureRepository.save(procedure);
    }

    public Procedure update(Long id, String name) {
        Procedure procedure = findOne(id);
        procedure.setName(name);
        return procedure;
    }

    public void remove(Long id) {
        Procedure procedure = findOne(id);
        procedureRepository.delete(procedure); // soft delete
    }
}

