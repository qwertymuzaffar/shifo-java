package com.shifo.shifo_java.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TotalRevenueDto {
    private BigDecimal total;
    private String growth;
}
