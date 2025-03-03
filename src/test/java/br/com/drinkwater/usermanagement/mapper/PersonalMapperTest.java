package br.com.drinkwater.usermanagement.mapper;

import br.com.drinkwater.usermanagement.exception.PersonalMapperIllegalArgumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.drinkwater.usermanagement.constants.PersonalTestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public final class PersonalMapperTest {

    private PersonalMapper mapper;

    @BeforeEach
    public void setUp() {
        this.mapper = new PersonalMapper();
    }

    @Test
    public void givenPersonalDTO_whenConvertingToEntity_thenShouldReturnPersonal() {
        var sut = this.mapper.toEntity(PERSONAL_DTO);

        assertThat(sut).isNotNull();
        assertThat(sut.getFirstName()).isEqualTo(PERSONAL_DTO.firstName());
        assertThat(sut.getLastName()).isEqualTo(PERSONAL_DTO.lastName());
        assertThat(sut.getBirthDate()).isEqualTo(PERSONAL_DTO.birthDate());
        assertThat(sut.getBiologicalSex()).isEqualTo(PERSONAL_DTO.biologicalSex());
    }

    @Test
    public void givenNullPersonalDTO_whenConvertingToEntity_thenShouldThrowException() {
        assertThatThrownBy(() -> this.mapper.toEntity(null))
                .isInstanceOf(PersonalMapperIllegalArgumentException.class)
                .hasMessage("Personal DTO cannot be null.");
    }

    @Test
    public void givenPersonal_whenConvertingToDTO_thenShouldReturnPersonalDTO() {
        var sut = mapper.toDto(PERSONAL);

        assertThat(sut).isNotNull();
        assertThat(sut.firstName()).isEqualTo(PERSONAL.getFirstName());
        assertThat(sut.lastName()).isEqualTo(PERSONAL.getLastName());
        assertThat(sut.birthDate()).isEqualTo(PERSONAL.getBirthDate());
        assertThat(sut.biologicalSex()).isEqualTo(PERSONAL.getBiologicalSex());
    }

    @Test
    public void givenNullPersonal_whenConvertingToDTO_thenShouldThrowException() {
        assertThatThrownBy(() -> this.mapper.toDto(null))
                .isInstanceOf(PersonalMapperIllegalArgumentException.class)
                .hasMessage("Personal entity cannot be null.");
    }
}