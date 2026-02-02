package com.shifo.shifo_java.features.analytics;

import com.shifo.shifo_java.common.enums.AppointmentStatus;
import com.shifo.shifo_java.common.enums.AppointmentType;
import com.shifo.shifo_java.common.enums.PaymentMethod;
import com.shifo.shifo_java.features.appointment.AppointmentRepository;
import com.shifo.shifo_java.features.doctor.Doctor;
import com.shifo.shifo_java.features.doctor.DoctorRepository;
import com.shifo.shifo_java.features.patient.PatientRepository;
import com.shifo.shifo_java.features.payment.PaymentRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PaymentRepository paymentRepository;

    // ------------ Helpers: parse filters from query strings ------------

    private List<Long> parseDoctorIds(String doctorIds) {
        if (doctorIds == null || doctorIds.isBlank()) {
            return null;
        }
        List<Long> ids = Arrays.stream(doctorIds.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    try {
                        return Long.parseLong(s);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return ids.isEmpty() ? null : ids;
    }

    private List<AppointmentType> parseAppointmentTypes(String appointmentTypes) {
        if (appointmentTypes == null || appointmentTypes.isBlank()) {
            return null;
        }
        List<AppointmentType> types = Arrays.stream(appointmentTypes.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toUpperCase)
                .map(name -> {
                    try {
                        return AppointmentType.valueOf(name);
                    } catch (IllegalArgumentException ex) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return types.isEmpty() ? null : types;
    }

    // ------------ Metrics helpers ------------

    public Map<String, Object> getTotalPatientsByDateRange(Instant start, Instant end) {
        long currentPeriod = patientRepository.countByCreatedAtBetween(start, end);

        long durationMillis = end.toEpochMilli() - start.toEpochMilli();
        Instant previousStart = start.minusMillis(durationMillis);
        Instant previousEnd = start;

        long previousPeriod = patientRepository.countByCreatedAtBetween(previousStart, previousEnd);

        double growth = (previousPeriod > 0)
                ? ((double) (currentPeriod - previousPeriod) / previousPeriod) * 100.0
                : 0.0;

        String growthStr = String.format(Locale.US, "%.1f", growth);

        Map<String, Object> result = new HashMap<>();
        result.put("total", currentPeriod);
        result.put("growth", growthStr);
        return result;
    }

    public long getTotalActiveDoctors(List<Long> doctorIds) {
        if (doctorIds != null && !doctorIds.isEmpty()) {
            return doctorRepository.countByIsActiveTrueAndIdIn(doctorIds);
        }
        return doctorRepository.countByIsActiveTrue();
    }

    public Map<String, Object> getTotalAppointmentsByDateRange(
            LocalDate startDate,
            LocalDate endDate,
            List<Long> doctorIds,
            List<AppointmentType> types
    ) {
        long currentPeriod = appointmentRepository.countByDateRangeAndFilters(
                startDate, endDate, doctorIds, types, null
        );

        long durationDays = Duration.between(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay()).toDays();
        LocalDate prevEndDate = startDate.minusDays(1);
        LocalDate prevStartDate = prevEndDate.minusDays(durationDays - 1);

        long previousPeriod = appointmentRepository.countByDateRangeAndFilters(
                prevStartDate, prevEndDate, doctorIds, types, null
        );

        double growth = (previousPeriod > 0)
                ? ((double) (currentPeriod - previousPeriod) / previousPeriod) * 100.0
                : 0.0;

        String growthStr = String.format(Locale.US, "%.1f", growth);

        Map<String, Object> result = new HashMap<>();
        result.put("total", currentPeriod);
        result.put("growth", growthStr);
        return result;
    }

    public Map<String, Object> getTotalRevenueByDateRange(
            Instant start,
            Instant end,
            List<Long> doctorIds,
            List<AppointmentType> types
    ) {
        BigDecimal totalOverall = paymentRepository.sumTotalPaidAmount();
        BigDecimal currentPeriod = paymentRepository
                .sumPaidAmountByDateRangeAndFilters(start, end, doctorIds, types);
        if (currentPeriod == null) {
            currentPeriod = BigDecimal.ZERO;
        }

        long durationMillis = end.toEpochMilli() - start.toEpochMilli();
        Instant previousStart = start.minusMillis(durationMillis);
        Instant previousEnd = start;

        BigDecimal previousPeriod =
                paymentRepository.sumPaidAmountByDateRangeAndFilters(previousStart, previousEnd, doctorIds, types);
        if (previousPeriod == null) {
            previousPeriod = BigDecimal.ZERO;
        }

        double growth = previousPeriod.compareTo(BigDecimal.ZERO) > 0
                ? currentPeriod.subtract(previousPeriod)
                .divide(previousPeriod, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue()
                : 0.0;

        String growthStr = String.format(Locale.US, "%.1f", growth);

        Map<String, Object> result = new HashMap<>();
        result.put("total", currentPeriod);
        result.put("growth", growthStr);
        return result;
    }

    public Map<String, Object> getAppointmentStatusesByDateRange(
            LocalDate startDate,
            LocalDate endDate,
            List<Long> doctorIds,
            List<AppointmentType> types
    ) {
        long planned = appointmentRepository.countByDateRangeAndFilters(
                startDate, endDate, doctorIds, types, AppointmentStatus.SCHEDULED
        );
        long completed = appointmentRepository.countByDateRangeAndFilters(
                startDate, endDate, doctorIds, types, AppointmentStatus.COMPLETED
        );
        long cancelled = appointmentRepository.countByDateRangeAndFilters(
                startDate, endDate, doctorIds, types, AppointmentStatus.CANCELLED
        );

        Map<String, Object> result = new HashMap<>();
        result.put("planned", planned);
        result.put("completed", completed);
        result.put("cancelled", cancelled);
        return result;
    }

    public Map<String, Object> getAppointmentTypesByDateRange(
            LocalDate startDate,
            LocalDate endDate,
            List<Long> doctorIds,
            List<AppointmentType> typesFilter
    ) {
        // typesFilter is the "allowed types" overall; we still count by each type
        List<AppointmentType> allowed = typesFilter != null ? typesFilter : null;

        long consultation = appointmentRepository.countByDateRangeAndFilters(
                startDate, endDate, doctorIds, allowed, AppointmentType.CONSULTATION
        );
        long followup = appointmentRepository.countByDateRangeAndFilters(
                startDate, endDate, doctorIds, allowed, AppointmentType.FOLLOWUP
        );
        long procedure = appointmentRepository.countByDateRangeAndFilters(
                startDate, endDate, doctorIds, allowed, AppointmentType.PROCEDURE
        );
        long emergency = appointmentRepository.countByDateRangeAndFilters(
                startDate, endDate, doctorIds, allowed, AppointmentType.EMERGENCY
        );

        Map<String, Object> result = new HashMap<>();
        result.put("consultation", consultation);
        result.put("followup", followup);
        result.put("procedure", procedure);
        result.put("emergency", emergency);
        return result;
    }

    public List<Map<String, Object>> getDoctorRatingsByDateRange(
            LocalDate startDate,
            LocalDate endDate,
            Instant startInstant,
            Instant endInstant,
            List<Long> doctorIds,
            List<AppointmentType> types
    ) {
        List<Doctor> doctors = (doctorIds != null && !doctorIds.isEmpty())
                ? doctorRepository.findByIsActiveTrueAndIdIn(doctorIds)
                : doctorRepository.findByIsActiveTrue();

        List<Map<String, Object>> result = new ArrayList<>();

        for (Doctor doctor : doctors) {
            List<Long> singleDoctor = List.of(doctor.getId());

            long appointments = appointmentRepository.countByDateRangeAndFilters(
                    startDate, endDate, singleDoctor, types, null
            );

            long cancellations = appointmentRepository.countByDateRangeAndFilters(
                    startDate, endDate, singleDoctor, types, AppointmentStatus.CANCELLED
            );

            long completed = appointmentRepository.countByDateRangeAndFilters(
                    startDate, endDate, singleDoctor, types, AppointmentStatus.COMPLETED
            );

            BigDecimal revenue = paymentRepository.sumPaidAmountByDateRangeAndFilters(
                    startInstant, endInstant, singleDoctor, types
            );
            if (revenue == null) {
                revenue = BigDecimal.ZERO;
            }

            double efficiency = appointments > 0
                    ? (double) completed * 100.0 / appointments
                    : 0.0;
            String efficiencyStr = String.format(Locale.US, "%.1f", efficiency);

            Map<String, Object> doctorMap = new HashMap<>();
            doctorMap.put("name", doctor.getFullName());
            doctorMap.put("specialization", doctor.getSpecialization());
            doctorMap.put("appointments", appointments);
            doctorMap.put("cancellations", cancellations);
            doctorMap.put("completed", completed);
            doctorMap.put("efficiency", efficiencyStr);
            doctorMap.put("revenue", revenue);

            result.add(doctorMap);
        }

        return result;
    }

    public List<Map<String, Object>> getPaymentMethodsByDateRange(
            Instant start,
            Instant end,
            List<Long> doctorIds,
            List<AppointmentType> types
    ) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (PaymentMethod method : PaymentMethod.values()) {
            long count = paymentRepository.countPaidByTypeAndDateRange(
                    start, end, method, doctorIds, types
            );
            BigDecimal amount = paymentRepository.sumPaidAmountByTypeAndDateRange(
                    start, end, method, doctorIds, types
            );
            if (amount == null) {
                amount = BigDecimal.ZERO;
            }

            Map<String, Object> map = new HashMap<>();
            map.put("type", method); // or method.name().toLowerCase()
            map.put("count", count);
            map.put("amount", amount);
            result.add(map);
        }

        return result;
    }

    // ------------ Main dashboard method ------------

    public Map<String, Object> getDashboardAnalytics(
            String dateFrom,
            String dateTo,
            String doctorIdsStr,
            String appointmentTypesStr
    ) {
        LocalDate startDate;
        LocalDate endDate;

        try {
            startDate = LocalDate.parse(dateFrom);
            endDate = LocalDate.parse(dateTo);
        } catch (DateTimeParseException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid date format. Use YYYY-MM-DD"
            );
        }

        if (startDate.isAfter(endDate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "dateFrom cannot be greater than dateTo"
            );
        }

        ZoneId zone = ZoneId.systemDefault();
        Instant startInstant = startDate.atStartOfDay(zone).toInstant();
        Instant endInstant = endDate.atTime(LocalTime.MAX).atZone(zone).toInstant();

        List<Long> doctorIds = parseDoctorIds(doctorIdsStr);
        List<AppointmentType> types = parseAppointmentTypes(appointmentTypesStr);

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("totalPatients", getTotalPatientsByDateRange(startInstant, endInstant));
        response.put("totalActiveDoctors", getTotalActiveDoctors(doctorIds));
        response.put("totalAppointments", getTotalAppointmentsByDateRange(startDate, endDate, doctorIds, types));
        response.put("totalRevenue", getTotalRevenueByDateRange(startInstant, endInstant, doctorIds, types));
        response.put("appointmentStatuses", getAppointmentStatusesByDateRange(startDate, endDate, doctorIds, types));
        response.put("appointmentTypes", getAppointmentTypesByDateRange(startDate, endDate, doctorIds, types));
        response.put("doctorRatings", getDoctorRatingsByDateRange(startDate, endDate, startInstant, endInstant, doctorIds, types));
        response.put("paymentMethods", getPaymentMethodsByDateRange(startInstant, endInstant, doctorIds, types));

        return response;
    }
}

