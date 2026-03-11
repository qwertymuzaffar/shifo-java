package com.shifo.shifo_java.features.payment;

import com.shifo.shifo_java.features.payment.dto.CreatePaymentDto;
import com.shifo.shifo_java.features.payment.dto.FilterPaymentDto;
import com.shifo.shifo_java.features.payment.dto.ListWithCountDto;
import com.shifo.shifo_java.features.payment.dto.PaymentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('payment.create')")
    public ResponseEntity<PaymentDto> create(@RequestBody @Valid CreatePaymentDto dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(paymentsService.create(dto));
    }

    @GetMapping
    @Operation(summary = "Получить все платежи с возможностью фильтрации")
    @PreAuthorize("hasAuthority('payment.view')")
    public ResponseEntity<ListWithCountDto<PaymentDto>> findAll(
            @Valid @ModelAttribute FilterPaymentDto filterDto
    ) {
        return ResponseEntity.ok(paymentsService.findAll(filterDto));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удалить платеж",
            description = """
                    Удаляет платеж (мягкое удаление) и выполняет связанные операции:
                    
                    При удалении платежа:
                    1. Удаляется связанная запись из таблицы balances (если существует)
                    2. Если платеж имел статус 'paid', происходит обратное изменение баланса пациента:
                       - debt и balance_deduction → сумма добавляется обратно
                       - prepayment и debt_payment → сумма вычитается
                    3. Платеж помечается как удаленный (soft delete)
                    
                    Типы платежей:
                    - debt
                    - prepayment
                    - debt_payment
                    - balance_deduction
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Платеж успешно удален"),
            @ApiResponse(responseCode = "404", description = "Платеж не найден"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @PreAuthorize("hasAuthority('payment.delete')")
    public void remove(@PathVariable Long id) {
        paymentsService.remove(id);
    }
}
