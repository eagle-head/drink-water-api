package br.com.drinkwater.hydrationtracking.model;

import static br.com.drinkwater.hydrationtracking.constants.WaterIntakeTestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

final class WaterIntakeTest {

    @Test
    void givenWaterIntake_whenGetId_thenReturnsCorrectId() {
        assertThat(WATER_INTAKE.getId()).isEqualTo(WATER_INTAKE_ID);
    }

    @Test
    void givenWaterIntake_whenGetDateTimeUTC_thenReturnsCorrectDateTimeUTC() {
        assertThat(WATER_INTAKE.getDateTimeUTC()).isEqualTo(DATE_TIME_UTC);
    }

    @Test
    void givenWaterIntake_whenGetVolume_thenReturnsCorrectVolume() {
        assertThat(WATER_INTAKE.getVolume()).isEqualTo(VOLUME);
    }

    @Test
    void givenWaterIntake_whenGetVolumeUnit_thenReturnsCorrectVolumeUnit() {
        assertThat(WATER_INTAKE.getVolumeUnit()).isEqualTo(VOLUME_UNIT);
    }

    @Test
    void givenWaterIntake_whenGetUserId_thenReturnsCorrectUserId() {
        assertThat(WATER_INTAKE.getUserId()).isEqualTo(USER_ID);
    }

    @Test
    void givenInvalidDateTimeUTC_whenCreateWaterIntake_thenThrowsNullPointerException() {
        // When & Then
        assertThatThrownBy(() -> new WaterIntake(null, VOLUME, VOLUME_UNIT, USER_ID))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Date and time cannot be null");
    }

