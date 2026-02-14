package com.shifo.shifo_java.features.specialization;

import com.shifo.shifo_java.common.exceptions.BadRequestException;
import com.shifo.shifo_java.common.exceptions.NotFoundException;
import com.shifo.shifo_java.features.specialization.dto.SpecializationDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SpecializationsService {

    private final SpecializationRepository specializationRepository;
    private final EntityManager entityManager;

    // -------------------------
    // findAll (aggregation)
    // -------------------------
    @Transactional(readOnly = true)
    public List<SpecializationDto> findAll() {

        String jpql = """
            SELECT new com.shifo.shifo_java.features.specialization.dto.SpecializationDto(
                s.id,
                s.name,
                COUNT(d.id)
            )
            FROM Specialization s
            LEFT JOIN s.doctors d
                ON d.status = 1
            GROUP BY s.id, s.name
            ORDER BY s.id
        """;

        return entityManager
                .createQuery(jpql, SpecializationDto.class)
                .getResultList();
    }

    // -------------------------
    // findOne
    // -------------------------
    public Specialization findOne(Long id) {
        return specializationRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Specialization not found")
                );
    }

    // -------------------------
    // create
    // -------------------------
    public Specialization create(String name) {

        if (name == null || name.trim().isEmpty()) {
            throw new BadRequestException("Название специализации не указано");
        }

        String trimmedName = name.trim();

        boolean exists = specializationRepository.existsByName(trimmedName);
        if (exists) {
            throw new BadRequestException("Такая специализация уже существует");
        }

        Specialization specialization = new Specialization();
        specialization.setName(trimmedName);

        return specializationRepository.save(specialization);
    }

    // -------------------------
    // remove (soft delete)
    // -------------------------
    public void remove(Long id) {
        Specialization specialization = findOne(id);
        specializationRepository.delete(specialization); // soft delete
    }
}

