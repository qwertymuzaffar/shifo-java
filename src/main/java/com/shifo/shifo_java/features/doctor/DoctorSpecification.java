package com.shifo.shifo_java.features.doctor;

import com.shifo.shifo_java.features.doctor.dto.FilterDoctorDto;
import com.shifo.shifo_java.features.specialization.Specialization;
import com.shifo.shifo_java.features.user.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class DoctorSpecification {

    private DoctorSpecification() {}

    public static Specification<Doctor> build(FilterDoctorDto filter) {

        return (root, query, cb) -> {

            // Important when using joins
            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();

            // Default filter (same as NestJS: doctor.status = 1)
            predicates.add(cb.equal(root.get("status"), 1));

            Join<Doctor, User> userJoin = root.join("user", JoinType.LEFT);
            Join<Doctor, Specialization> specializationJoin =
                    root.join("specialization", JoinType.LEFT);

            // Search
            if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
                String like = "%" + filter.getSearch().toLowerCase() + "%";

                predicates.add(cb.or(
                        cb.like(cb.lower(userJoin.get("firstName")), like),
                        cb.like(cb.lower(userJoin.get("lastName")), like),
                        cb.like(cb.lower(userJoin.get("phone")), like),
                        cb.like(
                                cb.lower(cb.concat(
                                        cb.concat(userJoin.get("firstName"), " "),
                                        cb.coalesce(userJoin.get("lastName"), "")
                                )),
                                like
                        )
                ));
            }

            if (filter.getIsActive() != null) {
                predicates.add(cb.equal(root.get("isActive"), filter.getIsActive()));
            }

            if (filter.getSpecializationId() != null && filter.getSpecializationId() > 0) {
                predicates.add(cb.equal(
                        specializationJoin.get("id"),
                        filter.getSpecializationId()
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

