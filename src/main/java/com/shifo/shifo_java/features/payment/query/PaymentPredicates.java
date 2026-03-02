package com.shifo.shifo_java.features.payment.query;

import com.shifo.shifo_java.features.appointment.Appointment;
import com.shifo.shifo_java.features.patient.Patient;
import com.shifo.shifo_java.features.payment.Payment;
import com.shifo.shifo_java.features.payment.dto.FilterPaymentDto;
import jakarta.persistence.criteria.*;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public final class PaymentPredicates {

    private PaymentPredicates() {}

    public static List<Predicate> build(
            FilterPaymentDto filter,
            CriteriaBuilder cb,
            Root<Payment> payment,
            Join<Payment, Appointment> appointment,
            Join<Appointment, Patient> appointmentPatient,
            Join<Payment, Patient> paymentPatient
    ) {
        List<Predicate> predicates = new ArrayList<>();

        if (filter.getPatientId() != null) {
            predicates.add(
                    cb.or(
                            cb.equal(appointmentPatient.get("id"), filter.getPatientId()),
                            cb.equal(paymentPatient.get("id"), filter.getPatientId())
                    )
            );
        }

        if (filter.getAppointmentId() != null) {
            predicates.add(cb.equal(appointment.get("id"), filter.getAppointmentId()));
        }

        if (filter.getPaymentType() != null) {
            predicates.add(cb.equal(payment.get("paymentType"), filter.getPaymentType()));
        }

        if (filter.getStatus() != null) {
            predicates.add(cb.equal(payment.get("status"), filter.getStatus()));
        }

        if (filter.getPaymentKind() != null) {
            predicates.add(cb.equal(payment.get("paymentKind"), filter.getPaymentKind()));
        }

        if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
            String term = "%" + filter.getSearch().toLowerCase() + "%";

            Expression<String> fullName =
                    cb.lower(
                            cb.coalesce(
                                    paymentPatient.get("fullName"),
                                    appointmentPatient.get("fullName")
                            )
                    );

            predicates.add(cb.like(fullName, term));
        }

        if (filter.getDateFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(payment.get("paidAt"), filter.getDateFrom().atStartOfDay().toInstant(ZoneOffset.UTC)));
        }

        if (filter.getDateTo() != null) {
            predicates.add(cb.lessThan(payment.get("paidAt"), filter.getDateTo().plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC)));
        }

        return predicates;
    }
}
