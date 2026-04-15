package com.shifo.shifo_java.features.payment;

import com.shifo.shifo_java.features.balance.BalanceService;
import com.shifo.shifo_java.features.payment.dto.CreatePaymentDto;
import com.shifo.shifo_java.features.payment.dto.FilterPaymentDto;
import com.shifo.shifo_java.features.payment.dto.ListWithCountDto;
import com.shifo.shifo_java.features.payment.dto.PaymentDto;
import com.shifo.shifo_java.features.payment.context.PaymentContext;
import com.shifo.shifo_java.features.payment.factory.PaymentFactory;
import com.shifo.shifo_java.features.payment.model.PaymentKind;
import com.shifo.shifo_java.features.payment.policy.PaymentPolicy;
import com.shifo.shifo_java.features.payment.model.PaymentStatus;
import com.shifo.shifo_java.features.payment.context.PaymentContextResolver;
import com.shifo.shifo_java.features.payment.policy.PaymentPolicyRegistry;
import com.shifo.shifo_java.features.payment.repository.PaymentQueryRepository;
import com.shifo.shifo_java.features.payment.repository.PaymentRepository;
import com.shifo.shifo_java.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import com.shifo.shifo_java.common.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentQueryRepository paymentQueryRepository;

    private final BalanceService balanceService;
    private final PaymentMapper paymentMapper;
    private final PaymentPolicyRegistry policyRegistry;
    private final PaymentContextResolver contextResolver;
    private final SecurityUtils securityUtils;
    private final PaymentFactory paymentFactory;


    @Transactional
    public PaymentDto create(CreatePaymentDto dto) {

        PaymentKind kind = Optional.ofNullable(dto.getPaymentKind())
                .orElse(PaymentKind.PAYMENT);

        PaymentContext context = contextResolver.resolve(dto, kind);
        context.setUserId(securityUtils.getCurrentUserId());

        PaymentPolicy policy = policyRegistry.get(kind);

        policy.validate(context);
        policy.enrich(context);

        Payment payment = paymentFactory.create(context);

        paymentRepository.save(payment);

        policy.applySideEffects(context);

        if (context.getStatus() == PaymentStatus.PAID) {
            balanceService.recordPayment(payment);
        }

        return paymentMapper.toDto(payment);
    }

    @Transactional(readOnly = true)
    public ListWithCountDto<PaymentDto> findAll(FilterPaymentDto filter) {

        int page = Optional.ofNullable(filter.getPage()).orElse(1);
        int limit = Optional.ofNullable(filter.getLimit()).orElse(10);

        List<Long> ids = paymentQueryRepository.findPageIds(filter, page, limit);
        long total = paymentQueryRepository.count(filter);

        if (ids.isEmpty()) {
            return new ListWithCountDto<>(List.of(), total);
        }

        List<Payment> payments = paymentQueryRepository.fetchByIds(ids);

        Map<Long, Payment> byId = payments.stream()
                .collect(Collectors.toMap(Payment::getId, p -> p));

        List<Payment> ordered = ids.stream()
                .map(byId::get)
                .toList();

        List<PaymentDto> dto = paymentMapper.toDtoList(ordered);

        return new ListWithCountDto<>(dto, total);
    }


    @Transactional
    public void remove(Long id) {
        Payment payment = paymentRepository
                .findByIdWithRelations(id)
                .orElseThrow(() ->
                        new NotFoundException("Оплата с id " + id + " не найден"));

        balanceService.handlePaymentStatusRemoved(payment.getId());
        balanceService.reversePayment(payment);

        payment.softDelete();
        paymentRepository.save(payment);
    }
}
