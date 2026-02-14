package com.shifo.shifo_java.features.specialization;

import com.shifo.shifo_java.features.specialization.dto.SpecializationDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/specializations")
@RequiredArgsConstructor
@Tag(name = "Specializations")
@SecurityRequirement(name = "Bearer Authentication")
public class SpecializationsController {

    private final SpecializationsService specializationsService;

    @GetMapping
    public List<SpecializationDto> findAll() {
        return specializationsService.findAll();
    }

    @GetMapping("/{id}")
    public Specialization findOne(@PathVariable Long id) {
        return specializationsService.findOne(id);
    }

    @PostMapping
    public Specialization create(@RequestBody Map<String, String> body) {
        return specializationsService.create(body.get("name"));
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable Long id) {
        specializationsService.remove(id);
    }
}

