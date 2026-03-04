package br.com.drinkwater.usermanagement.mapper;

import static br.com.drinkwater.usermanagement.constants.PhysicalTestConstants.PHYSICAL;
import static br.com.drinkwater.usermanagement.constants.PhysicalTestConstants.PHYSICAL_DTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

final class PhysicalMapperTest {

    private PhysicalMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PhysicalMapper();
    }

    @Test
    void givenPhysicalDTO_whenConvertingToEntity_thenShouldReturnPhysical() {
        // When
        var sut = mapper.toEntity(PHYSICAL_DTO);

        // Then
        assertThat(sut).isNotNull().isEqualTo(PHYSICAL);
    }

    @Test
    void givenNullPhysicalDTO_whenConvertingToEntity_thenShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> mapper.toEntity(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Physical DTO cannot be null");
    }

    @Test
    void givenPhysical_whenConvertingToDTO_thenShouldReturnPhysicalDTO() {
        // When
        var sut = mapper.toDto(PHYSICAL);

        // Then
        assertThat(sut).isNotNull().isEqualTo(PHYSICAL_DTO);
    }

    @Test
    void givenNullPhysical_whenConvertingToDTO_thenShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> mapper.toDto(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Physical entity cannot be null");
    }
}
