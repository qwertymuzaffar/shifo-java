package com.shifo.shifo_java.features.procedure.mapper;

import com.shifo.shifo_java.features.procedure.Procedure;
import com.shifo.shifo_java.features.procedure.ProcedureMapper;
import com.shifo.shifo_java.features.procedure.dto.CreateProcedureDto;
import com.shifo.shifo_java.features.procedure.dto.ProcedureDto;
import com.shifo.shifo_java.features.procedure.dto.UpdateProcedureDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProcedureMapperTest {

    private final ProcedureMapper mapper = new ProcedureMapper();

    @Test
    void toDto_shouldReturnNull_whenProcedureIsNull() {
        assertNull(mapper.toDto(null));
    }

    @Test
    void toDto_shouldMapAllFields_andSetIsActiveTrue_whenDeletedAtIsNull() {
        Procedure procedure = new Procedure();
        procedure.setId(10L);
        procedure.setName("MRI");
        procedure.setDescription("Magnetic resonance imaging");
        procedure.setDuration(60);
        procedure.setCost(new BigDecimal("150.50"));
        procedure.setCreatedAt(Instant.parse("2026-03-01T10:00:00Z"));
        procedure.setUpdatedAt(Instant.parse("2026-03-02T10:00:00Z"));
        procedure.setDeletedAt(null);

        ProcedureDto dto = mapper.toDto(procedure);

        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals("MRI", dto.getName());
        assertEquals("Magnetic resonance imaging", dto.getDescription());
        assertEquals(60, dto.getDuration());
        assertEquals(new BigDecimal("150.50"), dto.getCost());
        assertTrue(dto.getIsActive());
        assertEquals(Instant.parse("2026-03-01T10:00:00Z"), dto.getCreatedAt());
        assertEquals(Instant.parse("2026-03-02T10:00:00Z"), dto.getUpdatedAt());
    }

    @Test
    void toDto_shouldSetIsActiveFalse_whenDeletedAtIsPresent() {
        Procedure procedure = new Procedure();
        procedure.setDeletedAt(Instant.parse("2026-03-03T10:00:00Z"));

        ProcedureDto dto = mapper.toDto(procedure);

        assertNotNull(dto);
        assertFalse(dto.getIsActive());
    }

    @Test
    void toEntity_shouldReturnNull_whenDtoIsNull() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    void toEntity_shouldMapName() {
        CreateProcedureDto dto = new CreateProcedureDto();
        dto.setName("X-Ray");

        Procedure procedure = mapper.toEntity(dto);

        assertNotNull(procedure);
        assertEquals("X-Ray", procedure.getName());
    }

    @Test
    void updateEntity_shouldDoNothing_whenDtoIsNull() {
        Procedure procedure = new Procedure();
        procedure.setName("Old name");

        mapper.updateEntity(null, procedure);

        assertEquals("Old name", procedure.getName());
    }

    @Test
    void updateEntity_shouldUpdateName() {
        Procedure procedure = new Procedure();
        procedure.setName("Old name");

        UpdateProcedureDto dto = new UpdateProcedureDto();
        dto.setName("Updated name");

        mapper.updateEntity(dto, procedure);

        assertEquals("Updated name", procedure.getName());
    }

    @Test
    void toDtoList_shouldReturnEmptyList_whenInputIsNullOrEmpty() {
        assertTrue(mapper.toDtoList(null).isEmpty());
        assertTrue(mapper.toDtoList(List.of()).isEmpty());
    }

    @Test
    void toDtoList_shouldMapAllItems() {
        Procedure first = new Procedure();
        first.setId(1L);
        first.setName("A");

        Procedure second = new Procedure();
        second.setId(2L);
        second.setName("B");

        List<ProcedureDto> result = mapper.toDtoList(List.of(first, second));

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("A", result.get(0).getName());
        assertEquals(2L, result.get(1).getId());
        assertEquals("B", result.get(1).getName());
    }
}
