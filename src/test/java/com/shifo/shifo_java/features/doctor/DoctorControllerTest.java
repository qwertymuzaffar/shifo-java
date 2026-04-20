package com.shifo.shifo_java.features.doctor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shifo.shifo_java.common.dto.PagedResponseDto;
import com.shifo.shifo_java.common.exceptions.NotFoundException;
import com.shifo.shifo_java.config.GlobalExceptionHandler;
import com.shifo.shifo_java.features.doctor.dto.CreateDoctorDto;
import com.shifo.shifo_java.features.doctor.dto.DoctorDto;
import com.shifo.shifo_java.features.doctor.dto.FilterDoctorDto;
import com.shifo.shifo_java.features.doctor.dto.UpdateDoctorDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DoctorControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private FakeDoctorService doctorService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        doctorService = new FakeDoctorService();
        mockMvc = MockMvcBuilders.standaloneSetup(new DoctorController(doctorService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldCreateDoctorAndReturn201() throws Exception {
        CreateDoctorDto request = new CreateDoctorDto();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setUsername("john_doc");
        request.setEmail("doc@example.com");
        request.setPassword("Abcd1234");
        request.setPhone("+992901234567");
        request.setSpecializationId(1L);

        Doctor created = Doctor.builder().id(3L).build();
        doctorService.createResponse = created;

        mockMvc.perform(post("/api/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3));
    }

    @Test
    void shouldReturnPagedDoctors() throws Exception {
        DoctorDto dto = new DoctorDto();
        dto.setId(1L);
        doctorService.findAllResponse = PagedResponseDto.<DoctorDto>builder()
                .items(List.of(dto))
                .page(1)
                .limit(10)
                .total(1L)
                .totalPages(1)
                .build();

        mockMvc.perform(get("/api/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.items[0].id").value(1));
    }

    @Test
    void shouldReturnDoctorById() throws Exception {
        DoctorDto dto = new DoctorDto();
        dto.setId(5L);
        doctorService.findOneResponse = dto;

        mockMvc.perform(get("/api/doctors/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void shouldReturn404WhenDoctorNotFound() throws Exception {
        doctorService.findOneException = new NotFoundException("doctors.errors.notFound");

        mockMvc.perform(get("/api/doctors/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRemoveDoctor() throws Exception {
        mockMvc.perform(delete("/api/doctors/7"))
                .andExpect(status().isOk());

        assertThat(doctorService.removedId).isEqualTo(7L);
    }

    @Test
    void shouldUpdateDoctor() throws Exception {
        UpdateDoctorDto request = new UpdateDoctorDto();
        request.setFirstName("Ivan");

        DoctorDto dto = new DoctorDto();
        dto.setId(4L);
        dto.setFirstName("Ivan");
        doctorService.updateResponse = dto;

        mockMvc.perform(patch("/api/doctors/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.firstName").value("Ivan"));
    }

    private static final class FakeDoctorService extends DoctorService {

        private Doctor createResponse;
        private PagedResponseDto<DoctorDto> findAllResponse;
        private DoctorDto findOneResponse;
        private RuntimeException findOneException;
        private DoctorDto updateResponse;
        private Long removedId;

        private FakeDoctorService() {
            super(null, null, null, null, null, null, null, null, null, null);
        }

        @Override
        public Doctor create(CreateDoctorDto dto) {
            return createResponse;
        }

        @Override
        public PagedResponseDto<DoctorDto> findAll(FilterDoctorDto filter) {
            return findAllResponse;
        }

        @Override
        public DoctorDto findOne(Long id) {
            if (findOneException != null) throw findOneException;
            return findOneResponse;
        }

        @Override
        public void remove(Long id) {
            this.removedId = id;
        }

        @Override
        public DoctorDto update(Long id, UpdateDoctorDto dto) {
            return updateResponse;
        }
    }
}
