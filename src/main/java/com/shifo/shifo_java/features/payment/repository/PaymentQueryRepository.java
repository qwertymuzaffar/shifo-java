package com.shifo.shifo_java.features.payment.repository;

import com.shifo.shifo_java.features.payment.Payment;
import com.shifo.shifo_java.features.payment.dto.FilterPaymentDto;

import java.util.List;

public interface PaymentQueryRepository {
    List<Long> findPageIds(FilterPaymentDto filter, int page, int limit);
    long count(FilterPaymentDto filter);
    List<Payment> fetchByIds(List<Long> ids);
}
