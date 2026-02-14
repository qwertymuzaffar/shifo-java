package com.shifo.shifo_java.features.doctor;

import com.shifo.shifo_java.common.dto.PagedResponseDto;
import com.shifo.shifo_java.common.exceptions.BadRequestException;
import com.shifo.shifo_java.common.exceptions.NotFoundException;
import com.shifo.shifo_java.features.doctor.dto.CreateDoctorDto;
import com.shifo.shifo_java.features.doctor.dto.DoctorDto;
import com.shifo.shifo_java.features.doctor.dto.FilterDoctorDto;
import com.shifo.shifo_java.features.role.Role;
import com.shifo.shifo_java.features.role.RoleRepository;
import com.shifo.shifo_java.features.specialization.Specialization;
import com.shifo.shifo_java.features.specialization.SpecializationRepository;
import com.shifo.shifo_java.features.user.User;
import com.shifo.shifo_java.features.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final SpecializationRepository specializationRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final MessageSource messageSource;
    private final ObjectMapper objectMapper;
    private final DoctorMapper doctorMapper;

    @PersistenceContext
    private EntityManager entityManager;

    public Doctor create(CreateDoctorDto dto) {

        // ----------------------------
        // 1. Email uniqueness check
        // ----------------------------
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException(
                    translate("auth.errors.emailAlreadyInUse")
            );
        }

        // ----------------------------
        // 2. Username uniqueness check
        // ----------------------------
        if (dto.getUsername() != null &&
                userRepository.existsByUsername(dto.getUsername())) {

            throw new BadRequestException(
                    translate("auth.errors.usernameAlreadyInUse")
            );
        }

        // ----------------------------
        // 3. Fetch doctor role
        // ----------------------------
        Role doctorRole = roleRepository.findBySlug("doctor")
                .orElseThrow(() ->
                        new NotFoundException(
                                translate("users.errors.invalidRole")
                        )
                );

        // ----------------------------
        // 4. Create User
        // ----------------------------
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPassword(dto.getPassword()); // hashing assumed elsewhere
        user.setRole(doctorRole);

        userRepository.save(user);

        // ----------------------------
        // 5. Create Doctor
        // ----------------------------
        Doctor doctor = new Doctor();
        doctor.setUser(user);

        Long specializationId;
        try {
            specializationId = Long.parseLong(dto.getSpecialization());
        } catch (NumberFormatException ex) {
            throw new BadRequestException("Invalid specialization id");
        }

        Specialization specializationEntity = specializationRepository
                .findById(specializationId)
                .orElseThrow(() -> new NotFoundException("Specialization not found"));

        if (specializationEntity.getId() != null) {
            doctor.setSpecialization(specializationEntity);
        }

        doctor.setWorkingHours(dto.getWorkingHours());

        doctor.setExperience(dto.getExperience());
        doctor.setConsultationFee(dto.getConsultationFee());

        // ----------------------------
        // 6. Save Doctor
        // ----------------------------
        return doctorRepository.save(doctor);
    }

    // --------------------------------
    // i18n helper (Nest i18n equivalent)
    // --------------------------------
    private String translate(String key) {
        return messageSource.getMessage(
                key,
                null,
                LocaleContextHolder.getLocale()
        );
    }

    @Transactional(readOnly = true)
    public PagedResponseDto<DoctorDto> findAll(FilterDoctorDto filterDto) {

        int page = filterDto.getPage() != null ? filterDto.getPage() : 1;
        int limit = filterDto.getLimit() != null ? filterDto.getLimit() : 10;
        int offset = (page - 1) * limit;

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // =================================================
        // Main query (items)
        // =================================================
        CriteriaQuery<Doctor> cq = cb.createQuery(Doctor.class);
        Root<Doctor> doctor = cq.from(Doctor.class);

        Join<Doctor, User> userJoin =
                doctor.join("user", JoinType.LEFT);

        Join<Doctor, Specialization> specializationJoin =
                doctor.join("specialization", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        // Default filter: only active status = 1
        predicates.add(cb.equal(doctor.get("status"), 1));

        // Search (ILIKE equivalent)
        if (filterDto.getSearch() != null && !filterDto.getSearch().isBlank()) {
            String search = "%" + filterDto.getSearch().toLowerCase() + "%";

            predicates.add(
                    cb.or(
                            cb.like(cb.lower(userJoin.get("firstName")), search),
                            cb.like(cb.lower(userJoin.get("lastName")), search),
                            cb.like(cb.lower(userJoin.get("phone")), search),
                            cb.like(
                                    cb.lower(
                                            cb.concat(
                                                    cb.concat(userJoin.get("firstName"), " "),
                                                    cb.coalesce(userJoin.get("lastName"), "")
                                            )
                                    ),
                                    search
                            )
                    )
            );
        }

        // isActive filter
        if (filterDto.getIsActive() != null) {
            predicates.add(cb.equal(doctor.get("isActive"), filterDto.getIsActive()));
        }

        // specialization filter (frontend may send 0)
        if (filterDto.getSpecialization() != null && filterDto.getSpecialization() > 0) {
            predicates.add(
                    cb.equal(specializationJoin.get("id"), filterDto.getSpecialization())
            );
        }

        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.desc(doctor.get("createdAt")));

        TypedQuery<Doctor> query = entityManager.createQuery(cq);
        query.setFirstResult(offset);
        query.setMaxResults(limit);

        List<Doctor> items = query.getResultList();

        // =================================================
        // Count query
        // =================================================
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Doctor> countRoot = countQuery.from(Doctor.class);

        Join<Doctor, User> countUserJoin =
                countRoot.join("user", JoinType.LEFT);

        Join<Doctor, Specialization> countSpecJoin =
                countRoot.join("specialization", JoinType.LEFT);

        List<Predicate> countPredicates = new ArrayList<>();

        countPredicates.add(cb.equal(countRoot.get("status"), 1));

        if (filterDto.getSearch() != null && !filterDto.getSearch().isBlank()) {
            String search = "%" + filterDto.getSearch().toLowerCase() + "%";

            countPredicates.add(
                    cb.or(
                            cb.like(cb.lower(countUserJoin.get("firstName")), search),
                            cb.like(cb.lower(countUserJoin.get("lastName")), search),
                            cb.like(cb.lower(countUserJoin.get("phone")), search),
                            cb.like(
                                    cb.lower(
                                            cb.concat(
                                                    cb.concat(countUserJoin.get("firstName"), " "),
                                                    cb.coalesce(countUserJoin.get("lastName"), "")
                                            )
                                    ),
                                    search
                            )
                    )
            );
        }

        if (filterDto.getIsActive() != null) {
            countPredicates.add(cb.equal(countRoot.get("isActive"), filterDto.getIsActive()));
        }

        if (filterDto.getSpecialization() != null && filterDto.getSpecialization() > 0) {
            countPredicates.add(
                    cb.equal(countSpecJoin.get("id"), filterDto.getSpecialization())
            );
        }

        countQuery
                .select(cb.countDistinct(countRoot))
                .where(countPredicates.toArray(new Predicate[0]));

        int total = Math.toIntExact(
                entityManager.createQuery(countQuery).getSingleResult()
        );

        int totalPages = (int) Math.ceil((double) total / limit);

        List<DoctorDto> doctorDtos = doctorMapper.toDtoList(items);

        return PagedResponseDto.<DoctorDto>builder()
                .items(doctorDtos)
                .page(page)
                .limit(limit)
                .total(total)
                .totalPages(totalPages)
                .build();
    }
}
