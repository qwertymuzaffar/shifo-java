package com.shifo.shifo_java.features.patient;

import com.shifo.shifo_java.features.patient.dto.FilterPatientDto;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PatientSpecifications {

    public static Specification<Patient> build(FilterPatientDto filter) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
                String like = "%" + filter.getSearch().toLowerCase() + "%";

                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("fullName")), like),
                        cb.like(cb.lower(root.get("phone")), like),
                        cb.like(cb.lower(root.get("parentFullName")), like)
                ));
            }

            if (filter.getBirthDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("birthDate"), filter.getBirthDateFrom()));
            }

            if (filter.getBirthDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("birthDate"), filter.getBirthDateTo()));
            }

            if (filter.getIsActive() != null) {
                predicates.add(cb.equal(root.get("isActive"), filter.getIsActive()));
            }

            if (filter.getRegistrationStatus() != null) {
                predicates.add(cb.equal(root.get("registrationStatus"), filter.getRegistrationStatus()));
            }

            if (filter.getSource() != null) {
                predicates.add(cb.equal(root.get("source"), filter.getSource()));
            }

            if (filter.getPositiveBalance() != null) {
                if (filter.getPositiveBalance()) {
                    predicates.add(cb.greaterThan(root.get("balance"), BigDecimal.ZERO));
                } else {
                    predicates.add(cb.lessThan(root.get("balance"), BigDecimal.ZERO));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

