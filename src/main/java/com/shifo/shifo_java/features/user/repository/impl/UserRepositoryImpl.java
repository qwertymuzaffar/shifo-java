package com.shifo.shifo_java.features.user.repository.impl;

import com.shifo.shifo_java.common.dto.PagedResponseDto;
import com.shifo.shifo_java.common.dto.PaginationDto;
import com.shifo.shifo_java.features.user.User;
import com.shifo.shifo_java.features.user.dto.FilterUserDto;
import com.shifo.shifo_java.features.user.repository.UserRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final EntityManager em;

    @Override
    public PagedResponseDto<User> findAllWithFilter(FilterUserDto filter) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        // ===== Data Query =====
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);

        List<Predicate> predicates = buildPredicates(filter, cb, root);
        cq.where(predicates.toArray(new Predicate[0]));

        TypedQuery<User> query = em.createQuery(cq);

        int offset = (filter.getPage() - 1) * filter.getLimit();
        query.setFirstResult(offset);
        query.setMaxResults(filter.getLimit());

        List<User> result = query.getResultList();

        // ===== Count Query =====
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<User> countRoot = countQuery.from(User.class);

        countQuery.select(cb.count(countRoot));
        countQuery.where(buildPredicates(filter, cb, countRoot).toArray(new Predicate[0]));

        Long total = em.createQuery(countQuery).getSingleResult();

        // ===== Wrap Result =====
        PagedResponseDto<User> dto = new PagedResponseDto<>();
        dto.setPage(filter.getPage());
        dto.setLimit(filter.getLimit());
        dto.setTotal(total);
        dto.setItems(result);

        return dto;
    }

    private List<Predicate> buildPredicates(FilterUserDto filter, CriteriaBuilder cb, Root<User> root) {

        List<Predicate> predicates = new ArrayList<>();

        if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
            predicates.add(cb.like(root.get("username"), "%" + filter.getSearch() + "%"));
        }

        if (filter.getEmail() != null && !filter.getEmail().isBlank()) {
            predicates.add(cb.like(root.get("email"), "%" + filter.getEmail() + "%"));
        }

        if (filter.getRoleId() != null) {
            predicates.add(cb.equal(root.get("roleId"), filter.getRoleId()));
        }

        if (filter.getIsActive() != null) {
            predicates.add(cb.equal(root.get("isActive"), filter.getIsActive()));
        }

        return predicates;
    }
}

