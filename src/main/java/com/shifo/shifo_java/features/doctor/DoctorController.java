package com.shifo.shifo_java.features.doctor;

import com.shifo.shifo_java.common.dto.PagedResponseDto;
import com.shifo.shifo_java.features.doctor.dto.CreateDoctorDto;
import com.shifo.shifo_java.features.doctor.dto.DoctorDto;
import com.shifo.shifo_java.features.doctor.dto.FilterDoctorDto;
import com.shifo.shifo_java.features.doctor.dto.UpdateDoctorDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctors")
@SecurityRequirement(name = "Bearer Authentication")
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    @Operation(summary = "Создать нового врача")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Врач успешно создан"),
            @ApiResponse(responseCode = "400", description = "Неверные данные")
    })
    @PreAuthorize("hasAuthority('doctor.create')")
    public ResponseEntity<Doctor> create(
            @Valid @RequestBody CreateDoctorDto dto
    ) {
        Doctor doctor = doctorService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(doctor);
    }

    @GetMapping
    @Operation(summary = "Получить всех врачей с фильтрацией и пагинацией")
    @PreAuthorize("hasAuthority('doctor.view')")
    public PagedResponseDto<DoctorDto> findAll(@Valid FilterDoctorDto filterDto) {
        return doctorService.findAll(filterDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить врача по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Информация о враче"),
            @ApiResponse(responseCode = "404", description = "Врач не найден")
    })
    @PreAuthorize("hasAuthority('doctor.view')")
    public DoctorDto findOne(@PathVariable Long id) {
        return doctorService.findOne(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Деактивировать (мягкое удаление) врача")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Врач деактивирован"),
            @ApiResponse(responseCode = "404", description = "Врач не найден")
    })
    @PreAuthorize("hasAuthority('doctor.delete')")
    public void remove(@PathVariable Long id) {
        doctorService.remove(id);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Обновить данные врача")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Врач успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Врач не найден"),
            @ApiResponse(responseCode = "400", description = "Неверные данные")
    })
    @PreAuthorize("hasAuthority('doctor.update')")
    public DoctorDto update(
            @PathVariable Long id,
            @RequestBody UpdateDoctorDto dto
    ) {
        return doctorService.update(id, dto);
    }
}
