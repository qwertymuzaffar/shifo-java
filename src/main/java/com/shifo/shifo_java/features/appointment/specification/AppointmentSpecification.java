package com.shifo.shifo_java.features.appointment.specification;

import com.shifo.shifo_java.features.appointment.model.Appointment;
import com.shifo.shifo_java.features.appointment.dto.FilterAppointmentDto;
import com.shifo.shifo_java.features.doctor.Doctor;
import com.shifo.shifo_java.features.patient.Patient;
import com.shifo.shifo_java.features.user.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentSpecification {

    public static Specification<Appointment> build(FilterAppointmentDto filter) {

        return (root, query, cb) -> {

            // Avoid fetch joins in count queries (used by pagination).
            if (query != null && query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("doctor", JoinType.LEFT)
                        .fetch("specialization", JoinType.LEFT);
                root.fetch("doctor", JoinType.LEFT)
                        .fetch("user", JoinType.LEFT);
                root.fetch("patient", JoinType.LEFT);
                root.fetch("procedures", JoinType.LEFT);
                query.distinct(true);
            }

            List<Predicate> predicates = new ArrayList<>();

            // doctorIds
            if (filter.getDoctorIds() != null && !filter.getDoctorIds().isEmpty()) {
                predicates.add(root.get("doctor").get("id").in(filter.getDoctorIds()));
            }

            // patientId
            if (filter.getPatientId() != null) {
                predicates.add(cb.equal(root.get("patient").get("id"), filter.getPatientId()));
            }

            // status
            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }

            // search (ILIKE equivalent)
            if (filter.getSearch() != null && !filter.getSearch().isBlank()) {

                String like = "%" + filter.getSearch().toLowerCase() + "%";

                Join<Appointment, Patient> patient = root.join("patient", JoinType.LEFT);
                Join<Appointment, Doctor> doctor = root.join("doctor", JoinType.LEFT);
                Join<Doctor, User> user = doctor.join("user", JoinType.LEFT);

                predicates.add(cb.or(
                        cb.like(cb.lower(patient.get("fullName")), like),
                        cb.like(cb.lower(patient.get("phone")), like),
                        cb.like(cb.lower(user.get("firstName")), like),
                        cb.like(cb.lower(user.get("lastName")), like),
                        cb.like(cb.lower(user.get("email")), like),
                        cb.like(cb.lower(user.get("phone")), like)
                ));
            }

            // upcoming logic
            if (Boolean.TRUE.equals(filter.getUpcoming())) {

                LocalDate today = LocalDate.now();
                LocalTime now = LocalTime.now();

                predicates.add(cb.or(
                        cb.greaterThan(root.get("date"), today),
                        cb.and(
                                cb.equal(root.get("date"), today),
                                cb.greaterThanOrEqualTo(root.get("time"), now)
                        )
                ));

                query.orderBy(
                        cb.asc(root.get("date")),
                        cb.asc(root.get("time"))
                );

            } else {

                if (filter.getDateFrom() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("date"), filter.getDateFrom()));
                }

                if (filter.getDateTo() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("date"), filter.getDateTo()));
                }

                query.orderBy(
                        cb.desc(root.get("date")),
                        cb.desc(root.get("time"))
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
