package com.shifo.shifo_java.features.permission.dto;

import java.util.List;

public record PermissionTreeDto(
        Long id,
        String slug,
        String name,
        List<PermissionTreeDto> children
) {}
