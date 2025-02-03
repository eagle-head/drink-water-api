package br.com.drinkwater.usermanagement.converter;

import br.com.drinkwater.usermanagement.model.BiologicalSex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public final class BiologicalSexConverterTest {

    private static final BiologicalSex VALID_MALE_ENTITY = BiologicalSex.MALE;
    private static final BiologicalSex VALID_FEMALE_ENTITY = BiologicalSex.FEMALE;
    private static final Integer VALID_MALE_DB = 1;
    private static final Integer VALID_FEMALE_DB = 2;
    private static final Integer INVALID_DB_CODE = 999;
    private static final BiologicalSex NULL_ENTITY = null;
    private static final Integer NULL_DB = null;

    private BiologicalSexConverter converter;

    @BeforeEach
    public void setUp() {
        this.converter = new BiologicalSexConverter();
    }

    @Test
    public void givenMale_whenConvertToDatabaseColumn_thenReturnMaleCode() {
        assertThat(this.converter.convertToDatabaseColumn(VALID_MALE_ENTITY))
                .isEqualTo(VALID_MALE_DB);
    }

    @Test
    public void givenFemale_whenConvertToDatabaseColumn_thenReturnFemaleCode() {
        assertThat(this.converter.convertToDatabaseColumn(VALID_FEMALE_ENTITY))
                .isEqualTo(VALID_FEMALE_DB);
    }

    @Test
    public void givenNull_whenConvertToDatabaseColumn_thenReturnNull() {
        assertThat(this.converter.convertToDatabaseColumn(NULL_ENTITY))
                .isEqualTo(NULL_DB);
    }

    @Test
    public void givenMaleCode_whenConvertToEntityAttribute_thenReturnMale() {
        assertThat(this.converter.convertToEntityAttribute(VALID_MALE_DB))
                .isEqualTo(VALID_MALE_ENTITY);
    }

    @Test
    public void givenFemaleCode_whenConvertToEntityAttribute_thenReturnFemale() {
        assertThat(this.converter.convertToEntityAttribute(VALID_FEMALE_DB))
                .isEqualTo(VALID_FEMALE_ENTITY);
    }

    @Test
    public void givenNull_whenConvertToEntityAttribute_thenReturnNull() {
        assertThat(this.converter.convertToEntityAttribute(NULL_DB))
                .isEqualTo(NULL_ENTITY);
    }

    @Test
    public void givenInvalidCode_whenConvertToEntityAttribute_thenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> this.converter.convertToEntityAttribute(INVALID_DB_CODE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid BiologicalSex code: " + INVALID_DB_CODE);
    }
}