package br.com.drinkwater.usermanagement.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.drinkwater.usermanagement.constants.PersonalTestConstants.PERSONAL;
import static br.com.drinkwater.usermanagement.constants.PersonalTestConstants.PERSONAL_DTO;
import static org.assertj.core.api.Assertions.assertThat;

public final class PersonalMapperTest {

    private PersonalMapper mapper;

    @BeforeEach
    public void setUp() {
        this.mapper = new PersonalMapper();
    }

    @Test
    public void givenPersonalDTO_whenConvertingToEntity_thenShouldReturnPersonal() {
        var sut = this.mapper.toEntity(PERSONAL_DTO);

        assertThat(sut).isNotNull().isEqualTo(PERSONAL);
    }

    @Test
    public void givenNullPersonalDTO_whenConvertingToEntity_thenShouldReturnNull() {
        var sut = this.mapper.toEntity(null);

        assertThat(sut).isNull();
    }

    @Test
    public void givenPersonal_whenConvertingToDTO_thenShouldReturnPersonalDTO() {
        var sut = mapper.toDto(PERSONAL);

        assertThat(sut).isNotNull().isEqualTo(PERSONAL_DTO);
    }

    @Test
    public void givenNullPersonal_whenConvertingToDTO_thenShouldReturnNull() {
        var sut = this.mapper.toDto(null);

        assertThat(sut).isNull();
    }
}