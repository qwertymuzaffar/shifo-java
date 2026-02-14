package com.shifo.shifo_java.features.patient;

import com.shifo.shifo_java.common.dto.PagedResponseDto;
import com.shifo.shifo_java.common.exceptions.ResourceNotFoundException;
import com.shifo.shifo_java.features.patient.dto.CreatePatientDto;
import com.shifo.shifo_java.features.patient.dto.FilterPatientDto;
import com.shifo.shifo_java.features.patient.dto.PatientDto;
import com.shifo.shifo_java.features.patient.dto.UpdatePatientDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
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
    public PatientDto create(CreatePatientDto dto) {
        Patient patient = new Patient();
        patient.setFullName(dto.getFullName());
        patient.setPhone(dto.getPhone());
        patient.setAddress(dto.getAddress());
        patient.setBirthDate(dto.getBirthDate());
        patient.setStatus(PatientStatus.ACTIVE);
        patient.setBalance(BigDecimal.ZERO);

        patientRepository.save(patient);

        return patientMapper.toDto(patient);
    }

    // ---------------------------------------------------------
    // FIND ALL WITH FILTERS
    // ---------------------------------------------------------
    public PagedResponseDto<PatientDto> findAll(FilterPatientDto filterDto) {

        String search = filterDto.getSearch();
        LocalDate birthDateFrom = filterDto.getBirthDateFrom();
        LocalDate birthDateTo = filterDto.getBirthDateTo();

        int page = filterDto.getPage() != null ? filterDto.getPage() : 1;
        int limit = filterDto.getLimit() != null ? filterDto.getLimit() : 10;
        int skip = (page - 1) * limit;

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        /* ---------- ITEMS QUERY ---------- */

        CriteriaQuery<Patient> itemsQuery = cb.createQuery(Patient.class);
        Root<Patient> itemsRoot = itemsQuery.from(Patient.class);

        List<Predicate> itemsPredicates =
                buildPredicates(cb, itemsRoot, search, birthDateFrom, birthDateTo);

        itemsQuery
                .where(itemsPredicates.toArray(new Predicate[0]))
                .orderBy(cb.desc(itemsRoot.get("createdAt")));

        List<Patient> items = entityManager.createQuery(itemsQuery)
                .setFirstResult(skip)
                .setMaxResults(limit)
                .getResultList();

        /* ---------- COUNT QUERY ---------- */

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Patient> countRoot = countQuery.from(Patient.class);

        List<Predicate> countPredicates =
                buildPredicates(cb, countRoot, search, birthDateFrom, birthDateTo);

        Long total = entityManager.createQuery(
                        countQuery
                                .select(cb.count(countRoot))
                                .where(countPredicates.toArray(new Predicate[0]))
                )
                .getSingleResult();

        /* ---------- MAPPING ---------- */

        List<PatientDto> patientDtos = patientMapper.toDtoList(items);

        int totalPages = (int) Math.ceil((double) total / limit);

        return PagedResponseDto.<PatientDto>builder()
                .items(patientDtos)
                .page(page)
                .limit(limit)
                .total(total)
                .totalPages(totalPages)
                .build();
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

        if (dto.getFullName() != null) {
            patient.setFullName(dto.getFullName());
        }
        if (dto.getPhone() != null) {
            patient.setPhone(dto.getPhone());
        }
        if (dto.getAddress() != null) {
            patient.setAddress(dto.getAddress());
        }
        if (dto.getEmergencyContact() != null) {
            patient.setEmergencyContact(dto.getEmergencyContact());
        }
        if (dto.getAllergies() != null) {
            patient.setAllergies(dto.getAllergies());
        }
        if (dto.getMedicalHistory() != null) {
            patient.setMedicalHistory(dto.getMedicalHistory());
        }
        if (dto.getBirthDate() != null) {
            patient.setBirthDate(dto.getBirthDate());
        }

        patientRepository.save(patient);

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

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Пациент не найден: id=" + id
                ));

        patient.setStatus(status);

        patientRepository.save(patient);

        return patientMapper.toDto(patient);
    }
}
