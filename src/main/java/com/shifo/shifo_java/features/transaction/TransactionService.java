package com.shifo.shifo_java.features.transaction;

import com.shifo.shifo_java.common.enums.RelatedEntityType;
import com.shifo.shifo_java.features.doctor.Doctor;
import com.shifo.shifo_java.features.doctor.DoctorRepository;
import com.shifo.shifo_java.features.patient.Patient;
import com.shifo.shifo_java.features.patient.PatientRepository;
import com.shifo.shifo_java.features.transaction.dto.CreateTransactionDto;
import com.shifo.shifo_java.features.transaction.dto.FilterTransactionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    // ---------------------------
    // CREATE
    // ---------------------------
    @Transactional
    public Transaction create(CreateTransactionDto dto) {
        Transaction transaction = new Transaction();

        transaction.setType(dto.getType());
        transaction.setPaymentMethod(dto.getPaymentMethod());
        transaction.setAmount(dto.getAmount());
        transaction.setComment(dto.getComment());
        transaction.setRelatedEntityId(dto.getRelatedEntityId());
        transaction.setRelatedEntityType(dto.getRelatedEntityType());

        return transactionRepository.save(transaction);
    }

    // ---------------------------
    // FIND ALL WITH FILTERS + PAGINATION
    // ---------------------------
    public Map<String, Object> findAll(FilterTransactionDto filter) {

        int page = Optional.ofNullable(filter.getPage()).orElse(1);
        int limit = Optional.ofNullable(filter.getLimit()).orElse(10);

        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Transaction> paged = transactionRepository.findAll(
                TransactionSpecifications.buildFilters(filter),
                pageable
        );

        List<Transaction> withRelations = loadRelatedData(paged.getContent());

        Map<String, Object> result = new HashMap<>();
        result.put("data", withRelations);
        result.put("total", paged.getTotalElements());
        result.put("page", page);
        result.put("limit", limit);
        result.put("totalPages", paged.getTotalPages());

        return result;
    }

    // ---------------------------
    // FIND ONE
    // ---------------------------
    public Transaction findOne(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Транзакция не найдена"));

        return loadRelatedData(List.of(transaction)).get(0);
    }

    // ---------------------------
    // LOAD RELATED DATA (doctor/patient)
    // ---------------------------
    private List<Transaction> loadRelatedData(List<Transaction> transactions) {

        // collect doctor IDs
        List<Long> doctorIds = transactions.stream()
                .filter(t -> t.getRelatedEntityType() == RelatedEntityType.DOCTOR)
                .map(Transaction::getRelatedEntityId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // collect patient IDs
        List<Long> patientIds = transactions.stream()
                .filter(t -> t.getRelatedEntityType() == RelatedEntityType.PATIENT)
                .map(Transaction::getRelatedEntityId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Map<Long, Doctor> doctorMap = doctorIds.isEmpty()
                ? Map.of()
                : doctorRepository.findAllById(doctorIds).stream()
                .collect(Collectors.toMap(Doctor::getId, d -> d));

        Map<Long, Patient> patientMap = patientIds.isEmpty()
                ? Map.of()
                : patientRepository.findAllById(patientIds).stream()
                .collect(Collectors.toMap(Patient::getId, p -> p));

        // assign relations
        transactions.forEach(t -> {
            if (t.getRelatedEntityType() == RelatedEntityType.DOCTOR && t.getRelatedEntityId() != null) {
                t.setDoctor(doctorMap.get(t.getRelatedEntityId()));
            }
            if (t.getRelatedEntityType() == RelatedEntityType.PATIENT && t.getRelatedEntityId() != null) {
                t.setPatient(patientMap.get(t.getRelatedEntityId()));
            }
        });

        return transactions;
    }

    // ---------------------------
    // SUMMARY: income / expense / balance
    // ---------------------------
    public Map<String, Object> getSummary(FilterTransactionDto filterDto) {

        BigDecimal totalIncome =
                transactionRepository.sumByType(TransactionType.INCOME, filterDto.getDateFrom(), filterDto.getDateTo())
                        .orElse(BigDecimal.ZERO);

        BigDecimal totalExpense =
                transactionRepository.sumByType(TransactionType.EXPENSE, filterDto.getDateFrom(), filterDto.getDateTo())
                        .orElse(BigDecimal.ZERO);

        BigDecimal balance = totalIncome.subtract(totalExpense);

        long count = transactionRepository.count(TransactionSpecifications.buildFilters(filterDto));

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalIncome", totalIncome);
        summary.put("totalExpense", totalExpense);
        summary.put("balance", balance);
        summary.put("transactionCount", count);

        return summary;
    }
}

