package com.shifo.shifo_java.features.appointment;

import com.shifo.shifo_java.common.dto.DateTimeDto;
import com.shifo.shifo_java.common.enums.AppointmentStatus;
import com.shifo.shifo_java.common.enums.AppointmentType;
import com.shifo.shifo_java.features.appointment.dto.*;
import com.shifo.shifo_java.features.doctor.Doctor;
import com.shifo.shifo_java.features.doctor.DoctorRepository;
import com.shifo.shifo_java.features.patient.Patient;
import com.shifo.shifo_java.features.patient.PatientRepository;
import com.shifo.shifo_java.features.payment.Payment;
import com.shifo.shifo_java.features.payment.PaymentRepository;
import com.shifo.shifo_java.features.payment.dto.PaymentMethodDto;
import com.shifo.shifo_java.features.procedure.Procedure;
import com.shifo.shifo_java.features.procedure.ProcedureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final ProcedureRepository procedureRepository;
    private final PaymentRepository paymentRepository;

    // ----------------------------------------------------
    // CREATE APPOINTMENTS (multiple datetimes)
    // ----------------------------------------------------
    @Transactional
    public Map<String, Object> create(CreateAppointmentDto dto) {

        List<Appointment> successful = new ArrayList<>();
        List<Map<String, Object>> failed = new ArrayList<>();

        Long doctorId = dto.getDoctorId();
        Long patientId = dto.getPatientId() != null ? dto.getPatientId().longValue() : null;
        Integer duration = dto.getDuration();
        String symptoms = dto.getSymptoms();
        AppointmentType type = dto.getType() != null ? dto.getType() : AppointmentType.CONSULTATION;
        String notes = dto.getNotes();
        List<Long> procedureIds = dto.getProcedureIds();

        Patient patient;
        if (patientId == null) {
            patient = patientRepository.findByFullName("Новый клиент")
                    .orElseGet(() -> {
                        Patient p = new Patient();
                        p.setFullName("Новый клиент");
                        p.setPhone("000000000");
                        p.setBirthDate(LocalDate.now());
                        return patientRepository.save(p);
                    });
        } else {
            patient = validatePatient(patientId);
        }

        Doctor doctor = validateDoctor(doctorId);

        List<Procedure> procedures = Collections.emptyList();
        if (procedureIds != null && !procedureIds.isEmpty()) {
            procedures = procedureRepository.findAllById(procedureIds);
        }

        for (DateTimeDto dt : dto.getDatetimes()) {
            String dateStr = dt.getDate();
            String timeStr = dt.getTime();
            try {
                LocalDate date = LocalDate.parse(dateStr);
                LocalTime time = LocalTime.parse(timeStr);

                // check availability for doctor
                checkAvailability(doctor.getId(), date, time, duration, "doctor", true);
                // check availability for patient (only if patient has ID)
                if (patientId != null) {
                    checkAvailability(patient.getId(), date, time, duration, "patient", true);
                }

                Appointment appointment = new Appointment();
                appointment.setDate(date);
                appointment.setTime(time);
                appointment.setDuration(duration);
                appointment.setSymptoms(symptoms);
                appointment.setType(type);
                appointment.setNotes(notes);
                appointment.setDoctor(doctor);
                appointment.setPatient(patient);
                appointment.setStatus(AppointmentStatus.SCHEDULED);
                appointment.setProcedures(new ArrayList<>(procedures));

                Appointment saved = appointmentRepository.save(appointment);
                successful.add(saved);
            } catch (Exception ex) {
                Map<String, Object> failItem = new HashMap<>();
                failItem.put("date", dateStr);
                failItem.put("time", timeStr);
                failItem.put("reason", ex.getMessage() != null ? ex.getMessage() : "Unknown error");
                failed.add(failItem);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("successful", successful);
        result.put("failed", failed);
        return result;
    }

    // ----------------------------------------------------
    // FIND ALL with filters (no pagination here)
    // ----------------------------------------------------
    @Transactional(readOnly = true)
    public List<Appointment> findAll(FilterAppointmentDto filter) {
        Long doctorId = filter.getDoctorId() != null ? filter.getDoctorId().longValue() : null;
        Long patientId = filter.getPatientId() != null ? filter.getPatientId().longValue() : null;
        String dateFromStr = filter.getDateFrom();
        String dateToStr = filter.getDateTo();
        AppointmentStatus status = filter.getStatus();
        String search = filter.getSearch();

        LocalDate dateFrom = null;
        LocalDate dateTo = null;
        try {
            if (dateFromStr != null) dateFrom = LocalDate.parse(dateFromStr);
            if (dateToStr != null) dateTo = LocalDate.parse(dateToStr);
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неверный формат даты");
        }

        if (dateFrom != null && dateTo != null && dateFrom.isAfter(dateTo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неверные даты");
        }

        // Delegating filtering to custom query in repository
        return appointmentRepository.findAllWithFilters(
                doctorId,
                patientId,
                status,
                dateFrom,
                dateTo,
                (search != null && !search.isBlank()) ? "%" + search.trim() + "%" : null
        );
    }

    // ----------------------------------------------------
    // FIND ONE
    // ----------------------------------------------------
    @Transactional(readOnly = true)
    public Appointment findOne(Long id) {
        return appointmentRepository.findByIdWithDoctorAndPatient(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Приём с ID " + id + " не найден"
                ));
    }

    // ----------------------------------------------------
    // CANCEL
    // ----------------------------------------------------
    @Transactional
    public Appointment cancel(Long id, String reason) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Приём с ID " + id + " не найден"
                ));

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            return appointment;
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(reason);
        return appointmentRepository.save(appointment);
    }

    // ----------------------------------------------------
    // COMPLETE (with payment)
    // ----------------------------------------------------
    @Transactional
    public Map<String, Object> complete(Long id, PaymentMethodDto paymentMethod) {
        Appointment appointment = findOne(id);

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appointment already completed");
        }

        Payment payment = new Payment();
        payment.setAppointment(appointment);
        payment.setAmount(paymentMethod.getAmount());
        payment.setPaymentType(paymentMethod.getPaymentType());
        payment.setPaymentKind(
                paymentMethod.getPaymentKind() != null
                        ? paymentMethod.getPaymentKind()
                        : com.shifo.shifo_java.common.enums.PaymentKind.PAYMENT
        );
        payment.setPaidAt(java.time.Instant.now());
        payment.setStatus(com.shifo.shifo_java.common.enums.PaymentStatus.PAID);

        paymentRepository.save(payment);

        appointment.setStatus(AppointmentStatus.COMPLETED);
        Appointment updatedAppointment = appointmentRepository.save(appointment);

        Map<String, Object> response = new HashMap<>();
        response.put("appointment", updatedAppointment);
        response.put("payment", payment);
        return response;
    }

    // ----------------------------------------------------
    // UPDATE
    // ----------------------------------------------------
    @Transactional
    public Appointment update(Long id, UpdateAppointmentDto dto) {
        Appointment appointment = findOne(id);

        if (appointment.getStatus() == AppointmentStatus.CANCELLED
                || appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Нельзя изменить статус завершенного или отмененного приёма"
            );
        }

        if (dto.getDoctorId() != null
                && !Objects.equals(appointment.getDoctor().getId(), dto.getDoctorId().longValue())) {
            appointment.setDoctor(validateDoctor(dto.getDoctorId().longValue()));
        }

        if (dto.getPatientId() != null
                && !Objects.equals(appointment.getPatient().getId(), dto.getPatientId().longValue())) {
            appointment.setPatient(validatePatient(dto.getPatientId().longValue()));
        }

        Long doctorId = appointment.getDoctor().getId();
        Long patientId = appointment.getPatient().getId();
        LocalDate date = dto.getDate() != null ? LocalDate.parse(dto.getDate()) : appointment.getDate();
        LocalTime time = dto.getTime() != null ? LocalTime.parse(dto.getTime()) : appointment.getTime();
        Integer duration = dto.getDuration() != null ? dto.getDuration() : appointment.getDuration();

        // Availability checks
        checkAvailability(doctorId, date, time, duration, "doctor", true);
        checkAvailability(patientId, date, time, duration, "patient", true);

        if (dto.getDate() != null) appointment.setDate(date);
        if (dto.getTime() != null) appointment.setTime(time);
        if (dto.getDuration() != null) appointment.setDuration(duration);
        if (dto.getNotes() != null) appointment.setNotes(dto.getNotes());
        if (dto.getSymptoms() != null) appointment.setSymptoms(dto.getSymptoms());
        if (dto.getType() != null) appointment.setType(dto.getType());
        if (dto.getStatus() != null) appointment.setStatus(
                AppointmentStatus.values()[dto.getStatus()] // or map properly
        );
        if (dto.getProcedureIds() != null) {
            List<Procedure> procedures = procedureRepository.findAllById(dto.getProcedureIds());
            appointment.setProcedures(new ArrayList<>(procedures));
        }

        return appointmentRepository.save(appointment);
    }

    // ----------------------------------------------------
    // DUPLICATE SINGLE
    // ----------------------------------------------------
    @Transactional
    public Appointment duplicate(Long id, String dateStr, String timeStr) {
        Appointment original = appointmentRepository.findByIdWithProcedures(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Приём с ID " + id + " не найден"
                ));

        LocalDate newDate = dateStr != null ? LocalDate.parse(dateStr) : original.getDate();
        LocalTime newTime = timeStr != null ? LocalTime.parse(timeStr) : original.getTime();

        checkAvailability(original.getDoctor().getId(), newDate, newTime,
                original.getDuration(), "doctor", true);
        checkAvailability(original.getPatient().getId(), newDate, newTime,
                original.getDuration(), "patient", true);

        Appointment duplicate = new Appointment();
        duplicate.setDate(newDate);
        duplicate.setTime(newTime);
        duplicate.setDoctor(original.getDoctor());
        duplicate.setPatient(original.getPatient());
        duplicate.setDuration(original.getDuration());
        duplicate.setNotes(original.getNotes());
        duplicate.setSymptoms(original.getSymptoms());
        duplicate.setType(original.getType());
        duplicate.setStatus(AppointmentStatus.SCHEDULED);
        duplicate.setProcedures(new ArrayList<>(original.getProcedures()));

        return appointmentRepository.save(duplicate);
    }

    // ----------------------------------------------------
    // DUPLICATE RANGE
    // ----------------------------------------------------
    @Transactional
    public Map<String, Object> duplicateRange(String copyDateStr, String dateToStr) {
        LocalDate copyDate = LocalDate.parse(copyDateStr);
        LocalDate dateTo = LocalDate.parse(dateToStr);

        if (copyDate.isAfter(dateTo)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "copyDate должен быть меньше или равен dateTo"
            );
        }

        List<Appointment> appointmentsToCopy =
                appointmentRepository.findAllByDateWithRelations(copyDate);

        if (appointmentsToCopy.isEmpty()) {
            Map<String, Object> res = new HashMap<>();
            res.put("message", "Нет записей для копирования на указанную дату");
            res.put("successful", List.of());
            res.put("failed", List.of());
            return res;
        }

        List<Map<String, Object>> successful = new ArrayList<>();
        List<Map<String, Object>> failed = new ArrayList<>();

        for (Appointment appointment : appointmentsToCopy) {
            try {
                checkAvailability(
                        appointment.getDoctor().getId(),
                        dateTo,
                        appointment.getTime(),
                        appointment.getDuration(),
                        "doctor",
                        true
                );
                checkAvailability(
                        appointment.getPatient().getId(),
                        dateTo,
                        appointment.getTime(),
                        appointment.getDuration(),
                        "patient",
                        true
                );

                Appointment duplicate = new Appointment();
                duplicate.setDate(dateTo);
                duplicate.setTime(appointment.getTime());
                duplicate.setDoctor(appointment.getDoctor());
                duplicate.setPatient(appointment.getPatient());
                duplicate.setDuration(appointment.getDuration());
                duplicate.setNotes(appointment.getNotes());
                duplicate.setSymptoms(appointment.getSymptoms());
                duplicate.setType(appointment.getType());
                duplicate.setStatus(AppointmentStatus.SCHEDULED);
                duplicate.setProcedures(new ArrayList<>(appointment.getProcedures()));

                Appointment saved = appointmentRepository.save(duplicate);

                Map<String, Object> ok = new HashMap<>();
                ok.put("originalId", appointment.getId());
                ok.put("newId", saved.getId());
                ok.put("originalDate", copyDateStr);
                ok.put("newDate", dateToStr);
                ok.put("time", appointment.getTime().toString());
                ok.put("doctorName", appointment.getDoctor().getFullName());
                ok.put("patientName", appointment.getPatient().getFullName());
                successful.add(ok);
            } catch (Exception ex) {
                Map<String, Object> fail = new HashMap<>();
                fail.put("originalId", appointment.getId());
                fail.put("originalDate", copyDateStr);
                fail.put("targetDate", dateToStr);
                fail.put("time", appointment.getTime().toString());
                fail.put("doctorName", appointment.getDoctor().getFullName());
                fail.put("patientName", appointment.getPatient().getFullName());
                fail.put("reason", ex.getMessage());
                failed.add(fail);
            }
        }

        Map<String, Object> res = new HashMap<>();
        res.put("message", String.format("Скопировано записей с %s на %s", copyDateStr, dateToStr));
        res.put("totalAppointments", appointmentsToCopy.size());
        res.put("successful", successful);
        res.put("failed", failed);
        res.put("successfulCount", successful.size());
        res.put("failedCount", failed.size());
        return res;
    }

    // ----------------------------------------------------
    // HELPERS
    // ----------------------------------------------------
    private LocalTime addMinutes(LocalTime time, int minutes) {
        return time.plusMinutes(minutes);
    }

    private Patient validatePatient(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Пациент с ID " + patientId + " не найден"
                ));
    }

    private Doctor validateDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Врач с ID " + doctorId + " не найден"
                ));

        if (Boolean.FALSE.equals(doctor.getIsActive())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Врач с ID " + doctorId + " неактивен и не может принимать пациентов"
            );
        }
        return doctor;
    }

    private void checkAvailability(
            Long entityId,
            LocalDate date,
            LocalTime time,
            Integer duration,
            String type, // "doctor" | "patient"
            boolean condition
    ) {
        if (!condition) return;

        List<Appointment> existing;
        if ("doctor".equals(type)) {
            existing = appointmentRepository
                    .findByDoctorIdAndDateAndStatus(entityId, date, AppointmentStatus.SCHEDULED);
        } else {
            existing = appointmentRepository
                    .findByPatientIdAndDateAndStatus(entityId, date, AppointmentStatus.SCHEDULED);
        }

        if (existing.isEmpty()) return;

        LocalTime newStart = time;
        LocalTime newEnd = addMinutes(time, duration);

        for (Appointment a : existing) {
            LocalTime existingStart = a.getTime();
            LocalTime existingEnd = addMinutes(existingStart, a.getDuration());

            boolean overlap = newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
            if (overlap) {
                String entityName;
                String entityType;
                if ("doctor".equals(type)) {
                    entityName = "\"" + a.getDoctor().getFullName() + "\"";
                    entityType = "врача";
                } else {
                    entityName = "\"" + a.getPatient().getFullName() + "\"";
                    entityType = "пациента";
                }

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        String.format(
                                "У %s %s уже есть запись в диапазоне времени %s - %s",
                                entityType,
                                entityName,
                                existingStart,
                                existingEnd
                        )
                );
            }
        }
    }
}


