package com.shifo.shifo_java.features.patient.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreatePatientDtoTest {

    @Test
    void shouldAcceptNullAllergies() {
        CreatePatientDto dto = new CreatePatientDto();
        dto.setAllergies(null);

        assertThat(dto.getAllergies()).isNull();
    }

    @Test
    void shouldStoreAllergiesStringDirectly() {
        CreatePatientDto dto = new CreatePatientDto();
        dto.setAllergies("peanuts, milk");

        assertThat(dto.getAllergies()).isEqualTo("peanuts, milk");
    }

    @Test
    void shouldJoinAllergiesListWithCommaSpace() {
        CreatePatientDto dto = new CreatePatientDto();
        dto.setAllergies(List.of("peanuts", "milk"));

        assertThat(dto.getAllergies()).isEqualTo("peanuts, milk");
    }

    @Test
    void shouldRejectAllergiesOfOtherType() {
        CreatePatientDto dto = new CreatePatientDto();

        assertThatThrownBy(() -> dto.setAllergies(42))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid allergies format");
    }
}
