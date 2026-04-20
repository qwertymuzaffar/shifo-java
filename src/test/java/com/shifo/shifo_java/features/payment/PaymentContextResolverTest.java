package com.shifo.shifo_java.features.payment;

import com.shifo.shifo_java.common.exceptions.BadRequestException;
import com.shifo.shifo_java.common.exceptions.NotFoundException;
import com.shifo.shifo_java.features.appointment.Appointment;
import com.shifo.shifo_java.features.appointment.AppointmentRepository;
import com.shifo.shifo_java.features.patient.Patient;
import com.shifo.shifo_java.features.patient.PatientRepository;
import com.shifo.shifo_java.features.payment.context.PaymentContext;
import com.shifo.shifo_java.features.payment.context.PaymentContextResolver;
import com.shifo.shifo_java.features.payment.dto.CreatePaymentDto;
import com.shifo.shifo_java.features.payment.model.PaymentKind;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentContextResolverTest {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private PatientRepository patientRepository;

    @InjectMocks private PaymentContextResolver resolver;

    @Test
    void shouldResolveAppointmentAndPatientFromAppointment() {
        Patient patient = new Patient();
        patient.setId(3L);
        Appointment appointment = new Appointment();
        appointment.setId(7L);
        appointment.setPatient(patient);

        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setAppointmentId(7L);

        when(appointmentRepository.findById(7L)).thenReturn(Optional.of(appointment));

        PaymentContext context = resolver.resolve(dto, PaymentKind.PAYMENT);

        assertThat(context.getAppointment()).isSameAs(appointment);
        assertThat(context.getPatient()).isSameAs(patient);
        assertThat(context.getKind()).isEqualTo(PaymentKind.PAYMENT);
    }

    @Test
    void shouldThrowNotFoundWhenAppointmentMissing() {
        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setAppointmentId(99L);

        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resolver.resolve(dto, PaymentKind.PAYMENT))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldThrowBadRequestWhenAppointmentIdMissingForNonPrepayment() {
        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setAppointmentId(null);

        assertThatThrownBy(() -> resolver.resolve(dto, PaymentKind.PAYMENT))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void shouldResolvePrepaymentFromPatientIdWhenNoAppointment() {
        Patient patient = new Patient();
        patient.setId(5L);

        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setAppointmentId(null);
        dto.setPatientId(5L);

        when(patientRepository.findById(5L)).thenReturn(Optional.of(patient));

        PaymentContext context = resolver.resolve(dto, PaymentKind.PREPAYMENT);

        assertThat(context.getAppointment()).isNull();
        assertThat(context.getPatient()).isSameAs(patient);
    }

    @Test
    void shouldThrowBadRequestForPrepaymentWithoutAppointmentOrPatientId() {
        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setAppointmentId(null);
        dto.setPatientId(null);

        assertThatThrownBy(() -> resolver.resolve(dto, PaymentKind.PREPAYMENT))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("предоплаты");
    }

    @Test
    void shouldThrowNotFoundWhenPrepaymentPatientMissing() {
        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setAppointmentId(null);
        dto.setPatientId(42L);

        when(patientRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resolver.resolve(dto, PaymentKind.PREPAYMENT))
                .isInstanceOf(NotFoundException.class);
    }
}
