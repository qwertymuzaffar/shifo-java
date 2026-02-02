package com.shifo.shifo_java.service;

import com.shifo.shifo_java.common.dto.DashboardAnalyticsDto;
import com.shifo.shifo_java.common.dto.TotalRevenueDto;
import com.shifo.shifo_java.features.appointment.Appointment;
import com.shifo.shifo_java.features.appointment.dto.AppointmentStatusCountDto;
import com.shifo.shifo_java.features.appointment.dto.AppointmentTypeCountDto;
import com.shifo.shifo_java.features.appointment.dto.TotalAppointmentsDto;
import com.shifo.shifo_java.features.doctor.Doctor;
import com.shifo.shifo_java.features.doctor.dto.DoctorRatingDto;
import com.shifo.shifo_java.features.patient.dto.TotalPatientsDto;
import com.shifo.shifo_java.features.payment.dto.PaymentMethodStatsDto;
import com.shifo.shifo_java.common.enums.AppointmentStatus;
import com.shifo.shifo_java.common.enums.AppointmentType;
import com.shifo.shifo_java.features.appointment.AppointmentRepository;
import com.shifo.shifo_java.features.doctor.DoctorRepository;
import com.shifo.shifo_java.features.patient.PatientRepository;
import com.shifo.shifo_java.features.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PaymentRepository paymentRepository;

    // ------------------------------
    // MAIN DASHBOARD ENTRY POINT
    // ------------------------------

    public DashboardAnalyticsDto getDashboardAnalytics(
            String dateFrom,
            String dateTo,
            String doctorIds,
            String appointmentTypes
    ) {
        LocalDate start = LocalDate.parse(dateFrom);
        LocalDate end = LocalDate.parse(dateTo);

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("dateFrom cannot be greater than dateTo");
        }

        List<Long> doctorList = AnalyticsFilterParser.parseDoctorIds(doctorIds);
        List<AppointmentType> typeList = AnalyticsFilterParser.parseAppointmentTypes(appointmentTypes);

        FilterOptions filters = new FilterOptions(doctorList, typeList);

        return new DashboardAnalyticsDto(
                getTotalPatients(start, end),
                getActiveDoctorCount(filters),
                getAppointmentTotals(start, end, filters),
                getRevenueTotals(start, end, filters),
                getStatusCounts(start, end, filters),
                getTypeCounts(start, end, filters),
                getDoctorRatings(start, end, filters),
                getPaymentMethodStats(start, end, filters)
        );
    }


    // ------------------------------
    // TOTAL PATIENTS
    // ------------------------------

    private TotalPatientsDto getTotalPatients(LocalDate start, LocalDate end) {

        long current = patientRepository.count(
                (root, query, cb) -> cb.between(root.get("createdAt"),
                        start.atStartOfDay().toInstant(ZoneOffset.UTC),
                        end.atTime(23, 59, 59).toInstant(ZoneOffset.UTC))
        );

        long duration = ChronoUnit.DAYS.between(start, end) + 1;

        LocalDate prevStart = start.minusDays(duration);
        LocalDate prevEnd = start.minusDays(1);

        long previous = patientRepository.count(
                (root, query, cb) -> cb.between(root.get("createdAt"),
                        prevStart.atStartOfDay().toInstant(ZoneOffset.UTC),
                        prevEnd.atTime(23, 59, 59).toInstant(ZoneOffset.UTC))
        );

        double growth = previous > 0
                ? ((double) (current - previous) / previous) * 100
                : 0;

        return new TotalPatientsDto(current, String.format("%.1f", growth));
    }


    // ------------------------------
    // ACTIVE DOCTORS
    // ------------------------------

    private long getActiveDoctorCount(FilterOptions filters) {
        if (filters.getDoctorIds() != null) {
            return doctorRepository.countByIsActiveTrueAndIdIn(filters.getDoctorIds());
        }
        return doctorRepository.countByIsActiveTrue();
    }


    // ------------------------------
    // TOTAL APPOINTMENTS
    // ------------------------------

    private TotalAppointmentsDto getAppointmentTotals(LocalDate start, LocalDate end, FilterOptions filters) {

        long current = appointmentRepository.count(
                buildAppointmentSpec(start, end, filters)
        );

        long duration = ChronoUnit.DAYS.between(start, end) + 1;

        LocalDate prevStart = start.minusDays(duration);
        LocalDate prevEnd = start.minusDays(1);

        long previous = appointmentRepository.count(
                buildAppointmentSpec(prevStart, prevEnd, filters)
        );

        double growth = previous > 0
                ? ((double) (current - previous) / previous) * 100
                : 0;

        return new TotalAppointmentsDto(current, String.format("%.1f", growth));
    }


    // ------------------------------
    // TOTAL REVENUE
    // ------------------------------

    private TotalRevenueDto getRevenueTotals(LocalDate start, LocalDate end, FilterOptions filters) {

        Instant s = start.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant e = end.atTime(23, 59, 59).toInstant(ZoneOffset.UTC);

        BigDecimal current = paymentRepository.sumPaidBetween(s, e);

        long days = ChronoUnit.DAYS.between(start, end) + 1;

        Instant prevStart = start.minusDays(days).atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant prevEnd = start.minusDays(1).atTime(23, 59, 59).toInstant(ZoneOffset.UTC);

        BigDecimal previous = paymentRepository.sumPaidBetween(prevStart, prevEnd);

        double growth = (previous != null && previous.compareTo(BigDecimal.ZERO) > 0)
                ? current.subtract(previous).doubleValue() / previous.doubleValue() * 100
                : 0;

        return new TotalRevenueDto(current, String.format("%.1f", growth));
    }


    // ------------------------------
    // STATUS COUNTS
    // ------------------------------

    private AppointmentStatusCountDto getStatusCounts(LocalDate start, LocalDate end, FilterOptions filters) {

        long planned = appointmentRepository.count(
                buildStatusSpec(start, end, filters, AppointmentStatus.SCHEDULED));

        long completed = appointmentRepository.count(
                buildStatusSpec(start, end, filters, AppointmentStatus.COMPLETED));

        long cancelled = appointmentRepository.count(
                buildStatusSpec(start, end, filters, AppointmentStatus.CANCELLED));

        return new AppointmentStatusCountDto(planned, completed, cancelled);
    }


    // ------------------------------
    // TYPE COUNTS
    // ------------------------------

    private AppointmentTypeCountDto getTypeCounts(LocalDate start, LocalDate end, FilterOptions filters) {

        long consultation = appointmentRepository.count(
                buildTypeSpec(start, end, filters, AppointmentType.CONSULTATION));

        long followup = appointmentRepository.count(
                buildTypeSpec(start, end, filters, AppointmentType.FOLLOWUP));

        long procedure = appointmentRepository.count(
                buildTypeSpec(start, end, filters, AppointmentType.PROCEDURE));

        long emergency = appointmentRepository.count(
                buildTypeSpec(start, end, filters, AppointmentType.EMERGENCY));

        return new AppointmentTypeCountDto(consultation, followup, procedure, emergency);
    }


    // ------------------------------
    // DOCTOR RATINGS
    // ------------------------------

    private List<DoctorRatingDto> getDoctorRatings(LocalDate start, LocalDate end, FilterOptions filters) {

        List<Doctor> doctors;

        if (filters.getDoctorIds() != null) {
            doctors = doctorRepository.findAllById(filters.getDoctorIds());
        } else {
            doctors = doctorRepository.findByIsActiveTrue();
        }

        return doctors.stream().map(doc -> {

            long appointments = appointmentRepository.count(
                    buildDoctorSpec(start, end, doc.getId(), filters));

            long cancellations = appointmentRepository.count(
                    buildDoctorStatusSpec(start, end, doc.getId(), AppointmentStatus.CANCELLED));

            long completed = appointmentRepository.count(
                    buildDoctorStatusSpec(start, end, doc.getId(), AppointmentStatus.COMPLETED));

            BigDecimal revenue = paymentRepository.sumPaidByType(null,
                    start.atStartOfDay().toInstant(ZoneOffset.UTC),
                    end.atTime(23, 59, 59).toInstant(ZoneOffset.UTC));

            double efficiency = appointments > 0 ? (double) completed / appointments * 100 : 0;

            return new DoctorRatingDto(
                    doc.getFullName(),
                    doc.getSpecialization() != null ? doc.getSpecialization().getName() : null,
                    appointments,
                    cancellations,
                    completed,
                    String.format("%.1f", efficiency),
                    revenue != null ? revenue : BigDecimal.ZERO
            );

        }).toList();
    }


    // ------------------------------
    // PAYMENT METHOD STATS
    // ------------------------------

    private List<PaymentMethodStatsDto> getPaymentMethodStats(LocalDate start, LocalDate end, FilterOptions filters) {

        Instant s = start.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant e = end.atTime(23, 59, 59).toInstant(ZoneOffset.UTC);

        List<String> types = List.of("dc", "alif", "eskhata", "cash");

        return types.stream().map(type -> {
            long count = paymentRepository.countByPaymentTypeAndStatusAndPaidAtBetween(
                    type, PaymentStatus.PAID, s, e);

            BigDecimal amount = paymentRepository.sumPaidByType(
                    type, s, e);

            return new PaymentMethodStatsDto(type, count, amount != null ? amount : BigDecimal.ZERO);
        }).toList();
    }


    // =================================================================
    // SPECIFICATIONS FOR FILTERS (Spring equivalent of QueryBuilder)
    // =================================================================

    private Specification<Appointment> buildAppointmentSpec(LocalDate from, LocalDate to, FilterOptions filters) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.between(root.get("date"), from, to));

            if (filters.getDoctorIds() != null) {
                predicates.add(root.get("doctor").get("id").in(filters.getDoctorIds()));
            }

            if (filters.getAppointmentTypes() != null) {
                predicates.add(root.get("type").in(filters.getAppointmentTypes()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Specification<Appointment> buildStatusSpec(LocalDate from, LocalDate to, FilterOptions filters,
                                                       AppointmentStatus status) {
        return buildAppointmentSpec(from, to, filters)
                .and((root, query, cb) -> cb.equal(root.get("status"), status));
    }

    private Specification<Appointment> buildTypeSpec(LocalDate from, LocalDate to, FilterOptions filters,
                                                     AppointmentType type) {
        return buildAppointmentSpec(from, to, filters)
                .and((root, query, cb) -> cb.equal(root.get("type"), type));
    }

    private Specification<Appointment> buildDoctorSpec(LocalDate from, LocalDate to, Long doctorId,
                                                       FilterOptions filters) {
        return (root, query, cb) -> cb.and(
                cb.equal(root.get("doctor").get("id"), doctorId),
                cb.between(root.get("date"), from, to)
        );
    }

    private Specification<Appointment> buildDoctorStatusSpec(LocalDate from, LocalDate to, Long doctorId,
                                                             AppointmentStatus status) {
        return (root, query, cb) -> cb.and(
                cb.equal(root.get("doctor").get("id"), doctorId),
                cb.equal(root.get("status"), status),
                cb.between(root.get("date"), from, to)
        );
    }
}


