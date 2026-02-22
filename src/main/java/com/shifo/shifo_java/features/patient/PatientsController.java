package com.shifo.shifo_java.features.patient;

import com.shifo.shifo_java.common.dto.PagedResponseDto;
import com.shifo.shifo_java.features.patient.dto.*;
import com.shifo.shifo_java.features.patient.enums.PatientSource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Tag(name = "Patients", description = "Patients API")
@SecurityRequirement(name = "Bearer Authentication")
public class PatientsController {

    private final PatientsService patientsService;


    @PostMapping
    @Operation(summary = "Создать нового пациента")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пациент успешно создан"),
            @ApiResponse(responseCode = "400", description = "Неверные данные")
    })
    public ResponseEntity<Patient> create(@Valid @RequestBody CreatePatientDto createPatientDto) {
        Patient patient = patientsService.create(createPatientDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(patient);
    }

    @GetMapping
    @Operation(summary = "Получить всех пациентов с фильтрацией и пагинацией")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список пациентов с пагинацией")
    })
    public ResponseEntity<PagedResponseDto<PatientDto>> findAll(@Valid @ModelAttribute FilterPatientDto filterDto) {
        PagedResponseDto<PatientDto> patients = patientsService.findAll(filterDto);
        return ResponseEntity.ok(patients);
    }

    @GetMapping("telegram")
    @Operation(summary = "Получить всех (telegram)пациентов с фильтрацией и пагинацией")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список пациентов с пагинацией")
    })
    public ResponseEntity<PagedResponseDto<PatientDto>> findAllTelegram(@Valid @ModelAttribute FilterPatientDto filterDto) {
        filterDto.setSource(PatientSource.TELEGRAM);
        PagedResponseDto<PatientDto> patients = patientsService.findAll(filterDto);
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить пациента по ID")
    @Parameter(name = "id", description = "ID пациента")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Информация о пациенте",
                    content = @Content(schema = @Schema(implementation = PatientDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пациент не найден"
            )
    })
    public ResponseEntity<PatientDto> findOne(@PathVariable Long id) {
        PatientDto patient = patientsService.findOne(id);
        return ResponseEntity.ok(patient);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Обновить данные пациента")
    @Parameter(name = "id", description = "ID пациента")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Пациент успешно обновлен",
                    content = @Content(schema = @Schema(implementation = PatientDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Неверные данные"),
            @ApiResponse(responseCode = "404", description = "Пациент не найден")
    })
    public ResponseEntity<PatientDto> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePatientDto dto
    ) {
        PatientDto updated = patientsService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Деактивировать (мягкое удаление) пациента")
    @Parameter(name = "id", description = "ID пациента")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пациент деактивирован"),
            @ApiResponse(responseCode = "404", description = "Пациент не найден")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        patientsService.deactivate(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Обновить статус пациента")
    @Parameter(name = "id", description = "ID пациента")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Статус обновлен",
                    content = @Content(schema = @Schema(implementation = PatientDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Неверный статус"),
            @ApiResponse(responseCode = "404", description = "Пациент не найден")
    })
    public ResponseEntity<PatientDto> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePatientStatusDto body
    ) {
        PatientDto updated = patientsService.updateStatus(id, body.getStatus());
        return ResponseEntity.ok(updated);
    }
}
