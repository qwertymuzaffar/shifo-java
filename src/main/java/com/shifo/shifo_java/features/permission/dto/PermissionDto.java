package com.shifo.shifo_java.features.permission.dto;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PermissionDto {

    private Long id;

    private String slug;

    private String name;

    private String description;

    private Long parentId;

    private List<Long> childrenIds;

    private Instant createdAt;

    private Instant updatedAt;
}
