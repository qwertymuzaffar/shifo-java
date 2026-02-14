package com.shifo.shifo_java.features.doctor;

import com.shifo.shifo_java.common.dto.PagedResponseDto;
import com.shifo.shifo_java.features.doctor.dto.CreateDoctorDto;
import com.shifo.shifo_java.features.doctor.dto.DoctorDto;
import com.shifo.shifo_java.features.doctor.dto.FilterDoctorDto;
import io.swagger.v3.oas.annotations.Operation;
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
    public ResponseEntity<Doctor> create(
            @Valid @RequestBody CreateDoctorDto dto
    ) {
        Doctor doctor = doctorService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(doctor);
    }

    @GetMapping
    @Operation(summary = "Получить всех врачей с фильтрацией и пагинацией")
    public PagedResponseDto<DoctorDto> findAll(@Valid FilterDoctorDto filterDto) {
        return doctorService.findAll(filterDto);
    }
}
