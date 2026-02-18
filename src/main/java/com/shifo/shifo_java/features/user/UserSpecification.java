package com.shifo.shifo_java.features.user;

import com.shifo.shifo_java.features.user.dto.FilterUserDto;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> filter(FilterUserDto filter) {
        return (root, query, cb) -> {

            var predicates = cb.conjunction();

            if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
                predicates = cb.and(predicates,
                        cb.like(root.get("username"), "%" + filter.getSearch() + "%"));
            }

            if (filter.getEmail() != null && !filter.getEmail().isBlank()) {
                predicates = cb.and(predicates,
                        cb.like(root.get("email"), "%" + filter.getEmail() + "%"));
            }

            if (filter.getIsActive() != null) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("isActive"), filter.getIsActive()));
            }

            if (filter.getRoleId() != null) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("roleId"), filter.getRoleId()));
            }

            return predicates;
        };
    }
}

