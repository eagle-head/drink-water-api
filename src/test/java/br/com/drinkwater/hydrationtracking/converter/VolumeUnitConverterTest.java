package br.com.drinkwater.hydrationtracking.converter;

import br.com.drinkwater.hydrationtracking.model.VolumeUnit;
import br.com.drinkwater.hydrationtracking.model.VolumeUnitConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public final class VolumeUnitConverterTest {

    private static final VolumeUnit VALID_ML_ENTITY = VolumeUnit.ML;
    private static final Integer VALID_ML_DB = 1;
    private static final Integer INVALID_DB_CODE = 999;
    private static final VolumeUnit NULL_ENTITY = null;
    private static final Integer NULL_DB = null;

    private VolumeUnitConverter converter;

    @BeforeEach
    public void setUp() {
        this.converter = new VolumeUnitConverter();
    }

    @Test
    public void givenML_whenConvertToDatabaseColumn_thenReturnMLCode() {
        assertThat(this.converter.convertToDatabaseColumn(VALID_ML_ENTITY)).isEqualTo(VALID_ML_DB);
    }

    @Test
    public void givenNull_whenConvertToDatabaseColumn_thenReturnNull() {
        assertThat(this.converter.convertToDatabaseColumn(NULL_ENTITY)).isEqualTo(NULL_DB);
    }

    @Test
    public void givenMLCode_whenConvertToEntityAttribute_thenReturnML() {
        assertThat(this.converter.convertToEntityAttribute(VALID_ML_DB)).isEqualTo(VALID_ML_ENTITY);
    }

    @Test
    public void givenNull_whenConvertToEntityAttribute_thenReturnNull() {
        assertThat(this.converter.convertToEntityAttribute(NULL_DB)).isEqualTo(NULL_ENTITY);
    }

    @Test
    public void givenInvalidCode_whenConvertToEntityAttribute_thenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> this.converter.convertToEntityAttribute(INVALID_DB_CODE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid VolumeUnit code: " + INVALID_DB_CODE);
    }
}