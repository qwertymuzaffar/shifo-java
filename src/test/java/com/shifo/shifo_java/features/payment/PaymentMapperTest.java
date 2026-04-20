package com.shifo.shifo_java.features.payment;

import com.shifo.shifo_java.features.appointment.Appointment;
import com.shifo.shifo_java.features.appointment.dto.AppointmentDto;
import com.shifo.shifo_java.features.appointment.mapper.AppointmentMapper;
import com.shifo.shifo_java.features.patient.Patient;
import com.shifo.shifo_java.features.patient.PatientMapper;
import com.shifo.shifo_java.features.patient.dto.PatientDto;
import com.shifo.shifo_java.features.payment.dto.PaymentDto;
import com.shifo.shifo_java.features.payment.model.PaymentKind;
import com.shifo.shifo_java.features.payment.model.PaymentStatus;
import com.shifo.shifo_java.features.payment.model.PaymentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentMapperTest {

    @Mock private PatientMapper patientMapper;
    @Mock private AppointmentMapper appointmentMapper;

    @InjectMocks private PaymentMapper paymentMapper;

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(paymentMapper.toDto(null)).isNull();
    }

    @Test
    void shouldMapAllFields() {
        Appointment appointment = new Appointment();
        appointment.setId(7L);

        Patient patient = new Patient();
        patient.setId(3L);

        Instant paidAt = Instant.parse("2026-04-18T10:00:00Z");
        Instant createdAt = Instant.parse("2026-04-18T09:00:00Z");
        Instant updatedAt = Instant.parse("2026-04-18T11:00:00Z");

        Payment payment = new Payment();
        payment.setId(1L);
        payment.setAppointment(appointment);
        payment.setPatient(patient);
        payment.setUserId(42L);
        payment.setAmount(new BigDecimal("250.00"));
        payment.setPaymentType(PaymentType.CASH);
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaymentKind(PaymentKind.PAYMENT);
        payment.setPaidAt(paidAt);
        payment.setCreatedAt(createdAt);
        payment.setUpdatedAt(updatedAt);

        AppointmentDto appointmentDto = new AppointmentDto();
        appointmentDto.setId(7L);
        PatientDto patientDto = new PatientDto();
        patientDto.setId(3L);

        when(appointmentMapper.toDto(appointment)).thenReturn(appointmentDto);
        when(patientMapper.toDto(patient)).thenReturn(patientDto);

        PaymentDto dto = paymentMapper.toDto(payment);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getAppointmentId()).isEqualTo(7L);
        assertThat(dto.getAppointment()).isSameAs(appointmentDto);
        assertThat(dto.getPatient()).isSameAs(patientDto);
        assertThat(dto.getUserId()).isEqualTo(42L);
        assertThat(dto.getAmount()).isEqualByComparingTo("250.00");
        assertThat(dto.getPaymentType()).isEqualTo(PaymentType.CASH);
        assertThat(dto.getStatus()).isEqualTo(PaymentStatus.PAID);
        assertThat(dto.getPaymentKind()).isEqualTo(PaymentKind.PAYMENT);
        assertThat(dto.getPaidAt()).isEqualTo(paidAt);
        assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
        assertThat(dto.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    void shouldMapWithNullAppointment() {
        Patient patient = new Patient();
        patient.setId(3L);

        Payment payment = new Payment();
        payment.setId(2L);
        payment.setAppointment(null);
        payment.setPatient(patient);

        when(appointmentMapper.toDto(null)).thenReturn(null);
        when(patientMapper.toDto(patient)).thenReturn(new PatientDto());

        PaymentDto dto = paymentMapper.toDto(payment);

        assertThat(dto.getAppointmentId()).isNull();
        assertThat(dto.getAppointment()).isNull();
    }

    @Test
    void shouldMapList() {
        Payment p1 = new Payment();
        p1.setId(1L);
        Payment p2 = new Payment();
        p2.setId(2L);

        when(appointmentMapper.toDto(null)).thenReturn(null);
        when(patientMapper.toDto(null)).thenReturn(null);

        List<PaymentDto> dtos = paymentMapper.toDtoList(List.of(p1, p2));

        assertThat(dtos).extracting(PaymentDto::getId).containsExactly(1L, 2L);
    }
}
