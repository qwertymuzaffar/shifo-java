package com.shifo.shifo_java.features.appointment;

import com.shifo.shifo_java.common.security.annotations.RequiresPermission;
import com.shifo.shifo_java.common.security.enums.Permission;
import com.shifo.shifo_java.features.payment.dto.PaymentMethodDto;
import com.shifo.shifo_java.features.appointment.dto.*;
import com.shifo.shifo_java.features.appointment.AppointmentsService;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
@Tag(name = "appointments", description = "Appointment Operations")
public class AppointmentsController {

    private final AppointmentsService appointmentsService;

    // --------------------------------------------------------------------

    @Post
    @PreAuthorize(Permission.APPOINTMENT_CREATE)
    @Operation(summary = "Создать новый приём")
    @ApiResponse(responseCode = "201", description = "Приём успешно создан")
    @ApiResponse(responseCode = "400", description = "Неверные данные")
    @ApiResponse(responseCode = "404", description = "Врач или пациент не найден")
    public Map<String, Object> create(
            @Valid @RequestBody CreateAppointmentDto dto
    ) {
        return appointmentsService.create(dto);
    }

    // --------------------------------------------------------------------

    @Get
    @RequiresPermission(Permission.APPOINTMENT_VIEW)
    @Operation(summary = "Получить все приёмы с фильтрацией и пагинацией")
    @ApiResponse(responseCode = "200", description = "Приёмы успешно получены")
    public Object findAll(
            @Valid FilterAppointmentDto filter
    ) {
        return appointmentsService.findAll(filter);
    }

    // --------------------------------------------------------------------

    @Get("/{id}")
    @RequiresPermission(Permission.APPOINTMENT_VIEW)
    @Operation(summary = "Получить приём по ID")
    @ApiResponse(responseCode = "200", description = "Приём найден")
    @ApiResponse(responseCode = "404", description = "Приём не найден")
    public Appointment findOne(
            @Parameter(description = "ID приёма")
            @PathVariable Long id
    ) {
        return appointmentsService.findOne(id);
    }

    // --------------------------------------------------------------------

    @Patch("/{id}")
    @RequiresPermission(Permission.APPOINTMENT_UPDATE)
    @Operation(summary = "Обновить данные приёма")
    @ApiResponse(responseCode = "200", description = "Приём успешно обновлён")
    @ApiResponse(responseCode = "400", description = "Неверные данные")
    @ApiResponse(responseCode = "404", description = "Приём не найден")
    public Appointment update(
            @Parameter(description = "ID приёма")
            @PathVariable Long id,
            @Valid @RequestBody UpdateAppointmentDto dto
    ) {
        return appointmentsService.update(id, dto);
    }

    // --------------------------------------------------------------------

    @Post("/complete/{id}")
    @RequiresPermission(Permission.APPOINTMENT_CONFIRM)
    @Operation(summary = "Завершить приём и создать платёж")
    @ApiResponse(responseCode = "200", description = "Приём успешно завершён")
    @ApiResponse(responseCode = "404", description = "Приём не найден")
    public Map<String, Object> complete(
            @PathVariable Long id,
            @Valid @RequestBody PaymentMethodDto paymentDto
    ) {
        return appointmentsService.complete(id, paymentDto);
    }

    // --------------------------------------------------------------------

    @Post("/duplicate/{id}")
    @RequiresPermission(Permission.APPOINTMENT_CREATE)
    @Operation(summary = "Дублировать приём по ID с новой датой/временем")
    @ApiResponse(responseCode = "201", description = "Приём продублирован")
    @ApiResponse(responseCode = "404", description = "Приём не найден")
    public Appointment duplicate(
            @Parameter(description = "ID приёма")
            @PathVariable Long id,
            @Valid @RequestBody DuplicateAppointmentDto dto
    ) {
        return appointmentsService.duplicate(id, dto.getDate(), dto.getTime());
    }

    // --------------------------------------------------------------------

    @Post("/duplicate-range")
    @RequiresPermission(Permission.APPOINTMENT_CREATE)
    @Operation(summary = "Дублировать приёмы в диапазоне дат")
    @ApiResponse(responseCode = "201", description = "Приёмы успешно продублированы")
    @ApiResponse(responseCode = "400", description = "Неверные данные")
    public Map<String, Object> duplicateRange(
            @Valid @RequestBody DateRangeDuplicateDto dto
    ) {
        return appointmentsService.duplicateRange(dto.getCopyDate(), dto.getDateTo());
    }

    // --------------------------------------------------------------------

    @Patch("/cancel/{id}")
    @RequiresPermission(Permission.APPOINTMENT_CANCEL)
    @Operation(summary = "Отменить приём")
    @ApiResponse(responseCode = "200", description = "Приём успешно отменён")
    @ApiResponse(responseCode = "404", description = "Приём не найден")
    public Appointment cancel(
            @Parameter(description = "ID приёма")
            @PathVariable Long id,
            @Valid @RequestBody CancelAppointmentDto dto
    ) {
        return appointmentsService.cancel(id, dto.getReason());
    }
}
