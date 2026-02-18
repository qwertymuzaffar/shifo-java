package com.shifo.shifo_java.features.doctor;

import com.shifo.shifo_java.common.dto.PagedResponseDto;
import com.shifo.shifo_java.common.enums.AppointmentStatus;
import com.shifo.shifo_java.features.appointment.AppointmentRepository;
import com.shifo.shifo_java.features.doctor.dto.CreateDoctorDto;
import com.shifo.shifo_java.features.doctor.dto.DoctorDto;
import com.shifo.shifo_java.features.doctor.dto.FilterDoctorDto;
import com.shifo.shifo_java.features.doctor.dto.UpdateDoctorDto;
import com.shifo.shifo_java.features.doctor.factory.DoctorFactory;
import com.shifo.shifo_java.features.doctor.loader.DoctorReferenceLoader;
import com.shifo.shifo_java.features.doctor.validation.DoctorValidator;
import com.shifo.shifo_java.features.role.Role;
import com.shifo.shifo_java.features.specialization.Specialization;
import com.shifo.shifo_java.features.user.User;
import com.shifo.shifo_java.features.user.UserMapper;
import com.shifo.shifo_java.features.user.UserRepository;
import com.shifo.shifo_java.features.user.UserService;
import com.shifo.shifo_java.features.user.dto.UpdateUserDto;
import com.shifo.shifo_java.features.user.factory.UserFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    private final DoctorMapper doctorMapper;
    private final UserMapper userMapper;

    private final DoctorValidator validator;
    private final DoctorReferenceLoader referenceLoader;


    private final UserFactory userFactory;
    private final DoctorFactory doctorFactory;
    private final UserService usersService;

    @PersistenceContext
    private EntityManager entityManager;

    public Doctor create(CreateDoctorDto dto) {

        validator.validateForCreation(dto);

        Role role = referenceLoader.loadDoctorRole();
        Specialization specialization =
                referenceLoader.loadSpecialization(dto.getSpecializationId());

        User user = userFactory.createDoctorUser(dto, role);
        userRepository.save(user);

        Doctor doctor = doctorFactory.create(user, specialization, dto);

        return doctorRepository.save(doctor);
    }

    @Transactional(readOnly = true)
    public PagedResponseDto<DoctorDto> findAll(FilterDoctorDto filterDto) {

        int page = filterDto.getPage() != null ? filterDto.getPage() : 1;
        int limit = filterDto.getLimit() != null ? filterDto.getLimit() : 10;
        int offset = (page - 1) * limit;

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // =================================================
        // ITEMS QUERY
        // =================================================
        CriteriaQuery<Doctor> itemsQuery = cb.createQuery(Doctor.class);
        Root<Doctor> root = itemsQuery.from(Doctor.class);

        Specification<Doctor> spec = DoctorSpecification.build(filterDto);
        Predicate predicate = spec.toPredicate(root, itemsQuery, cb);

        if (predicate != null) {
            itemsQuery.where(predicate);
        }

        itemsQuery.orderBy(cb.desc(root.get("createdAt")));

        List<Doctor> items = entityManager.createQuery(itemsQuery)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();

        // =================================================
        // COUNT QUERY (reuse SAME specification)
        // =================================================
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Doctor> countRoot = countQuery.from(Doctor.class);

        Predicate countPredicate = spec.toPredicate(countRoot, countQuery, cb);

        if (countPredicate != null) {
            countQuery.where(countPredicate);
        }

        countQuery.select(cb.countDistinct(countRoot));

        long total = entityManager.createQuery(countQuery).getSingleResult();

        int totalPages = (int) Math.ceil((double) total / limit);

        return PagedResponseDto.<DoctorDto>builder()
                .items(doctorMapper.toDtoList(items))
                .page(page)
                .limit(limit)
                .total((int) total)
                .totalPages(totalPages)
                .build();
    }

    @Transactional(readOnly = true)
    public DoctorDto findOne(Long id) {

        Doctor doctor = doctorRepository.findActiveByIdWithRelations(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "doctors.errors.notFound"
                ));

        return doctorMapper.toDto(doctor);
    }

    @Transactional
    public void remove(Long id) {

        Doctor doctor = doctorRepository.findActiveByIdWithUser(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "doctors.errors.notFound"
                ));

        // Cancel appointments
        appointmentRepository.cancelFutureAppointments(
                id,
                List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.TEMPORARY),
                AppointmentStatus.CANCELLED_FOREVER
        );

        // Soft delete doctor
        doctorRepository.softDeactivate(id);

        // Deactivate linked user
        usersService.remove(doctor.getUser().getId());
    }

    @Transactional
    public DoctorDto update(Long id, UpdateDoctorDto dto) {

        Doctor doctor = doctorRepository.findActiveByIdWithUser(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Doctor not found"
                ));

        // ---------------- USER UPDATE ----------------
        if (userMapper.hasUserChanges(dto)) {

            UpdateUserDto userDto = userMapper.fromDoctorUpdate(dto);

            usersService.update(doctor.getUser().getId(), userDto);
        }

        // ---------------- DOCTOR UPDATE ----------------
        if (dto.getSpecializationId() != null) {
            doctor.setSpecialization(
                    entityManager.getReference(Specialization.class, dto.getSpecializationId())
            );
        }

        if (dto.getExperience() != null) {
            doctor.setExperience(dto.getExperience());
        }

        if (dto.getConsultationFee() != null) {
            doctor.setConsultationFee(dto.getConsultationFee());
        }

        if (dto.getWorkingHours() != null) {
            doctor.setWorkingHours(dto.getWorkingHours());
        }

        return doctorMapper.toDto(doctor);
    }

}
