package com.shifo.shifo_java.features.procedure.service;

import com.shifo.shifo_java.features.procedure.Procedure;
import com.shifo.shifo_java.features.procedure.ProcedureMapper;
import com.shifo.shifo_java.features.procedure.ProcedureRepository;
import com.shifo.shifo_java.features.procedure.ProcedureService;

import com.shifo.shifo_java.features.procedure.dto.CreateProcedureDto;
import com.shifo.shifo_java.features.procedure.dto.ProcedureDto;
import com.shifo.shifo_java.features.procedure.dto.UpdateProcedureDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProcedureServiceTest {

    @Mock
    private ProcedureRepository procedureRepository;

    @Mock
    private ProcedureMapper procedureMapper;

    @InjectMocks
    private ProcedureService procedureService;

    @Test
    void shouldReturnAllProcedures() {

        Procedure procedure = new Procedure();
        procedure.setId(1L);
        procedure.setName("MRI");

        ProcedureDto dto = new ProcedureDto();
        dto.setId(1L);
        dto.setName("MRI");

        when(procedureRepository.findAll()).thenReturn(List.of(procedure));
        when(procedureMapper.toDto(procedure)).thenReturn(dto);

        List<ProcedureDto> result = procedureService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("MRI");

        verify(procedureRepository).findAll();
        verify(procedureMapper).toDto(procedure);
    }

    @Test
    void shouldReturnProcedureById() {

        Procedure procedure = new Procedure();
        procedure.setId(1L);
        procedure.setName("CT Scan");

        ProcedureDto dto = new ProcedureDto();
        dto.setId(1L);
        dto.setName("CT Scan");

        when(procedureRepository.findById(1L)).thenReturn(Optional.of(procedure));
        when(procedureMapper.toDto(procedure)).thenReturn(dto);

        ProcedureDto result = procedureService.findOne(1L);

        assertThat(result.getName()).isEqualTo("CT Scan");

        verify(procedureRepository).findById(1L);
        verify(procedureMapper).toDto(procedure);
    }

    @Test
    void shouldCreateProcedure() {

        CreateProcedureDto request = new CreateProcedureDto();
        request.setName("X-Ray");

        Procedure procedure = new Procedure();
        procedure.setName("X-Ray");

        Procedure saved = new Procedure();
        saved.setId(1L);
        saved.setName("X-Ray");

        ProcedureDto dto = new ProcedureDto();
        dto.setId(1L);
        dto.setName("X-Ray");

        when(procedureMapper.toEntity(request)).thenReturn(procedure);
        when(procedureRepository.save(procedure)).thenReturn(saved);
        when(procedureMapper.toDto(saved)).thenReturn(dto);

        ProcedureDto result = procedureService.create(request);

        assertThat(result.getId()).isEqualTo(1L);

        verify(procedureRepository).save(procedure);
    }

    @Test
    void shouldUpdateProcedure() {

        Procedure procedure = new Procedure();
        procedure.setId(1L);
        procedure.setName("Old");

        UpdateProcedureDto update = new UpdateProcedureDto();
        update.setName("Updated");

        ProcedureDto dto = new ProcedureDto();
        dto.setId(1L);
        dto.setName("Updated");

        when(procedureRepository.findById(1L)).thenReturn(Optional.of(procedure));
        when(procedureMapper.toDto(procedure)).thenReturn(dto);

        ProcedureDto result = procedureService.update(1L, update);

        assertThat(result.getName()).isEqualTo("Updated");

        verify(procedureMapper).updateEntity(update, procedure);
    }

    @Test
    void shouldDeleteProcedure() {

        Procedure procedure = new Procedure();
        procedure.setId(1L);

        when(procedureRepository.findById(1L)).thenReturn(Optional.of(procedure));

        procedureService.remove(1L);

        verify(procedureRepository).delete(procedure);
    }
}
