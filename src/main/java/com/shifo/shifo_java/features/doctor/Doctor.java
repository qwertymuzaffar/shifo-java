package com.shifo.shifo_java.features.doctor;

import com.shifo.shifo_java.features.appointment.Appointment;
import com.shifo.shifo_java.features.specialization.Specialization;
import com.shifo.shifo_java.features.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // user_id column + OneToOne relationship
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private Integer status = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialization_id")
    private Specialization specialization;

    @Column
    private Integer experience; // years

    @Column
    private Integer consultationFee;

    // JSON field → stored as TEXT or JSONB depending on DB
    @Column(columnDefinition = "TEXT")
    private String workingHours;
    // store JSON string such as:
    // {"start": "09:00", "end": "17:00", "workingDays": [1,2,3,4,5]}

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Transient
    public String getFullName() {
        if (user == null) return "";
        return (user.getFirstName() + " " + user.getLastName()).trim();
    }

    @Transient
    public String getFirstName() {
        return (user != null) ? user.getFirstName() : "";
    }

    @Transient
    public String getLastName() {
        return (user != null) ? user.getLastName() : "";
    }
}
