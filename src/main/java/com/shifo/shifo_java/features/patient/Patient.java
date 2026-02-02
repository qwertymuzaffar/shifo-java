package com.shifo.shifo_java.features.patient;

import com.shifo.shifo_java.features.appointment.Appointment;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "patients")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", length = 255, nullable = false)
    private String fullName;

    @Column(length = 20, nullable = false)
    @Pattern(regexp = "^(\\+992|992)?\\d{9}$", message = "Номер телефона должен быть валидным номером Таджикистана")
    private String phone;

    @Column(length = 255)
    private String address;

    // Keep as JSON string; DB must support JSON column type (MySQL/MariaDB)
    @Column(name = "emergency_contact", columnDefinition = "json")
    private String emergencyContact;

    // Stored in a separate table patient_allergies
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "patient_allergies", joinColumns = @JoinColumn(name = "patient_id"))
    @Column(name = "allergy")
    private List<String> allergies = new ArrayList<>();

    @Column(columnDefinition = "text")
    private String medicalHistory;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(columnDefinition = "int default 1")
    private Integer status = 1;

    @Column(precision = 12, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments = new ArrayList<>();
}
