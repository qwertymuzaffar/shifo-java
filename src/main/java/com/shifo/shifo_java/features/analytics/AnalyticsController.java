package com.shifo.shifo_java.features.analytics;

import com.shifo.shifo_java.features.analytics.AnalyticsService;
import com.shifo.shifo_java.common.enums.AppointmentType;
import com.shifo.shifo_java.security.permissions.RequiresPermission;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    @RequiresPermission("analytics.view") // equivalent to Permission.ANALYTICS_VIEW
    @Operation(summary = "Dashboard analytics", description = "Returns analytics data for a given period")
    public ResponseEntity<?> getDashboardAnalytics(

            @Parameter(
                    description = "Start date (YYYY-MM-DD)",
                    required = true,
                    example = "2024-01-01")
            @RequestParam String dateFrom,

            @Parameter(
                    description = "End date (YYYY-MM-DD)",
                    required = true,
                    example = "2024-12-31")
            @RequestParam String dateTo,

            @Parameter(
                    description = "Comma-separated doctor IDs",
                    example = "1,2,3")
            @RequestParam(required = false) String doctorIds,

            @Parameter(
                    description = "Appointment types (comma-separated)",
                    example = "consultation,procedure",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AppointmentType.class)
            )
            @RequestParam(required = false) String appointmentTypes
    ) {

        return ResponseEntity.ok(
                analyticsService.getDashboardAnalytics(dateFrom, dateTo, doctorIds, appointmentTypes)
        );
    }
}

