package br.com.drinkwater.hydrationtracking.mapper;

import static br.com.drinkwater.hydrationtracking.constants.WaterIntakeTestConstants.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

final class WaterIntakeMapperTest {

    private WaterIntakeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new WaterIntakeMapper();
    }

    @Test
    void givenNullDTO_whenToEntity_thenShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> mapper.toEntity(null, USER_ID))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Water intake DTO cannot be null");
    }

    @Test
    void givenNullUser_whenToEntity_thenShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> mapper.toEntity(WATER_INTAKE_DTO, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("User ID cannot be null");
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, -100})
    void givenInvalidId_whenToEntity_thenShouldThrowException(Long invalidId) {
        // When & Then
        assertThatThrownBy(() -> mapper.toEntity(WATER_INTAKE_DTO, USER_ID, invalidId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ID must be greater than zero");
    }

    @Test
    void givenWaterIntakeDTOAndUserAndId_whenToEntity_thenShouldReturnWaterIntakeWithProvidedId() {
        // When
        var sut = mapper.toEntity(WATER_INTAKE_DTO, USER_ID, WATER_INTAKE_ID);

        // Then
        assertThat(sut).isNotNull();
        assertThat(sut.getId()).isEqualTo(WATER_INTAKE_ID);
        assertThat(sut.getDateTimeUTC()).isEqualTo(DATE_TIME_UTC);
        assertThat(sut.getVolume()).isEqualTo(VOLUME);
        assertThat(sut.getVolumeUnit()).isEqualTo(VOLUME_UNIT);
        assertThat(sut.getUserId()).isEqualTo(USER_ID);
    }

    @Test
    void givenWaterIntakeDTOAndUser_whenToEntity_thenShouldReturnWaterIntakeWithNullId() {
        // When
        var sut = mapper.toEntity(WATER_INTAKE_DTO, USER_ID);

        // Then
        assertThat(sut).isNotNull();
        assertThat(sut.getId()).isNull();
        assertThat(sut.getDateTimeUTC()).isEqualTo(DATE_TIME_UTC);
        assertThat(sut.getVolume()).isEqualTo(VOLUME);
        assertThat(sut.getVolumeUnit()).isEqualTo(VOLUME_UNIT);
        assertThat(sut.getUserId()).isEqualTo(USER_ID);
    }

    @Test
    void givenNullWaterIntake_whenToDto_thenShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> mapper.toDto(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Water intake entity cannot be null");
    }

    @Test
    void givenWaterIntake_whenToDto_thenShouldReturnResponseDTO() {
        // When
        var sut = mapper.toDto(WATER_INTAKE);

        // Then
        assertThat(sut).isNotNull();
        assertThat(sut.id()).isEqualTo(WATER_INTAKE.getId());
        assertThat(sut.dateTimeUTC()).isEqualTo(WATER_INTAKE.getDateTimeUTC());
        assertThat(sut.volume()).isEqualTo(WATER_INTAKE.getVolume());
        assertThat(sut.volumeUnit()).isEqualTo(WATER_INTAKE.getVolumeUnit());
    }
}
