package com.shifo.shifo_java.features.appointment;

import com.shifo.shifo_java.common.dto.PagedResponseDto;
import com.shifo.shifo_java.features.appointment.dto.*;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointments", description = "Appointments API")
@SecurityRequirement(name = "Bearer Authentication")
public class AppointmentsController {

    private final AppointmentsService appointmentService;


    @PostMapping
    public ResponseEntity<CreateAppointmentResultDto> create(
            @Valid @RequestBody CreateAppointmentDto dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(appointmentService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<AppointmentDto>> findAll(@ModelAttribute FilterAppointmentDto filter) {
        return ResponseEntity.ok(appointmentService.findAll(filter));
    }

}
