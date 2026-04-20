package com.shifo.shifo_java.features.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shifo.shifo_java.common.exceptions.NotFoundException;
import com.shifo.shifo_java.config.GlobalExceptionHandler;
import com.shifo.shifo_java.features.payment.dto.CreatePaymentDto;
import com.shifo.shifo_java.features.payment.dto.FilterPaymentDto;
import com.shifo.shifo_java.features.payment.dto.ListWithCountDto;
import com.shifo.shifo_java.features.payment.dto.PaymentDto;
import com.shifo.shifo_java.features.payment.model.PaymentKind;
import com.shifo.shifo_java.features.payment.model.PaymentStatus;
import com.shifo.shifo_java.features.payment.model.PaymentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PaymentControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private FakePaymentService paymentService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        paymentService = new FakePaymentService();
        mockMvc = MockMvcBuilders.standaloneSetup(new PaymentController(paymentService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldCreatePaymentAndReturn201() throws Exception {
        CreatePaymentDto request = new CreatePaymentDto();
        request.setAppointmentId(7L);
        request.setAmount(new BigDecimal("250.00"));
        request.setPaymentType(PaymentType.CASH);
        request.setPaymentKind(PaymentKind.PAYMENT);

        paymentService.createResponse = PaymentDto.builder()
                .id(1L)
                .amount(new BigDecimal("250.00"))
                .status(PaymentStatus.PAID)
                .build();

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("paid"));
    }

    @Test
    void shouldReturnPagedPayments() throws Exception {
        paymentService.findAllResponse = new ListWithCountDto<>(
                List.of(
                        PaymentDto.builder().id(1L).build(),
                        PaymentDto.builder().id(2L).build()
                ),
                2L
        );

        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.data.size()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[1].id").value(2));
    }

    @Test
    void shouldRemovePayment() throws Exception {
        mockMvc.perform(delete("/api/payments/5"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenRemovingMissingPayment() throws Exception {
        paymentService.removeException = new NotFoundException("Оплата с id 99 не найден");

        mockMvc.perform(delete("/api/payments/99"))
                .andExpect(status().isNotFound());
    }

    private static final class FakePaymentService extends PaymentService {

        private PaymentDto createResponse;
        private ListWithCountDto<PaymentDto> findAllResponse;
        private RuntimeException removeException;

        private FakePaymentService() {
            super(null, null, null, null, null, null, null, null);
        }

        @Override
        public PaymentDto create(CreatePaymentDto dto) {
            return createResponse;
        }

        @Override
        public ListWithCountDto<PaymentDto> findAll(FilterPaymentDto filter) {
            return findAllResponse;
        }

        @Override
        public void remove(Long id) {
            if (removeException != null) throw removeException;
        }
    }
}
