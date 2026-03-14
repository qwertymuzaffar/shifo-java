package com.shifo.shifo_java.features.procedure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.shifo.shifo_java.features.procedure.ProcedureController;
import com.shifo.shifo_java.features.procedure.ProcedureService;
import com.shifo.shifo_java.features.procedure.dto.CreateProcedureDto;
import com.shifo.shifo_java.features.procedure.dto.ProcedureDto;
import com.shifo.shifo_java.features.procedure.dto.UpdateProcedureDto;
import com.shifo.shifo_java.security.JwtService;
import com.shifo.shifo_java.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ProcedureController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
class ProcedureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProcedureService procedureService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProcedureDto createProcedureDto() {
        return new ProcedureDto(
                1L,
                "MRI",
                "Magnetic resonance imaging",
                60,
                new BigDecimal("120.00"),
                true,
                Instant.now(),
                Instant.now()
        );
    }

    @Test
    void shouldReturnAllProcedures() throws Exception {

        List<ProcedureDto> procedures = List.of(createProcedureDto());

        when(procedureService.findAll()).thenReturn(procedures);

        mockMvc.perform(get("/api/procedures"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("MRI"))
                .andExpect(jsonPath("$[0].description").value("Magnetic resonance imaging"));
    }

    @Test
    void shouldReturnProcedureById() throws Exception {

        ProcedureDto procedure = createProcedureDto();

        when(procedureService.findOne(1L)).thenReturn(procedure);

        mockMvc.perform(get("/api/procedures/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("MRI"))
                .andExpect(jsonPath("$.duration").value(60));
    }

    @Test
    void shouldCreateProcedure() throws Exception {

        CreateProcedureDto request = new CreateProcedureDto("MRI");

        ProcedureDto response = createProcedureDto();

        when(procedureService.create(any(CreateProcedureDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/procedures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("MRI"));
    }

    @Test
    void shouldUpdateProcedure() throws Exception {

        UpdateProcedureDto request = new UpdateProcedureDto();
        request.setName("Updated MRI");

        ProcedureDto response = createProcedureDto();
        response.setName("Updated MRI");

        when(procedureService.update(eq(1L), any(UpdateProcedureDto.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/procedures/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated MRI"));
    }

    @Test
    void shouldDeleteProcedure() throws Exception {

        doNothing().when(procedureService).remove(1L);

        mockMvc.perform(delete("/api/procedures/1"))
                .andExpect(status().isNoContent());
    }
}
