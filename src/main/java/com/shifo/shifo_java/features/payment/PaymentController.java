package com.shifo.shifo_java.features.payment;

import com.shifo.shifo_java.features.payment.dto.CreatePaymentDto;
import com.shifo.shifo_java.features.payment.dto.FilterPaymentDto;
import com.shifo.shifo_java.features.payment.dto.ListWithCountDto;
import com.shifo.shifo_java.features.payment.dto.PaymentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments")
public class PaymentController {

    private final PaymentService paymentsService;

    @PostMapping
    @Operation(summary = "Создать новый платеж")
    public ResponseEntity<PaymentDto> create(@RequestBody @Valid CreatePaymentDto dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(paymentsService.create(dto));
    }

    @GetMapping
    @Operation(summary = "Получить все платежи с возможностью фильтрации")
    public ResponseEntity<ListWithCountDto<PaymentDto>> findAll(
            @Valid @ModelAttribute FilterPaymentDto filterDto
    ) {
        return ResponseEntity.ok(paymentsService.findAll(filterDto));
    }
}
