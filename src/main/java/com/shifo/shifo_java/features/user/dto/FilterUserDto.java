package com.shifo.shifo_java.features.user.dto;

import com.shifo.shifo_java.common.dto.PaginationDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
public class FilterUserDto extends PaginationDto {

    @Schema(description = "Search by name", required = false)
    @Nullable
    private String search;

    @Schema(description = "Search by role ID", example = "1", required = false)
    @Nullable
    private Integer roleId;

    @Schema(description = "Filter by active status", example = "true", required = false)
    @Nullable
    private Boolean isActive;

    @Schema(description = "Search by email", required = false)
    @Nullable
    private String email;
}

