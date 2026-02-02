package com.shifo.shifo_java.features.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, unique = true, nullable = false)
    private String username;

    @Column(length = 100, unique = true, nullable = false)
    private String email;

    @Column(length = 100)
    private String phone;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(length = 100)
    private String firstName;

    @Column(length = 100)
    private String lastName;

    // Computed field (like @Expose)
    @Transient
    public String getFullName() {
        return ((firstName == null ? "" : firstName) + " " +
                (lastName == null ? "" : lastName)).trim();
    }

    @Column(name = "role_id")
    private Long roleId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    private Role role;

    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    public void hashPassword(String rawPassword, org.springframework.security.crypto.password.PasswordEncoder encoder) {
        this.password = encoder.encode(rawPassword);
    }

    public boolean validatePassword(String rawPassword, org.springframework.security.crypto.password.PasswordEncoder encoder) {
        return encoder.matches(rawPassword, this.password);
    }
}

