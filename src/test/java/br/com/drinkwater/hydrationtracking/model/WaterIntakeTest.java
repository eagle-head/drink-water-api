package br.com.drinkwater.hydrationtracking.model;

import static br.com.drinkwater.hydrationtracking.constants.WaterIntakeTestConstants.*;
import static br.com.drinkwater.usermanagement.constants.UserTestConstants.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.drinkwater.usermanagement.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public final class WaterIntakeTest {

    @Test
    public void givenWaterIntake_whenGetId_thenReturnsCorrectId() {
        assertThat(WATER_INTAKE.getId()).isEqualTo(WATER_INTAKE_ID);
    }

    @Test
    public void givenWaterIntake_whenGetDateTimeUTC_thenReturnsCorrectDateTimeUTC() {
        assertThat(WATER_INTAKE.getDateTimeUTC()).isEqualTo(DATE_TIME_UTC);
    }

    @Test
    public void givenWaterIntake_whenGetVolume_thenReturnsCorrectVolume() {
        assertThat(WATER_INTAKE.getVolume()).isEqualTo(VOLUME);
    }

    @Test
    public void givenWaterIntake_whenGetVolumeUnit_thenReturnsCorrectVolumeUnit() {
        assertThat(WATER_INTAKE.getVolumeUnit()).isEqualTo(VOLUME_UNIT);
    }

    @Test
    public void givenWaterIntake_whenGetUser_thenReturnsCorrectUser() {
        assertThat(WATER_INTAKE.getUser()).isEqualTo(USER);
    }

    @Test
    public void givenInvalidDateTimeUTC_whenCreateWaterIntake_thenThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> new WaterIntake(null, VOLUME, VOLUME_UNIT, USER))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Date and time cannot be null");
    }

    @Test
    public void givenInvalidVolume_whenCreateWaterIntake_thenThrowsIllegalArgumentException() {
        // Test zero volume
        assertThatThrownBy(() -> new WaterIntake(DATE_TIME_UTC, 0, VOLUME_UNIT, USER))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Volume must be greater than zero");

        // Test negative volume
        assertThatThrownBy(() -> new WaterIntake(DATE_TIME_UTC, -1, VOLUME_UNIT, USER))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Volume must be greater than zero");
    }

    @Test
    public void givenInvalidVolumeUnit_whenCreateWaterIntake_thenThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> new WaterIntake(DATE_TIME_UTC, VOLUME, null, USER))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Volume unit cannot be null");
    }

    @Test
    public void givenInvalidUser_whenCreateWaterIntake_thenThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> new WaterIntake(DATE_TIME_UTC, VOLUME, VOLUME_UNIT, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User cannot be null");
    }

    @Test
    public void givenNullId_whenCreateWaterIntakeWithId_thenThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> new WaterIntake(null, DATE_TIME_UTC, VOLUME, VOLUME_UNIT, USER))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID cannot be null for existing entity");
    }

    @Test
    public void givenZeroId_whenCreateWaterIntakeWithId_thenThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> new WaterIntake(0L, DATE_TIME_UTC, VOLUME, VOLUME_UNIT, USER))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID must be greater than zero");
    }

    @Test
    public void givenNegativeId_whenCreateWaterIntakeWithId_thenThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> new WaterIntake(-1L, DATE_TIME_UTC, VOLUME, VOLUME_UNIT, USER))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID must be greater than zero");
    }

    @Test
    public void givenTwoEqualWaterIntakes_whenEquals_thenReturnsTrue() {
        // Given
        WaterIntake waterIntake1 = new WaterIntake(WATER_INTAKE_ID, DATE_TIME_UTC, VOLUME, VOLUME_UNIT, USER);
        WaterIntake waterIntake2 = new WaterIntake(WATER_INTAKE_ID, DATE_TIME_UTC, VOLUME, VOLUME_UNIT, USER);

        // Then
        assertThat(waterIntake1).isEqualTo(waterIntake2);
        assertThat(waterIntake1.hashCode()).isEqualTo(waterIntake2.hashCode());
    }

    @Test
    public void givenSameInstance_whenEquals_thenReturnsTrue() {
        // Then
        assertThat(WATER_INTAKE.equals(WATER_INTAKE)).isTrue();
    }

    @Test
    public void givenNullObject_whenEquals_thenReturnsFalse() {
        // Then
        assertThat(WATER_INTAKE.equals(null)).isFalse();
    }

    @Test
    public void givenDifferentType_whenEquals_thenReturnsFalse() {
        // Given
        String notAnIntake = "Not a WaterIntake";

        // Then
        assertThat(WATER_INTAKE.equals(notAnIntake)).isFalse();
    }

    @ParameterizedTest(name = "Given different {0} when equals then returns false")
    @ValueSource(strings = {"id", "dateTimeUTC", "volume", "volumeUnit", "user"})
    public void givenDifferentField_whenEquals_thenReturnsFalse(String fieldToChange) {
        // Given
        var waterIntake1 = new WaterIntake(WATER_INTAKE_ID, DATE_TIME_UTC, VOLUME, VOLUME_UNIT, USER);
        var waterIntake2 = new WaterIntake(WATER_INTAKE_ID, DATE_TIME_UTC, VOLUME, VOLUME_UNIT, USER);

        // When we change one field in the second object
        switch (fieldToChange) {
            case "id":
                waterIntake2.setId(999L);
                break;
            case "dateTimeUTC":
                waterIntake2.setDateTimeUTC(DATE_TIME_UTC.plusDays(1));
                break;
            case "volume":
                waterIntake2.setVolume(VOLUME + 100);
                break;
            case "volumeUnit":
                waterIntake2.setVolumeUnit(null);
                break;
            case "user":
                // Cria um usuário diferente utilizando o construtor de 5 argumentos e define o ID posteriormente
                User differentUser = new User(USER.getPublicId(), USER.getEmail(), USER.getPersonal(), USER.getPhysical(), USER.getSettings());
                differentUser.setId(999L);
                waterIntake2.setUser(differentUser);
                break;
        }

        // Then
        assertThat(waterIntake1).isNotEqualTo(waterIntake2);
        assertThat(waterIntake1.hashCode()).isNotEqualTo(waterIntake2.hashCode());
    }

    @Test
    public void givenNullUserInWaterIntake_whenHashCode_thenThrowsNullPointerException() {
        // Given a water intake with null user
        WaterIntake waterIntake = new WaterIntake(WATER_INTAKE_ID, DATE_TIME_UTC, VOLUME, VOLUME_UNIT, USER);
        waterIntake.setUser(null);

        // When/Then: hashCode should handle null user
        assertThatThrownBy(waterIntake::hashCode)
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void givenWaterIntake_whenToString_thenIncludesAllFields() {
        // When
        String result = WATER_INTAKE.toString();

        // Then
        assertThat(result)
                .contains("id=" + WATER_INTAKE_ID)
                .contains("dateTimeUTC=" + DATE_TIME_UTC)
                .contains("volume=" + VOLUME)
                .contains("volumeUnit=" + VOLUME_UNIT)
                .contains("userId=" + USER.getId());
    }

    @Test
    public void givenNullUserInWaterIntake_whenToString_thenContainsNullUserId() {
        // Given
        WaterIntake waterIntake = new WaterIntake(WATER_INTAKE_ID, DATE_TIME_UTC, VOLUME, VOLUME_UNIT, USER);
        waterIntake.setUser(null);

        // When
        String result = waterIntake.toString();

        // Then
        assertThat(result).contains("userId=null");
    }

    @Test
    public void givenUserPresent_whenToString_thenUsesUserGetId() {
        // Given
        User user = new User(USER.getPublicId(), USER.getEmail(), USER.getPersonal(), USER.getPhysical(), USER.getSettings());
        user.setId(101L);
        WaterIntake waterIntake = new WaterIntake(WATER_INTAKE_ID, DATE_TIME_UTC, VOLUME, VOLUME_UNIT, user);

        // When
        String result = waterIntake.toString();

        // Then
        assertThat(result)
                .contains("userId=101")
                .doesNotContain("userId=null");
    }
}