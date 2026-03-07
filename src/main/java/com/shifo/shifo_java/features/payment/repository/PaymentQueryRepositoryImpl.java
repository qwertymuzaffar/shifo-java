package com.shifo.shifo_java.features.payment.repository;

import com.shifo.shifo_java.features.appointment.Appointment;
import com.shifo.shifo_java.features.patient.Patient;
import com.shifo.shifo_java.features.payment.Payment;
import com.shifo.shifo_java.features.payment.query.PaymentPredicates;
import com.shifo.shifo_java.features.payment.dto.FilterPaymentDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PaymentQueryRepositoryImpl implements PaymentQueryRepository {

    private final EntityManager entityManager;

    @Override
    public List<Long> findPageIds(FilterPaymentDto filter, int page, int limit) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> idQuery = cb.createQuery(Long.class);
        Root<Payment> payment = idQuery.from(Payment.class);

        Join<Payment, Appointment> appointment = payment.join("appointment", JoinType.LEFT);
        Join<Appointment, Patient> appointmentPatient = appointment.join("patient", JoinType.LEFT);
        Join<Payment, Patient> paymentPatient = payment.join("patient", JoinType.LEFT);

        List<Predicate> predicates =
                PaymentPredicates.build(filter, cb, payment, appointment, appointmentPatient, paymentPatient);

        idQuery.select(payment.get("id"))
                .where(predicates.toArray(new Predicate[0]))
                .orderBy(
                        cb.desc(payment.get("paidAt")),
                        cb.desc(payment.get("createdAt")),
                        cb.desc(payment.get("id"))
                );

        return entityManager.createQuery(idQuery)
                .setFirstResult((page - 1) * limit)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public long count(FilterPaymentDto filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Payment> payment = cq.from(Payment.class);

        Join<Payment, Appointment> appointment = payment.join("appointment", JoinType.LEFT);
        Join<Appointment, Patient> appointmentPatient = appointment.join("patient", JoinType.LEFT);
        Join<Payment, Patient> paymentPatient = payment.join("patient", JoinType.LEFT);

        List<Predicate> predicates =
                PaymentPredicates.build(filter, cb, payment, appointment, appointmentPatient, paymentPatient);

        cq.select(cb.count(payment))
                .where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(cq).getSingleResult();
    }

    @Override
    public List<Payment> fetchByIds(List<Long> ids) {
        if (ids.isEmpty()) return List.of();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Payment> query = cb.createQuery(Payment.class);
        Root<Payment> root = query.from(Payment.class);

        root.fetch("patient", JoinType.LEFT);
        root.fetch("appointment", JoinType.LEFT)
                .fetch("patient", JoinType.LEFT);

        query.select(root)
                .where(root.get("id").in(ids))
                .distinct(true);

        return entityManager.createQuery(query).getResultList();
    }
}
