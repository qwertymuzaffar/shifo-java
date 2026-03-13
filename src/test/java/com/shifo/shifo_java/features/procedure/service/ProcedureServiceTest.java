package com.shifo.shifo_java.features.procedure.service;

import com.shifo.shifo_java.features.procedure.Procedure;
import com.shifo.shifo_java.features.procedure.ProcedureRepository;
import com.shifo.shifo_java.features.procedure.ProcedureService;
import com.shifo.shifo_java.common.exceptions.NotFoundException;

import org.junit.jupiter.api.BeforeEach;
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

    @InjectMocks
    private ProcedureService procedureService;

    @Test
    void shouldReturnAllProcedures() {

        Procedure p1 = new Procedure();
        p1.setId(1L);
        p1.setName("X-Ray");

        Procedure p2 = new Procedure();
        p2.setId(2L);
        p2.setName("MRI");

        when(procedureRepository.findAll())
                .thenReturn(List.of(p1, p2));

        List<Procedure> result = procedureService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("X-Ray");

        verify(procedureRepository).findAll();
    }

    @Test
    void shouldReturnProcedureById() {

        Procedure procedure = new Procedure();
        procedure.setId(1L);
        procedure.setName("CT Scan");

        when(procedureRepository.findById(1L))
                .thenReturn(Optional.of(procedure));

        Procedure result = procedureService.findOne(1L);

        assertThat(result.getName()).isEqualTo("CT Scan");

        verify(procedureRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenProcedureNotFound() {

        when(procedureRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> procedureService.findOne(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Procedure");

        verify(procedureRepository).findById(1L);
    }

    @Test
    void shouldCreateProcedure() {

        Procedure savedProcedure = new Procedure();
        savedProcedure.setId(1L);
        savedProcedure.setName("Ultrasound");

        when(procedureRepository.save(any(Procedure.class)))
                .thenReturn(savedProcedure);

        Procedure result = procedureService.create("Ultrasound");

        assertThat(result.getName()).isEqualTo("Ultrasound");

        verify(procedureRepository).save(any(Procedure.class));
    }

    @Test
    void shouldUpdateProcedure() {

        Procedure procedure = new Procedure();
        procedure.setId(1L);
        procedure.setName("Old Name");

        when(procedureRepository.findById(1L))
                .thenReturn(Optional.of(procedure));

        when(procedureRepository.save(any(Procedure.class)))
                .thenReturn(procedure);

        Procedure result = procedureService.update(1L, "New Name");

        assertThat(result.getName()).isEqualTo("New Name");

        verify(procedureRepository).save(procedure);
    }

    @Test
    void shouldDeleteProcedure() {

        Procedure procedure = new Procedure();
        procedure.setId(1L);

        when(procedureRepository.findById(1L))
                .thenReturn(Optional.of(procedure));

        procedureService.remove(1L);

        verify(procedureRepository).delete(procedure);
    }
}
