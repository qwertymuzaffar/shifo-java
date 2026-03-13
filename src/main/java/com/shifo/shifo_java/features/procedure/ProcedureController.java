package com.shifo.shifo_java.features.procedure;

import com.shifo.shifo_java.features.procedure.dto.CreateProcedureDto;
import com.shifo.shifo_java.features.procedure.dto.ProcedureDto;
import com.shifo.shifo_java.features.procedure.dto.UpdateProcedureDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/procedures")
@RequiredArgsConstructor
@Tag(name = "Procedures")
public class ProcedureController {

    private final ProcedureService procedureService;

    @GetMapping
    public List<ProcedureDto> findAll() {
        return procedureService.findAll();
    }

    @GetMapping("/{id}")
    public ProcedureDto findOne(@PathVariable Long id) {
        return procedureService.findOne(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProcedureDto create(@Valid @RequestBody CreateProcedureDto request) {
        return procedureService.create(request);
    }

    @PutMapping("/{id}")
    public ProcedureDto update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProcedureDto request
    ) {
        return procedureService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long id) {
        procedureService.remove(id);
    }
}