    @Test
    void givenInvalidVolume_whenCreateWaterIntake_thenThrowsIllegalArgumentException() {
        // When & Then
        assertThatThrownBy(() -> new WaterIntake(DATE_TIME_UTC, 0, VOLUME_UNIT, USER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Volume must be greater than zero");

        assertThatThrownBy(() -> new WaterIntake(DATE_TIME_UTC, -1, VOLUME_UNIT, USER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Volume must be greater than zero");
    }

    @Test
    void givenInvalidVolumeUnit_whenCreateWaterIntake_thenThrowsNullPointerException() {
        assertThatThrownBy(() -> new WaterIntake(DATE_TIME_UTC, VOLUME, null, USER_ID))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Volume unit cannot be null");
    }

    @Test
    void givenInvalidUser_whenCreateWaterIntake_thenThrowsNullPointerException() {
        // When & Then
        assertThatThrownBy(() -> new WaterIntake(DATE_TIME_UTC, VOLUME, VOLUME_UNIT, (Long) null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("User ID cannot be null");
    }

    @Test
    void givenNullId_whenCreateWaterIntakeWithId_thenThrowsNullPointerException() {
        // When & Then
        assertThatThrownBy(() -> new WaterIntake(null, DATE_TIME_UTC, VOLUME, VOLUME_UNIT, USER_ID))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("ID cannot be null for existing entity");
    }

    @Test
    void givenZeroId_whenCreateWaterIntakeWithId_thenThrowsIllegalArgumentException() {
        // When & Then
        assertThatThrownBy(() -> new WaterIntake(0L, DATE_TIME_UTC, VOLUME, VOLUME_UNIT, USER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID must be greater than zero");
    }

    @Test
    void givenNegativeId_whenCreateWaterIntakeWithId_thenThrowsIllegalArgumentException() {
        // When & Then
        assertThatThrownBy(() -> new WaterIntake(-1L, DATE_TIME_UTC, VOLUME, VOLUME_UNIT, USER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID must be greater than zero");
    }

    @Test
    void givenZeroVolume_whenCreateWaterIntakeWithId_thenThrowsIllegalArgumentException() {
        assertThatThrownBy(
                        () ->
                                new WaterIntake(
                                        WATER_INTAKE_ID, DATE_TIME_UTC, 0, VOLUME_UNIT, USER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Volume must be greater than zero");
    }

    @Test
    void givenNegativeVolume_whenCreateWaterIntakeWithId_thenThrowsIllegalArgumentException() {
        assertThatThrownBy(
                        () ->
                                new WaterIntake(
                                        WATER_INTAKE_ID, DATE_TIME_UTC, -1, VOLUME_UNIT, USER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Volume must be greater than zero");
    }

    @Test
    void givenTwoWaterIntakesWithSameId_whenEquals_thenReturnsTrue() {
        // Given
        var waterIntake1 =
                new WaterIntake(WATER_INTAKE_ID, DATE_TIME_UTC, VOLUME, VOLUME_UNIT, USER_ID);
        var waterIntake2 =
                new WaterIntake(WATER_INTAKE_ID, DATE_TIME_UTC, VOLUME + 100, VOLUME_UNIT, USER_ID);

        // When & Then
        assertThat(waterIntake1).isEqualTo(waterIntake2);
        assertThat(waterIntake1.hashCode()).isEqualTo(waterIntake2.hashCode());
    }

    @Test
    void givenSameInstance_whenEquals_thenReturnsTrue() {
        assertThat(WATER_INTAKE.equals(WATER_INTAKE)).isTrue();
    }

    @Test
    void givenNullObject_whenEquals_thenReturnsFalse() {
        assertThat(WATER_INTAKE.equals(null)).isFalse();
    }

    @Test
    void givenDifferentType_whenEquals_thenReturnsFalse() {
        assertThat(WATER_INTAKE).isNotEqualTo("Not a WaterIntake");
    }

    @Test
    void givenDifferentId_whenEquals_thenReturnsFalse() {
        // Given
        var waterIntake1 =
                new WaterIntake(WATER_INTAKE_ID, DATE_TIME_UTC, VOLUME, VOLUME_UNIT, USER_ID);
        var waterIntake2 = new WaterIntake(999L, DATE_TIME_UTC, VOLUME, VOLUME_UNIT, USER_ID);

        // When & Then
        assertThat(waterIntake1).isNotEqualTo(waterIntake2);
    }

    @Test
    void givenTransientWaterIntakes_whenEquals_thenShouldNotBeEqual() {
        // Given
        var waterIntake1 = new WaterIntake(DATE_TIME_UTC, VOLUME, VOLUME_UNIT, USER_ID);
        var waterIntake2 = new WaterIntake(DATE_TIME_UTC, VOLUME, VOLUME_UNIT, USER_ID);

        // When & Then
        assertThat(waterIntake1).isNotEqualTo(waterIntake2);
    }

    @Test
    void givenWaterIntake_whenToString_thenIncludesAllFields() {
        // When
        String result = WATER_INTAKE.toString();

        // Then
        assertThat(result)
                .contains("id=" + WATER_INTAKE_ID)
                .contains("dateTimeUTC=" + DATE_TIME_UTC)
                .contains("volume=" + VOLUME)
                .contains("volumeUnit=" + VOLUME_UNIT)
                .contains("userId=" + USER_ID);
    }

    @Test
    void givenPersistenceCreatorConstructor_whenCreateWithNullId_thenCreatesTransientEntity() {
        var waterIntake =
                new WaterIntake(null, DATE_TIME_UTC, VOLUME, VolumeUnit.ML.getCode(), USER_ID);

        assertThat(waterIntake.getId()).isNull();
        assertThat(waterIntake.getDateTimeUTC()).isEqualTo(DATE_TIME_UTC);
        assertThat(waterIntake.getVolume()).isEqualTo(VOLUME);
        assertThat(waterIntake.getVolumeUnit()).isEqualTo(VOLUME_UNIT);
        assertThat(waterIntake.getUserId()).isEqualTo(USER_ID);
    }

    @Test
    void givenWaterIntakeWithNullId_whenEquals_thenReturnsFalseForOtherWithNullId() {
        var wi1 = new WaterIntake(null, DATE_TIME_UTC, VOLUME, VolumeUnit.ML.getCode(), USER_ID);
        var wi2 = new WaterIntake(null, DATE_TIME_UTC, VOLUME, VolumeUnit.ML.getCode(), USER_ID);

        assertThat(wi1).isNotEqualTo(wi2);
    }

    @Test
    void givenTransientWaterIntake_whenToString_thenIncludesNullId() {
        var waterIntake = new WaterIntake(DATE_TIME_UTC, VOLUME, VOLUME_UNIT, USER_ID);

        String result = waterIntake.toString();

        assertThat(result)
                .contains("id=null")
                .contains("dateTimeUTC=")
                .contains("volume=" + VOLUME)
                .contains("volumeUnit=" + VOLUME_UNIT)
                .contains("userId=" + USER_ID);
    }
}
