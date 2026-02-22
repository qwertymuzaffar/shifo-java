package com.shifo.shifo_java.features.patient;

import com.shifo.shifo_java.features.appointment.Appointment;
import com.shifo.shifo_java.features.patient.enums.PatientGender;
import com.shifo.shifo_java.features.patient.enums.PatientRegistrationStatus;
import com.shifo.shifo_java.features.patient.enums.PatientSource;
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
@Table(name = "patients", indexes = {
        @Index(name = "idx_patients_created_at", columnList = "created_at"),
        @Index(name = "idx_patients_registration_status", columnList = "registration_status"),
        @Index(name = "idx_patients_balance", columnList = "balance"),
        @Index(name = "idx_patients_fullname", columnList = "full_name")
})
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

    @Column(name = "emergency_contact", columnDefinition = "text")
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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PatientStatus status = PatientStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "registration_status", nullable = false)
    private PatientRegistrationStatus registrationStatus = PatientRegistrationStatus.APPROVED;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private PatientSource source = PatientSource.MANUAL;

    @Column(name = "telegram_chat_id")
    private String telegramChatId;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private PatientGender gender;

    @Column(length = 100)
    private String nationality;

    @Column(name = "passport_series", length = 20)
    private String passportSeries;

    @Column(name = "passport_number", length = 50)
    private String passportNumber;

    @Column(name = "place_of_work")
    private String placeOfWork;

    @Column(length = 255)
    private String disability;

    @Column(name = "ambulatory_card_number", length = 50)
    private String ambulatoryCardNumber;

    @Column(name = "parent_full_name")
    private String parentFullName;

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
