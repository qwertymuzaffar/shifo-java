package com.shifo.shifo_java.features.appointment;

import com.shifo.shifo_java.common.enums.AppointmentStatus;
import com.shifo.shifo_java.common.enums.AppointmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("""
            SELECT a FROM Appointment a
            LEFT JOIN FETCH a.doctor d
            LEFT JOIN FETCH d.specialization s
            LEFT JOIN FETCH d.user u
            LEFT JOIN FETCH a.patient p
            LEFT JOIN FETCH a.procedures pr
            WHERE (:doctorId IS NULL OR d.id = :doctorId)
              AND (:patientId IS NULL OR p.id = :patientId)
              AND (:status IS NULL OR a.status = :status)
              AND (:dateFrom IS NULL OR a.date >= :dateFrom)
              AND (:dateTo IS NULL OR a.date <= :dateTo)
              AND (:search IS NULL OR
                   LOWER(p.fullName) LIKE LOWER(:search) OR
                   LOWER(CONCAT(u.firstName, ' ', COALESCE(u.lastName, '')))
                       LIKE LOWER(:search))
            ORDER BY a.date DESC
            """)
    List<Appointment> findAllWithFilters(
            @Param("doctorId") Long doctorId,
            @Param("patientId") Long patientId,
            @Param("status") AppointmentStatus status,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            @Param("search") String search
    );

    @Query("""
            SELECT a FROM Appointment a
            LEFT JOIN FETCH a.doctor d
            LEFT JOIN FETCH d.specialization s
            LEFT JOIN FETCH d.user u
            LEFT JOIN FETCH a.patient p
            WHERE a.id = :id
            """)
    Optional<Appointment> findByIdWithDoctorAndPatient(@Param("id") Long id);

    @Query("""
            SELECT a FROM Appointment a
            LEFT JOIN FETCH a.doctor d
            LEFT JOIN FETCH d.user u
            LEFT JOIN FETCH a.patient p
            LEFT JOIN FETCH a.procedures pr
            WHERE a.id = :id
            """)
    Optional<Appointment> findByIdWithProcedures(@Param("id") Long id);

    List<Appointment> findByDoctorIdAndDateAndStatus(
            Long doctorId, LocalDate date, AppointmentStatus status);

    List<Appointment> findByPatientIdAndDateAndStatus(
            Long patientId, LocalDate date, AppointmentStatus status);

    @Query("""
            SELECT a FROM Appointment a
            LEFT JOIN FETCH a.doctor d
            LEFT JOIN FETCH d.user u
            LEFT JOIN FETCH a.patient p
            LEFT JOIN FETCH a.procedures pr
            WHERE a.date = :date
            """)
    List<Appointment> findAllByDateWithRelations(@Param("date") LocalDate date);

    @Query("""
        SELECT COUNT(a)
        FROM Appointment a
        WHERE a.date BETWEEN :dateFrom AND :dateTo
          AND (:doctorIds IS NULL OR a.doctor.id IN :doctorIds)
          AND (:types IS NULL OR a.type IN :types)
          AND (:status IS NULL OR a.status = :status)
        """)
    long countByDateRangeAndFilters(
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            @Param("doctorIds") List<Long> doctorIds,
            @Param("types") List<AppointmentType> types,
            @Param("status") AppointmentStatus status
    );
}

