package com.shifo.shifo_java.features.patient;

import com.shifo.shifo_java.common.dto.PagedResponseDto;
import com.shifo.shifo_java.common.enums.SortOrder;
import com.shifo.shifo_java.common.exceptions.ResourceNotFoundException;
import com.shifo.shifo_java.features.patient.dto.CreatePatientDto;
import com.shifo.shifo_java.features.patient.dto.FilterPatientDto;
import com.shifo.shifo_java.features.patient.dto.PatientDto;
import com.shifo.shifo_java.features.patient.dto.UpdatePatientDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientsService {

    private final PatientRepository patientRepository;
    private final EntityManager entityManager;
    private final PatientMapper patientMapper;

    // ---------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------
    @Transactional
    public Patient create(CreatePatientDto dto) {
        Patient patient = patientMapper.toEntity(dto);
        return patientRepository.save(patient);
    }

    // ---------------------------------------------------------
    // FIND ALL WITH FILTERS
    // ---------------------------------------------------------
    public PagedResponseDto<PatientDto> findAll(FilterPatientDto filter) {

        int page = filter.getPage() != null ? filter.getPage() : 1;
        int limit = filter.getLimit() != null ? filter.getLimit() : 10;
        int offset = (page - 1) * limit;

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        /* ---------- ITEMS QUERY ---------- */

        CriteriaQuery<Patient> itemsQuery = cb.createQuery(Patient.class);
        Root<Patient> root = itemsQuery.from(Patient.class);

        Predicate predicate = PatientSpecifications.build(filter)
                .toPredicate(root, itemsQuery, cb);

        itemsQuery.where(predicate);

        applySorting(filter, cb, itemsQuery, root);

        List<Patient> items = entityManager.createQuery(itemsQuery)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();

        /* ---------- COUNT QUERY ---------- */

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Patient> countRoot = countQuery.from(Patient.class);

        Predicate countPredicate = PatientSpecifications.build(filter)
                .toPredicate(countRoot, countQuery, cb);

        Long total = entityManager.createQuery(
                countQuery.select(cb.count(countRoot)).where(countPredicate)
        ).getSingleResult();

        /* ---------- RESPONSE ---------- */

        List<PatientDto> dtos = patientMapper.toDtoList(items);
        int totalPages = (int) Math.ceil((double) total / limit);

        return PagedResponseDto.<PatientDto>builder()
                .items(dtos)
                .page(page)
                .limit(limit)
                .total(total)
                .totalPages(totalPages)
                .build();
    }

    private void applySorting(
            FilterPatientDto filter,
            CriteriaBuilder cb,
            CriteriaQuery<?> query,
            Root<Patient> root
    ) {

        if (filter.getSort() == null) {
            query.orderBy(cb.desc(root.get("createdAt")));
            return;
        }

        String field = switch (filter.getSort()) {
            case BALANCE -> "balance";
            case FULLNAME -> "lastName";
            case LAST_VISIT_DATE -> "lastVisitDate";
        };

        boolean asc = filter.getOrder() == SortOrder.ASC;

        query.orderBy(asc ? cb.asc(root.get(field)) : cb.desc(root.get(field)));
    }

    private List<Predicate> buildPredicates(
            CriteriaBuilder cb,
            Root<Patient> root,
            String search,
            LocalDate birthDateFrom,
            LocalDate birthDateTo
    ) {
        List<Predicate> predicates = new ArrayList<>();

        // Default filter: only active patients
        predicates.add(cb.equal(root.get("status"), PatientStatus.ACTIVE));

        if (StringUtils.hasText(search)) {
            String pattern = "%" + search.toLowerCase() + "%";

            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("fullName")), pattern),
                    cb.like(cb.lower(root.get("phone")), pattern)
            ));
        }

        if (birthDateFrom != null && birthDateTo != null) {
            predicates.add(cb.between(root.get("birthDate"), birthDateFrom, birthDateTo));
        } else if (birthDateFrom != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("birthDate"), birthDateFrom));
        } else if (birthDateTo != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("birthDate"), birthDateTo));
        }

        return predicates;
    }

    public PatientDto findOne(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Пациент не найден: id=" + id
                ));

        return patientMapper.toDto(patient);
    }

    @Transactional
    public PatientDto update(Long id, UpdatePatientDto dto) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Пациент не найден: id=" + id
                ));

        patientMapper.updateEntity(dto, patient);

        return patientMapper.toDto(patient);
    }

    @Transactional
    public void deactivate(Long id) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Пациент не найден: id=" + id
                ));

        patient.setStatus(PatientStatus.INACTIVE);

        patientRepository.save(patient);
    }

    @Transactional
    public PatientDto updateStatus(Long id, PatientStatus status) {

        if (status == null) {
            throw new IllegalArgumentException("Status required");
        }

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Пациент не найден: id=" + id
                ));

        patient.setStatus(status);

        patientRepository.save(patient);

        return patientMapper.toDto(patient);
    }
}
