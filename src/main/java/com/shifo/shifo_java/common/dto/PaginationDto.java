package com.shifo.shifo_java.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Getter
@NoArgsConstructor
public class PaginationDto {

    @Schema(
            description = "Page number",
            defaultValue = "1",
            example = "1",
            required = false
    )
    @Min(value = 1, message = "Page number must be at least 1")
    @Nullable
    private Integer page = 1;

    @Schema(
            description = "Number of items per page",
            defaultValue = "10",
            example = "10",
            required = false
    )
    @Min(value = 1, message = "Limit must be at least 1")
    @Nullable
    private Integer limit = 10;

    // We KEEP custom setters because they normalize null values
    public void setPage(Integer page) {
        this.page = (page == null ? 1 : page);
    }

    public void setLimit(Integer limit) {
        this.limit = (limit == null ? 10 : limit);
    }
}

