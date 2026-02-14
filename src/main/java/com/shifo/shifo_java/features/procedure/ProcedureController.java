package com.shifo.shifo_java.features.procedure;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/procedures")
@RequiredArgsConstructor
@Tag(name = "Procedures")
public class ProcedureController {

    private final ProcedureService procedureService;

    @GetMapping
    public List<Procedure> findAll() {
        return procedureService.findAll();
    }

    @GetMapping("/{id}")
    public Procedure findOne(@PathVariable Long id) {
        return procedureService.findOne(id);
    }

    @PostMapping
    public Procedure create(@RequestBody Map<String, String> body) {
        return procedureService.create(body.get("name"));
    }

    @PutMapping("/{id}")
    public Procedure update(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        return procedureService.update(id, body.get("name"));
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable Long id) {
        procedureService.remove(id);
    }
}

