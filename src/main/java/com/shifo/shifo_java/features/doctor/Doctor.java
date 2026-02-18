package com.shifo.shifo_java.features.doctor;

import com.shifo.shifo_java.features.appointment.Appointment;
import com.shifo.shifo_java.features.doctor.dto.WorkingHoursDto;
import com.shifo.shifo_java.features.specialization.Specialization;
import com.shifo.shifo_java.features.user.User;
import jakarta.persistence.*;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(
        name = "doctors",
        indexes = {
                @Index(name = "idx_doctors_is_active", columnList = "is_active"),
                @Index(name = "idx_doctors_specialization_id", columnList = "specialization_id")
        }
)
@SQLDelete(sql = "UPDATE doctors SET deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private Integer status = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialization_id")
    private Specialization specialization;

    @Column
    private Integer experience;

    @Column
    private Integer consultationFee;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "working_hours")
    private WorkingHoursDto workingHours;

    @OneToMany(mappedBy = "doctor")
    private List<Appointment> appointments = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Transient
    public String getFullName() {
        if (user == null) return "";
        return (user.getFirstName() + " " + user.getLastName()).trim();
    }

    @Transient
    public String getFirstName() {
        return user != null ? user.getFirstName() : "";
    }

    @Transient
    public String getLastName() {
        return user != null ? user.getLastName() : "";
    }
}
