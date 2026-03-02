package com.shifo.shifo_java.features.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ListWithCountDto<T> {
    private List<T> data;
    private long count;
}
