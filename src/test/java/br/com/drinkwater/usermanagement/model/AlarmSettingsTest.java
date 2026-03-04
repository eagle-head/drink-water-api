package br.com.drinkwater.usermanagement.model;

import static br.com.drinkwater.usermanagement.constants.AlarmSettingsTestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalTime;
import org.junit.jupiter.api.Test;

final class AlarmSettingsTest {

    @Test
    void givenValidArguments_whenInstantiatedWithConstructor_thenShouldCreateValidInstance() {
        // When
        var settings =
                new AlarmSettings(
                        ALARM_SETTINGS.getGoal(),
                        ALARM_SETTINGS.getIntervalMinutes(),
                        ALARM_SETTINGS.getDailyStartTime(),
                        ALARM_SETTINGS.getDailyEndTime());

        // Then
        assertThat(settings).isNotNull();
        assertThat(settings.getGoal()).isEqualTo(ALARM_SETTINGS.getGoal());
        assertThat(settings.getIntervalMinutes()).isEqualTo(ALARM_SETTINGS.getIntervalMinutes());
        assertThat(settings.getDailyStartTime()).isEqualTo(ALARM_SETTINGS.getDailyStartTime());
        assertThat(settings.getDailyEndTime()).isEqualTo(ALARM_SETTINGS.getDailyEndTime());
    }

    @Test
    void givenValidArguments_whenWithUpdatedFields_thenFieldsAreUpdated() {
        // Given
        var settings = new AlarmSettings(1000, 20, START_TIME, END_TIME);

        // When
        var updated = settings.withUpdatedFields(2000, 30, LocalTime.of(9, 0), LocalTime.of(21, 0));

        // Then
        assertThat(updated.getGoal()).isEqualTo(2000);
        assertThat(updated.getIntervalMinutes()).isEqualTo(30);
        assertThat(updated.getDailyStartTime()).isEqualTo(LocalTime.of(9, 0));
        assertThat(updated.getDailyEndTime()).isEqualTo(LocalTime.of(21, 0));
    }

