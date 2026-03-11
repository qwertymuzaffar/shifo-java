package com.shifo.shifo_java.features.appointment;

import com.shifo.shifo_java.features.appointment.application.CreateBatchAppointmentsService;
import com.shifo.shifo_java.features.appointment.dto.*;
import lombok.RequiredArgsConstructor;
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

    private final AppointmentService appointmentService;


    private final CreateBatchAppointmentsService createBatchAppointmentsService;

    @PostMapping
    @PreAuthorize("hasAuthority('appointment.create')")
    public ResponseEntity<CreateAppointmentResultDto> create(
            @RequestBody @Valid CreateAppointmentDto dto
    ) {
        CreateAppointmentResultDto result =
                createBatchAppointmentsService.create(dto);

        return ResponseEntity.ok(result);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('appointment.view')")
    public ResponseEntity<List<AppointmentDto>> findAll(@ModelAttribute FilterAppointmentDto filter) {
        return ResponseEntity.ok(appointmentService.findAll(filter));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('appointment.view')")
    public ResponseEntity<AppointmentDetailsDto> findOne(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.findOne(id));
    }

}
