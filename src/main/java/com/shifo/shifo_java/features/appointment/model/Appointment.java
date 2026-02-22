package com.shifo.shifo_java.features.appointment.model;

import com.shifo.shifo_java.features.payment.Payment;
import com.shifo.shifo_java.features.procedure.Procedure;
import com.shifo.shifo_java.features.doctor.Doctor;
import com.shifo.shifo_java.features.patient.Patient;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "appointments", indexes = {
        @Index(name = "idx_appointments_date", columnList = "date"),
        @Index(name = "idx_appointments_status", columnList = "status"),
        @Index(name = "idx_appointments_type", columnList = "type"),
        @Index(name = "idx_appointments_date_status", columnList = "date,status"),
        @Index(name = "idx_appointments_date_type", columnList = "date,type"),
        @Index(name = "idx_appointments_complex", columnList = "doctor_id,date,status,type"),
        @Index(name = "idx_appointments_patient_date_time", columnList = "patient_id,date,time")
})
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "time")
    private LocalTime time;

    @Column(name = "date")
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @SQLDelete(sql = "UPDATE appointments SET deleted_at = now() WHERE id=?")
    @Where(clause = "deleted_at IS NULL")
    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(nullable = false)
    private Integer duration;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(columnDefinition = "text")
    private String symptoms;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentType type;

    @ManyToMany
    @JoinTable(
        name = "appointment_procedures",
        joinColumns = @JoinColumn(name = "appointment_id"),
        inverseJoinColumns = @JoinColumn(name = "procedure_id")
    )
    private List<Procedure> procedures = new ArrayList<>();

    @Column(columnDefinition = "text")
    private String cancellationReason;
}
