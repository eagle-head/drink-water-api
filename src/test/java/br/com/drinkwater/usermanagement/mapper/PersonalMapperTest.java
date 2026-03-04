package br.com.drinkwater.usermanagement.mapper;

import static br.com.drinkwater.usermanagement.constants.PersonalTestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

final class PersonalMapperTest {

    private PersonalMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PersonalMapper();
    }

    @Test
    void givenPersonalDTO_whenConvertingToEntity_thenShouldReturnPersonal() {
        // When
        var sut = mapper.toEntity(PERSONAL_DTO);

        // Then
        assertThat(sut).isNotNull();
        assertThat(sut.getFirstName()).isEqualTo(PERSONAL_DTO.firstName());
        assertThat(sut.getLastName()).isEqualTo(PERSONAL_DTO.lastName());
        assertThat(sut.getBirthDate()).isEqualTo(PERSONAL_DTO.birthDate());
        assertThat(sut.getBiologicalSex()).isEqualTo(PERSONAL_DTO.biologicalSex());
    }

    @Test
    void givenNullPersonalDTO_whenConvertingToEntity_thenShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> mapper.toEntity(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Personal DTO cannot be null");
    }

    @Test
    void givenPersonal_whenConvertingToDTO_thenShouldReturnPersonalDTO() {
        // When
        var sut = mapper.toDto(PERSONAL);

        // Then
        assertThat(sut).isNotNull();
        assertThat(sut.firstName()).isEqualTo(PERSONAL.getFirstName());
        assertThat(sut.lastName()).isEqualTo(PERSONAL.getLastName());
        assertThat(sut.birthDate()).isEqualTo(PERSONAL.getBirthDate());
        assertThat(sut.biologicalSex()).isEqualTo(PERSONAL.getBiologicalSex());
    }

    @Test
    void givenNullPersonal_whenConvertingToDTO_thenShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> mapper.toDto(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Personal entity cannot be null");
    }
}
