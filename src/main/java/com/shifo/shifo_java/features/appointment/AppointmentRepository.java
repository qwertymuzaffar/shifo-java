package com.shifo.shifo_java.features.appointment;

import com.shifo.shifo_java.features.appointment.model.Appointment;
import com.shifo.shifo_java.features.appointment.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {
    @Modifying
    @Query("""
                UPDATE Appointment a
                SET a.status = :cancelledStatus
                WHERE a.doctor.id = :doctorId
                  AND a.status IN :statuses
            """)
    int cancelFutureAppointments(
            @Param("doctorId") Long doctorId,
            @Param("statuses") List<AppointmentStatus> statuses,
            @Param("cancelledStatus") AppointmentStatus cancelledStatus
    );

    @Query(value = """
                SELECT EXISTS (
                    SELECT 1
                    FROM appointments a
                    WHERE a.deleted_at IS NULL
                      AND a.doctor_id = :doctorId
                      AND a.date = :date
                      AND (
                            a.time < :newEndTime
                        AND ADDTIME(a.time, SEC_TO_TIME(a.duration * 60)) > :newStartTime
                      )
                )
            """, nativeQuery = true)
    Integer existsDoctorOverlap(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date,
            @Param("newStartTime") LocalTime newStartTime,
            @Param("newEndTime") LocalTime newEndTime
    );

    @Query(value = """
                SELECT EXISTS (
                    SELECT 1
                    FROM appointments a
                    WHERE a.deleted_at IS NULL
                      AND a.patient_id = :patientId
                      AND a.date = :date
                      AND (
                            a.time < :newEndTime
                        AND ADDTIME(a.time, SEC_TO_TIME(a.duration * 60)) > :newStartTime
                      )
                )
            """, nativeQuery = true)
    Integer existsPatientOverlap(
            @Param("patientId") Long patientId,
            @Param("date") LocalDate date,
            @Param("newStartTime") LocalTime newStartTime,
            @Param("newEndTime") LocalTime newEndTime
    );

}
