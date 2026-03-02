package com.shifo.shifo_java.features.appointment;

import com.shifo.shifo_java.common.exceptions.NotFoundException;
import com.shifo.shifo_java.features.appointment.dto.*;
import com.shifo.shifo_java.features.appointment.mapper.AppointmentDetailsMapper;
import com.shifo.shifo_java.features.appointment.mapper.AppointmentMapper;
import com.shifo.shifo_java.features.appointment.specification.AppointmentSpecification;
import com.shifo.shifo_java.features.payment.Payment;
import com.shifo.shifo_java.features.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {


    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final AppointmentDetailsMapper appointmentDetailsMapper;
    private final PaymentRepository paymentRepository;

    public List<AppointmentDto> findAll(FilterAppointmentDto filter) {

        Specification<Appointment> specification = AppointmentSpecification.build(filter);
        Pageable pageable = PageRequest.of(filter.getPage() - 1, filter.getLimit());
        Page<Appointment> page = appointmentRepository.findAll(specification, pageable);
        List<Appointment> entities = page.getContent();

        return entities.stream()
                .map(appointmentMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AppointmentDetailsDto findOne(Long id) {

        Appointment appointment = appointmentRepository
                .findDetailedWithProcedures(id)
                .orElseThrow(() -> new NotFoundException("Appointment not found"));

        // Load payments in second query
        List<Payment> payments = paymentRepository.findByAppointmentId(id);

        appointment.setPayments(payments);

        BigDecimal total = payments.stream()
                .map(p -> p.getAmount() == null ? BigDecimal.ZERO : p.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        AppointmentDetailsDto dto = appointmentDetailsMapper.toDto(appointment);
        dto.setTotalPaymentAmount(total);

        return dto;
    }
}
