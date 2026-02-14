package com.shifo.shifo_java.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponseDto<T> {

    private List<T> items;
    private int page;
    private int limit;
    private long total;
    private int totalPages;
}

