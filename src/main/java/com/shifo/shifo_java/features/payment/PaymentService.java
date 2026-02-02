package com.shifo.shifo_java.features.payment;

import com.shifo.shifo_java.features.appointment.Appointment;
import com.shifo.shifo_java.common.exceptions.BadRequestException;
import com.shifo.shifo_java.features.patient.Patient;
import com.shifo.shifo_java.features.payment.dto.CreatePaymentDto;
import com.shifo.shifo_java.features.payment.dto.FilterPaymentDto;
import com.shifo.shifo_java.features.payment.dto.UpdatePaymentDto;
import com.shifo.shifo_java.features.appointment.AppointmentRepository;
import com.shifo.shifo_java.features.patient.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;

    public Payment create(CreatePaymentDto dto) {

        // Check existing payment for appointment
        Optional<Payment> paymentExists = paymentRepository.findByAppointmentId(dto.getAppointmentId());
        if (paymentExists.isPresent()) {
            throw new BadRequestException("Платеж уже существует");
        }

        Appointment appointment = appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException("Запись не найдена"));

        Payment payment = new Payment();
        payment.setAppointment(appointment);
        payment.setAmount(dto.getAmount());
        payment.setPaymentKind(dto.getPaymentKind());
        payment.setPaymentType(dto.getPaymentType());
        payment.setStatus(dto.getStatus() != null ? dto.getStatus() : "pending");
        payment.setPaidAt("paid".equals(dto.getStatus()) ? LocalDateTime.now() : null);

        Payment saved = paymentRepository.save(payment);

        // Update balance if applicable
        if ("paid".equals(dto.getStatus())
                && isBalanceChangingKind(dto.getPaymentKind())) {

            Patient patient = patientRepository.findById(appointment.getPatient().getId())
                    .orElse(null);

            if (patient != null) {
                double curr = patient.getBalance() == null ? 0 : patient.getBalance();
                double delta = getDelta(dto.getPaymentKind(), dto.getAmount());
                patient.setBalance(round(curr + delta));
                patientRepository.save(patient);
            }
        }

        return saved;
    }

    public Page<Payment> findAll(FilterPaymentDto filterDto) {
        Pageable pageable = PageRequest.of(
                filterDto.getPage() - 1,
                filterDto.getLimit(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return paymentRepository.filterPayments(
                filterDto.getAppointmentId(),
                filterDto.getPaymentType(),
                filterDto.getStatus(),
                filterDto.getSearch(),
                filterDto.getDateFrom(),
                filterDto.getDateTo(),
                pageable
        );
    }

    public Payment findOne(Long appointmentId) {
        return paymentRepository.findByAppointmentIdWithRelations(appointmentId)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException(
                        "Оплата с appointmentId " + appointmentId + " не найден"
                ));
    }

    public Payment update(Long id, UpdatePaymentDto dto) {
        Payment payment = findOne(id);

        boolean isBecomingPaid =
                "paid".equals(dto.getStatus()) && !"paid".equals(payment.getStatus());

        if (isBecomingPaid) {
            payment.setPaidAt(LocalDateTime.now());

            String kind = dto.getPaymentKind() != null ?
                    dto.getPaymentKind() : payment.getPaymentKind();

            if (isBalanceChangingKind(kind)) {
                double amount = dto.getAmount() != null ? dto.getAmount() : payment.getAmount();
                Patient patient = patientRepository.findById(payment.getAppointment().getPatient().getId())
                        .orElseThrow();

                double curr = patient.getBalance() == null ? 0 : patient.getBalance();
                patient.setBalance(round(curr + getDelta(kind, amount)));
                patientRepository.save(patient);
            }
        }

        BeanUtils.copyProperties(dto, payment, getNullPropertyNames(dto));
        return paymentRepository.save(payment);
    }

    public void remove(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new NotFoundException("Оплата с id " + id + " не найден");
        }
        paymentRepository.softDelete(id);
    }

    public Payment updateStatus(Long id, String status) {
        Payment payment = findOne(id);
        payment.setStatus(status);

        if ("paid".equals(status) && payment.getPaidAt() == null) {
            payment.setPaidAt(LocalDateTime.now());

            if (isBalanceChangingKind(payment.getPaymentKind())) {
                Patient patient = patientRepository.findById(payment.getAppointment().getPatient().getId())
                        .orElseThrow();
                double curr = patient.getBalance() == null ? 0 : patient.getBalance();
                patient.setBalance(round(curr + getDelta(payment.getPaymentKind(), payment.getAmount())));
                patientRepository.save(patient);
            }
        }

        return paymentRepository.save(payment);
    }

    public SummaryDto getSummary(SummaryPaymentQueryDto query) {
        Double totalRevenue = paymentRepository.calcTotalByStatus("paid", query);
        Double totalPending = paymentRepository.calcTotalByStatus("pending", query);
        Long count = paymentRepository.countFiltered(query);

        return new SummaryDto(
                totalRevenue != null ? totalRevenue : 0,
                totalPending != null ? totalPending : 0,
                count
        );
    }

    // Utility helpers

    private boolean isBalanceChangingKind(String kind) {
        return "debt".equals(kind) || "prepayment".equals(kind) || "debt_payment".equals(kind);
    }

    private double getDelta(String kind, double amount) {
        return "debt".equals(kind) ? -amount : amount;
    }

    private double round(double value) {
        return Math.round(value * 100) / 100.0;
    }

    private String[] getNullPropertyNames(Object source) {
        return Arrays.stream(BeanUtils.getPropertyDescriptors(source.getClass()))
                .map(PropertyDescriptor::getName)
                .filter(name -> {
                    try {
                        return BeanUtils.getPropertyDescriptor(source.getClass(), name)
                                .getReadMethod()
                                .invoke(source) == null;
                    } catch (Exception ignored) {}
                    return false;
                })
                .toArray(String[]::new);
    }
}

