package com.shifo.shifo_java.features.patient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shifo.shifo_java.common.dto.PagedResponseDto;
import com.shifo.shifo_java.common.exceptions.NotFoundException;
import com.shifo.shifo_java.config.GlobalExceptionHandler;
import com.shifo.shifo_java.features.patient.dto.CreatePatientDto;
import com.shifo.shifo_java.features.patient.dto.FilterPatientDto;
import com.shifo.shifo_java.features.patient.dto.PatientDto;
import com.shifo.shifo_java.features.patient.dto.UpdatePatientDto;
import com.shifo.shifo_java.features.patient.dto.UpdatePatientStatusDto;
import com.shifo.shifo_java.features.patient.enums.PatientSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PatientsControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private FakePatientsService patientsService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        patientsService = new FakePatientsService();
        mockMvc = MockMvcBuilders.standaloneSetup(new PatientsController(patientsService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldCreatePatientAndReturn201() throws Exception {
        CreatePatientDto request = new CreatePatientDto();
        request.setFullName("John Doe");
        request.setPhone("+992901234567");
        request.setBirthDate(LocalDate.of(1990, 1, 1));

        Patient created = new Patient();
        created.setId(1L);
        created.setFullName("John Doe");
        patientsService.createResponse = created;

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullName").value("John Doe"));
    }

    @Test
    void shouldReturnPagedPatients() throws Exception {
        PatientDto dto = new PatientDto();
        dto.setId(1L);
        dto.setFullName("Jane");
        patientsService.findAllResponse = PagedResponseDto.<PatientDto>builder()
                .items(List.of(dto))
                .page(1)
                .limit(10)
                .total(1L)
                .totalPages(1)
                .build();

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.items[0].id").value(1));
    }

    @Test
    void shouldForceTelegramSourceOnTelegramEndpoint() throws Exception {
        patientsService.findAllResponse = PagedResponseDto.<PatientDto>builder()
                .items(List.of())
                .total(0L)
                .build();

        mockMvc.perform(get("/api/patients/telegram"))
                .andExpect(status().isOk());

        assertThat(patientsService.lastFilter.getSource()).isEqualTo(PatientSource.TELEGRAM);
    }

    @Test
    void shouldReturnPatientById() throws Exception {
        PatientDto dto = new PatientDto();
        dto.setId(5L);
        dto.setFullName("Bob");
        patientsService.findOneResponse = dto;

        mockMvc.perform(get("/api/patients/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.fullName").value("Bob"));
    }

    @Test
    void shouldReturn404WhenPatientNotFound() throws Exception {
        patientsService.findOneException = new NotFoundException("Пациент не найден: id=99");

        mockMvc.perform(get("/api/patients/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdatePatient() throws Exception {
        UpdatePatientDto request = new UpdatePatientDto();
        request.setFullName("New Name");

        PatientDto dto = new PatientDto();
        dto.setId(3L);
        dto.setFullName("New Name");
        patientsService.updateResponse = dto;

        mockMvc.perform(patch("/api/patients/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("New Name"));
    }

    @Test
    void shouldDeactivatePatient() throws Exception {
        mockMvc.perform(delete("/api/patients/7"))
                .andExpect(status().isOk());

        assertThat(patientsService.deactivatedId).isEqualTo(7L);
    }

    @Test
    void shouldUpdatePatientStatus() throws Exception {
        UpdatePatientStatusDto body = new UpdatePatientStatusDto();
        body.setStatus(PatientStatus.INACTIVE);

        PatientDto dto = new PatientDto();
        dto.setId(8L);
        dto.setStatus(PatientStatus.INACTIVE);
        patientsService.updateStatusResponse = dto;

        mockMvc.perform(patch("/api/patients/8/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    private static final class FakePatientsService extends PatientsService {

        private Patient createResponse;
        private PagedResponseDto<PatientDto> findAllResponse;
        private PatientDto findOneResponse;
        private RuntimeException findOneException;
        private PatientDto updateResponse;
        private PatientDto updateStatusResponse;
        private Long deactivatedId;
        private FilterPatientDto lastFilter;

        private FakePatientsService() {
            super(null, null, null);
        }

        @Override
        public Patient create(CreatePatientDto dto) {
            return createResponse;
        }

        @Override
        public PagedResponseDto<PatientDto> findAll(FilterPatientDto filter) {
            this.lastFilter = filter;
            return findAllResponse;
        }

        @Override
        public PatientDto findOne(Long id) {
            if (findOneException != null) throw findOneException;
            return findOneResponse;
        }

        @Override
        public PatientDto update(Long id, UpdatePatientDto dto) {
            return updateResponse;
        }

        @Override
        public void deactivate(Long id) {
            this.deactivatedId = id;
        }

        @Override
        public PatientDto updateStatus(Long id, PatientStatus status) {
            return updateStatusResponse;
        }
    }
}
