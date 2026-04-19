package com.shifo.shifo_java.features.payment;

import com.shifo.shifo_java.common.exceptions.NotFoundException;
import com.shifo.shifo_java.features.balance.BalanceService;
import com.shifo.shifo_java.features.payment.context.PaymentContext;
import com.shifo.shifo_java.features.payment.context.PaymentContextResolver;
import com.shifo.shifo_java.features.payment.dto.CreatePaymentDto;
import com.shifo.shifo_java.features.payment.dto.FilterPaymentDto;
import com.shifo.shifo_java.features.payment.dto.ListWithCountDto;
import com.shifo.shifo_java.features.payment.dto.PaymentDto;
import com.shifo.shifo_java.features.payment.factory.PaymentFactory;
import com.shifo.shifo_java.features.payment.model.PaymentKind;
import com.shifo.shifo_java.features.payment.model.PaymentStatus;
import com.shifo.shifo_java.features.payment.policy.PaymentPolicy;
import com.shifo.shifo_java.features.payment.policy.PaymentPolicyRegistry;
import com.shifo.shifo_java.features.payment.repository.PaymentQueryRepository;
import com.shifo.shifo_java.features.payment.repository.PaymentRepository;
import com.shifo.shifo_java.security.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private PaymentQueryRepository paymentQueryRepository;
    @Mock private BalanceService balanceService;
    @Mock private PaymentMapper paymentMapper;
    @Mock private PaymentPolicyRegistry policyRegistry;
    @Mock private PaymentContextResolver contextResolver;
    @Mock private SecurityUtils securityUtils;
    @Mock private PaymentFactory paymentFactory;
    @Mock private PaymentPolicy paymentPolicy;

    @InjectMocks private PaymentService paymentService;

    @Test
    void shouldCreatePaidPaymentAndRecordBalance() {
        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setPaymentKind(PaymentKind.PAYMENT);

        PaymentContext context = new PaymentContext(dto, PaymentKind.PAYMENT, null, null);
        context.setStatus(PaymentStatus.PAID);

        Payment payment = new Payment();
        payment.setId(10L);

        PaymentDto expected = new PaymentDto();
        expected.setId(10L);

        when(contextResolver.resolve(dto, PaymentKind.PAYMENT)).thenReturn(context);
        when(securityUtils.getCurrentUserId()).thenReturn(42L);
        when(policyRegistry.get(PaymentKind.PAYMENT)).thenReturn(paymentPolicy);
        when(paymentFactory.create(context)).thenReturn(payment);
        when(paymentMapper.toDto(payment)).thenReturn(expected);

        PaymentDto result = paymentService.create(dto);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(context.getUserId()).isEqualTo(42L);

        verify(paymentPolicy).validate(context);
        verify(paymentPolicy).enrich(context);
        verify(paymentRepository).save(payment);
        verify(paymentPolicy).applySideEffects(context);
        verify(balanceService).recordPayment(payment);
    }

    @Test
    void shouldCreatePendingPaymentWithoutRecordingBalance() {
        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setPaymentKind(PaymentKind.PAYMENT);

        PaymentContext context = new PaymentContext(dto, PaymentKind.PAYMENT, null, null);
        context.setStatus(PaymentStatus.PENDING);

        Payment payment = new Payment();
        payment.setId(11L);

        when(contextResolver.resolve(dto, PaymentKind.PAYMENT)).thenReturn(context);
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(policyRegistry.get(PaymentKind.PAYMENT)).thenReturn(paymentPolicy);
        when(paymentFactory.create(context)).thenReturn(payment);
        when(paymentMapper.toDto(payment)).thenReturn(new PaymentDto());

        paymentService.create(dto);

        verify(paymentRepository).save(payment);
        verify(balanceService, never()).recordPayment(any());
    }

    @Test
    void shouldDefaultPaymentKindToPaymentWhenDtoKindIsNull() {
        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setPaymentKind(null);

        PaymentContext context = new PaymentContext(dto, PaymentKind.PAYMENT, null, null);
        context.setStatus(PaymentStatus.PENDING);

        Payment payment = new Payment();

        when(contextResolver.resolve(dto, PaymentKind.PAYMENT)).thenReturn(context);
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(policyRegistry.get(PaymentKind.PAYMENT)).thenReturn(paymentPolicy);
        when(paymentFactory.create(context)).thenReturn(payment);
        when(paymentMapper.toDto(payment)).thenReturn(new PaymentDto());

        paymentService.create(dto);

        verify(contextResolver).resolve(dto, PaymentKind.PAYMENT);
        verify(policyRegistry).get(PaymentKind.PAYMENT);
    }

    @Test
    void shouldReturnPagedPayments() {
        FilterPaymentDto filter = new FilterPaymentDto();
        filter.setPage(2);
        filter.setLimit(5);

        Payment p1 = new Payment();
        p1.setId(1L);
        Payment p2 = new Payment();
        p2.setId(2L);

        PaymentDto d1 = new PaymentDto();
        d1.setId(1L);
        PaymentDto d2 = new PaymentDto();
        d2.setId(2L);

        when(paymentQueryRepository.findPageIds(filter, 2, 5)).thenReturn(List.of(1L, 2L));
        when(paymentQueryRepository.count(filter)).thenReturn(17L);
        when(paymentQueryRepository.fetchByIds(List.of(1L, 2L))).thenReturn(List.of(p2, p1));
        when(paymentMapper.toDtoList(List.of(p1, p2))).thenReturn(List.of(d1, d2));

        ListWithCountDto<PaymentDto> result = paymentService.findAll(filter);

        assertThat(result.getCount()).isEqualTo(17L);
        assertThat(result.getData()).extracting(PaymentDto::getId).containsExactly(1L, 2L);
    }

    @Test
    void shouldReturnEmptyResultAndSkipFetchWhenNoIdsMatch() {
        FilterPaymentDto filter = new FilterPaymentDto();

        when(paymentQueryRepository.findPageIds(filter, 1, 10)).thenReturn(List.of());
        when(paymentQueryRepository.count(filter)).thenReturn(0L);

        ListWithCountDto<PaymentDto> result = paymentService.findAll(filter);

        assertThat(result.getCount()).isZero();
        assertThat(result.getData()).isEmpty();
        verify(paymentQueryRepository, never()).fetchByIds(any());
    }

    @Test
    void shouldApplyDefaultPaginationWhenPageAndLimitAreNull() {
        FilterPaymentDto filter = new FilterPaymentDto();
        filter.setPage(null);
        filter.setLimit(null);

        when(paymentQueryRepository.findPageIds(filter, 1, 10)).thenReturn(List.of());
        when(paymentQueryRepository.count(filter)).thenReturn(0L);

        paymentService.findAll(filter);

        verify(paymentQueryRepository).findPageIds(filter, 1, 10);
    }

    @Test
    void shouldRemovePaymentByReversingBalanceAndSoftDeleting() {
        Payment payment = new Payment();
        payment.setId(5L);

        when(paymentRepository.findByIdWithRelations(5L)).thenReturn(Optional.of(payment));

        paymentService.remove(5L);

        assertThat(payment.getDeletedAt()).isNotNull();
        verify(balanceService).handlePaymentStatusRemoved(5L);
        verify(balanceService).reversePayment(payment);
        verify(paymentRepository).save(payment);
    }

    @Test
    void shouldThrowNotFoundWhenRemovingMissingPayment() {
        when(paymentRepository.findByIdWithRelations(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.remove(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");

        verify(balanceService, never()).reversePayment(any());
        verify(paymentRepository, never()).save(any(Payment.class));
    }
}