    @Test
    void givenNegativeGoal_whenInstantiatedWithConstructor_thenShouldThrowException() {
        // When & Then
        assertThatThrownBy(
                        () ->
                                new AlarmSettings(
                                        -1,
                                        ALARM_SETTINGS.getIntervalMinutes(),
                                        ALARM_SETTINGS.getDailyStartTime(),
                                        ALARM_SETTINGS.getDailyEndTime()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Goal must be greater than zero");
    }

    @Test
    void givenZeroGoal_whenInstantiatedWithConstructor_thenShouldThrowException() {
        // When & Then
        assertThatThrownBy(
                        () ->
                                new AlarmSettings(
                                        0,
                                        ALARM_SETTINGS.getIntervalMinutes(),
                                        ALARM_SETTINGS.getDailyStartTime(),
                                        ALARM_SETTINGS.getDailyEndTime()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Goal must be greater than zero");
    }

    @Test
    void givenNegativeInterval_whenInstantiatedWithConstructor_thenShouldThrowException() {
        assertThatThrownBy(
                        () ->
                                new AlarmSettings(
                                        ALARM_SETTINGS.getGoal(),
                                        -1,
                                        ALARM_SETTINGS.getDailyStartTime(),
                                        ALARM_SETTINGS.getDailyEndTime()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Interval minutes must be greater than zero");
    }

    @Test
    void givenZeroInterval_whenInstantiatedWithConstructor_thenShouldThrowException() {
        // When & Then
        assertThatThrownBy(
                        () ->
                                new AlarmSettings(
                                        ALARM_SETTINGS.getGoal(),
                                        0,
                                        ALARM_SETTINGS.getDailyStartTime(),
                                        ALARM_SETTINGS.getDailyEndTime()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Interval minutes must be greater than zero");
    }

    @Test
    void givenNullStartTime_whenInstantiatedWithConstructor_thenShouldThrowException() {
        assertThatThrownBy(
                        () ->
                                new AlarmSettings(
                                        ALARM_SETTINGS.getGoal(),
                                        ALARM_SETTINGS.getIntervalMinutes(),
                                        null,
                                        ALARM_SETTINGS.getDailyEndTime()))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Daily start time cannot be null");
    }

    @Test
    void givenNullEndTime_whenInstantiatedWithConstructor_thenShouldThrowException() {
        assertThatThrownBy(
                        () ->
                                new AlarmSettings(
                                        ALARM_SETTINGS.getGoal(),
                                        ALARM_SETTINGS.getIntervalMinutes(),
                                        ALARM_SETTINGS.getDailyStartTime(),
                                        null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Daily end time cannot be null");
    }

    @Test
    void givenEndTimeBeforeStartTime_whenInstantiatedWithConstructor_thenShouldThrowException() {
        // Given
        LocalTime invalidEnd = START_TIME.minusHours(1);

        // When & Then
        assertThatThrownBy(
                        () ->
                                new AlarmSettings(
                                        ALARM_SETTINGS.getGoal(),
                                        ALARM_SETTINGS.getIntervalMinutes(),
                                        START_TIME,
                                        invalidEnd))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Daily end time cannot be before daily start time");
    }

    @Test
    void givenNegativeGoal_whenWithUpdatedFields_thenShouldThrowException() {
        // Given
        var settings = new AlarmSettings(1000, 20, START_TIME, END_TIME);

        // When & Then
        assertThatThrownBy(() -> settings.withUpdatedFields(-1, 20, START_TIME, END_TIME))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Goal must be greater than zero");
    }

    @Test
    void givenEndTimeBeforeStartTime_whenWithUpdatedFields_thenShouldThrowException() {
        // Given
        var settings = new AlarmSettings(1000, 20, START_TIME, END_TIME);

        // When & Then
        assertThatThrownBy(
                        () ->
                                settings.withUpdatedFields(
                                        1000, 20, LocalTime.of(20, 0), LocalTime.of(8, 0)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Daily end time cannot be before daily start time");
    }

    @Test
    void givenSameInstance_whenEquals_thenShouldBeTrue() {
        // Given
        var settings =
                new AlarmSettings(
                        ALARM_SETTINGS.getGoal(),
                        ALARM_SETTINGS.getIntervalMinutes(),
                        ALARM_SETTINGS.getDailyStartTime(),
                        ALARM_SETTINGS.getDailyEndTime());

        // When & Then
        assertThat(settings.equals(settings)).isTrue();
    }

    @Test
    void givenAlarmSettingsAndNull_whenEquals_thenShouldReturnFalse() {
        // Given
        var settings =
                new AlarmSettings(
                        ALARM_SETTINGS.getGoal(),
                        ALARM_SETTINGS.getIntervalMinutes(),
                        ALARM_SETTINGS.getDailyStartTime(),
                        ALARM_SETTINGS.getDailyEndTime());

        // When & Then
        assertThat(settings.equals(null)).isFalse();
    }

    @Test
    void givenDifferentObjectType_whenEquals_thenShouldReturnFalse() {
        // Given
        var settings =
                new AlarmSettings(
                        ALARM_SETTINGS.getGoal(),
                        ALARM_SETTINGS.getIntervalMinutes(),
                        ALARM_SETTINGS.getDailyStartTime(),
                        ALARM_SETTINGS.getDailyEndTime());

        // When & Then
        assertThat(settings).isNotEqualTo("Not an AlarmSettings instance");
    }

    @Test
    void givenTransientSettings_whenEquals_thenShouldNotEqualOtherTransient() {
        // Given
        var as1 =
                new AlarmSettings(
                        ALARM_SETTINGS.getGoal(),
                        ALARM_SETTINGS.getIntervalMinutes(),
                        ALARM_SETTINGS.getDailyStartTime(),
                        ALARM_SETTINGS.getDailyEndTime());
        var as2 =
                new AlarmSettings(
                        ALARM_SETTINGS.getGoal(),
                        ALARM_SETTINGS.getIntervalMinutes(),
                        ALARM_SETTINGS.getDailyStartTime(),
                        ALARM_SETTINGS.getDailyEndTime());

        // When & Then
        assertThat(as1).isNotEqualTo(as2);
    }

    @Test
    void givenAlarmSettings_whenToString_thenShouldContainAllFields() {
        // Given
        var as =
                new AlarmSettings(
                        ALARM_SETTINGS.getGoal(),
                        ALARM_SETTINGS.getIntervalMinutes(),
                        ALARM_SETTINGS.getDailyStartTime(),
                        ALARM_SETTINGS.getDailyEndTime());

        // When
        String str = as.toString();

        // Then
        assertThat(str)
                .contains(String.valueOf(ALARM_SETTINGS.getGoal()))
                .contains(String.valueOf(ALARM_SETTINGS.getIntervalMinutes()))
                .contains(ALARM_SETTINGS.getDailyStartTime().toString())
                .contains(ALARM_SETTINGS.getDailyEndTime().toString());
    }

    @Test
    void givenAlarmSettings_whenWithId_thenReturnsNewInstanceWithId() {
        var settings = new AlarmSettings(1000, 20, START_TIME, END_TIME);

        var withId = settings.withId(42L);

        assertThat(withId.getId()).isEqualTo(42L);
        assertThat(withId.getGoal()).isEqualTo(1000);
        assertThat(withId.getIntervalMinutes()).isEqualTo(20);
    }

    @Test
    void givenAlarmSettings_whenWithIdNull_thenReturnsNewInstanceWithNullId() {
        var settings = new AlarmSettings(1L, 1000, 20, START_TIME, END_TIME);

        var withNullId = settings.withId(null);

        assertThat(withNullId.getId()).isNull();
    }

    @Test
    void givenTwoAlarmSettingsWithSameId_whenEquals_thenShouldBeTrue() {
        var as1 = new AlarmSettings(1L, 1000, 20, START_TIME, END_TIME);
        var as2 = new AlarmSettings(1L, 2000, 30, LocalTime.of(9, 0), LocalTime.of(21, 0));

        assertThat(as1).isEqualTo(as2);
        assertThat(as1.hashCode()).isEqualTo(as2.hashCode());
    }

    @Test
    void givenTwoAlarmSettingsWithDifferentIds_whenEquals_thenShouldBeFalse() {
        var as1 = new AlarmSettings(1L, 1000, 20, START_TIME, END_TIME);
        var as2 = new AlarmSettings(2L, 1000, 20, START_TIME, END_TIME);

        assertThat(as1).isNotEqualTo(as2);
    }
}
