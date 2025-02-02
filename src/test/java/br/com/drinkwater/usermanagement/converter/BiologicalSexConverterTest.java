package br.com.drinkwater.usermanagement.converter;

import br.com.drinkwater.usermanagement.model.BiologicalSex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.drinkwater.usermanagement.constants.UserManagementTestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public final class BiologicalSexConverterTest {

    private BiologicalSexConverter converter;

    @BeforeEach
    public void setUp() {
        this.converter = new BiologicalSexConverter();
    }

    @Test
    public void givenMale_whenConvertToDatabaseColumn_thenReturnMaleCode() {
        assertThat(this.converter.convertToDatabaseColumn(BiologicalSex.MALE))
                .isEqualTo(VALID_BIOLOGICAL_SEX_MALE_CODE);
    }

    @Test
    public void givenFemale_whenConvertToDatabaseColumn_thenReturnFemaleCode() {
        assertThat(this.converter.convertToDatabaseColumn(BiologicalSex.FEMALE))
                .isEqualTo(VALID_BIOLOGICAL_SEX_FEMALE_CODE);
    }

    @Test
    public void givenNull_whenConvertToDatabaseColumn_thenReturnNull() {
        assertThat(this.converter.convertToDatabaseColumn(null))
                .isNull();
    }

    @Test
    public void givenMaleCode_whenConvertToEntityAttribute_thenReturnMale() {
        assertThat(this.converter.convertToEntityAttribute(VALID_BIOLOGICAL_SEX_MALE_CODE))
                .isEqualTo(BiologicalSex.MALE);
    }

    @Test
    public void givenFemaleCode_whenConvertToEntityAttribute_thenReturnFemale() {
        assertThat(this.converter.convertToEntityAttribute(VALID_BIOLOGICAL_SEX_FEMALE_CODE))
                .isEqualTo(BiologicalSex.FEMALE);
    }

    @Test
    public void givenNull_whenConvertToEntityAttribute_thenReturnNull() {
        assertThat(this.converter.convertToEntityAttribute(null))
                .isNull();
    }

    @Test
    public void givenInvalidCode_whenConvertToEntityAttribute_thenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> this.converter.convertToEntityAttribute(INVALID_BIOLOGICAL_SEX_CODE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid BiologicalSex code: " + INVALID_BIOLOGICAL_SEX_CODE);
    }
}