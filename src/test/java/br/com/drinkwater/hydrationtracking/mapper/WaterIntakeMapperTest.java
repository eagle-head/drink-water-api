package br.com.drinkwater.hydrationtracking.mapper;

import br.com.drinkwater.hydrationtracking.exception.WaterIntakeMapperIllegalArgumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static br.com.drinkwater.hydrationtracking.constants.WaterIntakeTestConstants.*;
import static br.com.drinkwater.usermanagement.constants.UserTestConstants.USER;
import static org.assertj.core.api.Assertions.*;

public final class WaterIntakeMapperTest {

    private WaterIntakeMapper mapper;

    @BeforeEach
    void setUp() {
        this.mapper = new WaterIntakeMapper();
    }

    @Test
    void givenNullDTO_whenToEntity_thenShouldThrowException() {
        assertThatThrownBy(() -> this.mapper.toEntity(null, USER))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Water intake DTO cannot be null");
    }

    @Test
    void givenNullUser_whenToEntity_thenShouldThrowException() {
        assertThatThrownBy(() -> this.mapper.toEntity(WATER_INTAKE_DTO, null))
                .isInstanceOf(WaterIntakeMapperIllegalArgumentException.class)
                .hasMessage("User cannot be null");
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, -100})
    void givenInvalidId_whenToEntity_thenShouldThrowException(Long invalidId) {
        assertThatThrownBy(() -> this.mapper.toEntity(WATER_INTAKE_DTO, USER, invalidId))
                .isInstanceOf(WaterIntakeMapperIllegalArgumentException.class)
                .hasMessage("ID must be greater than zero");
    }

    @Test
    void givenWaterIntakeDTOAndUserAndId_whenToEntity_thenShouldReturnWaterIntakeWithProvidedId() {

        var sut = this.mapper.toEntity(WATER_INTAKE_DTO, USER, WATER_INTAKE_ID);

        assertThat(sut).isNotNull();
        assertThat(sut.getId()).isEqualTo(WATER_INTAKE_ID);
        assertThat(sut.getDateTimeUTC()).isEqualTo(DATE_TIME_UTC);
        assertThat(sut.getVolume()).isEqualTo(VOLUME);
        assertThat(sut.getVolumeUnit()).isEqualTo(VOLUME_UNIT);
        assertThat(sut.getUser()).isEqualTo(USER);
    }

    @Test
    void givenWaterIntakeDTOAndUser_whenToEntity_thenShouldReturnWaterIntakeWithNullId() {

        var sut = this.mapper.toEntity(WATER_INTAKE_DTO, USER);

        assertThat(sut).isNotNull();
        assertThat(sut.getId()).isNull();
        assertThat(sut.getDateTimeUTC()).isEqualTo(DATE_TIME_UTC);
        assertThat(sut.getVolume()).isEqualTo(VOLUME);
        assertThat(sut.getVolumeUnit()).isEqualTo(VOLUME_UNIT);
        assertThat(sut.getUser()).isEqualTo(USER);
    }

    @Test
    void givenNullWaterIntake_whenToResponseDTO_thenShouldThrowException() {
        assertThatThrownBy(() -> mapper.toResponseDTO(null))
                .isInstanceOf(WaterIntakeMapperIllegalArgumentException.class)
                .hasMessage("Water intake entity cannot be null");
    }

    @Test
    void givenWaterIntake_whenToResponseDTO_thenShouldReturnResponseDTO() {

        var sut = mapper.toResponseDTO(WATER_INTAKE);

        assertThat(sut).isNotNull();
        assertThat(sut.id()).isEqualTo(WATER_INTAKE.getId());
        assertThat(sut.dateTimeUTC()).isEqualTo(WATER_INTAKE.getDateTimeUTC());
        assertThat(sut.volume()).isEqualTo(WATER_INTAKE.getVolume());
        assertThat(sut.volumeUnit()).isEqualTo(WATER_INTAKE.getVolumeUnit());
    }
}
