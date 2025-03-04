package br.com.drinkwater.usermanagement.mapper;

import br.com.drinkwater.usermanagement.exception.PhysicalMapperIllegalArgumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.drinkwater.usermanagement.constants.PhysicalTestConstants.PHYSICAL;
import static br.com.drinkwater.usermanagement.constants.PhysicalTestConstants.PHYSICAL_DTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public final class PhysicalMapperTest {

    private PhysicalMapper mapper;

    @BeforeEach
    public void setUp() {
        this.mapper = new PhysicalMapper();
    }

    @Test
    public void givenPhysicalDTO_whenConvertingToEntity_thenShouldReturnPhysical() {
        var sut = this.mapper.toEntity(PHYSICAL_DTO);

        assertThat(sut).isNotNull().isEqualTo(PHYSICAL);
    }

    @Test
    public void givenNullPhysicalDTO_whenConvertingToEntity_thenShouldThrowException() {
        assertThatThrownBy(() -> this.mapper.toEntity(null))
                .isInstanceOf(PhysicalMapperIllegalArgumentException.class)
                .hasMessage("Physical DTO cannot be null.");
    }

    @Test
    public void givenPhysical_whenConvertingToDTO_thenShouldReturnPhysicalDTO() {
        var sut = this.mapper.toDto(PHYSICAL);

        assertThat(sut).isNotNull().isEqualTo(PHYSICAL_DTO);
    }

    @Test
    public void givenNullPhysical_whenConvertingToDTO_thenShouldThrowException() {
        assertThatThrownBy(() -> this.mapper.toDto(null))
                .isInstanceOf(PhysicalMapperIllegalArgumentException.class)
                .hasMessage("Physical entity cannot be null.");
    }
}