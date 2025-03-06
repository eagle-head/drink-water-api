package br.com.drinkwater.usermanagement.model;

import static br.com.drinkwater.usermanagement.constants.UserTestConstants.USER;
import static br.com.drinkwater.usermanagement.constants.AlarmSettingsTestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.OffsetDateTime;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public final class AlarmSettingsTest {

    private static final Long ID = 1L;
    private static final int INVALID_GOAL = -1;
    private static final int INVALID_INTERVAL = 0;
    private static final OffsetDateTime INVALID_END_TIME = START_TIME.minusHours(1);

    @ParameterizedTest(name = "Testing setter/getter with value: {2}")
    @MethodSource("provideSetterGetterPairs")
    public <T> void testGetterSetter(final BiConsumer<AlarmSettings, T> setter,
                                     final Function<AlarmSettings, T> getter,
                                     final T expectedValue) {
        var alarmSettings = new AlarmSettings(
                ALARM_SETTINGS.getGoal(),
                ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(),
                ALARM_SETTINGS.getDailyEndTime()
        );
        setter.accept(alarmSettings, expectedValue);

        assertThat(getter.apply(alarmSettings)).isEqualTo(expectedValue);
    }

    public static Stream<Arguments> provideSetterGetterPairs() {
        return Stream.of(
                // Test for the 'id' property
                Arguments.of((BiConsumer<AlarmSettings, Long>) AlarmSettings::setId,
                        (Function<AlarmSettings, Long>) AlarmSettings::getId,
                        ID),
                // Tests for the 'goal' property
                Arguments.of((BiConsumer<AlarmSettings, Integer>) AlarmSettings::setGoal,
                        (Function<AlarmSettings, Integer>) AlarmSettings::getGoal,
                        ALARM_SETTINGS.getGoal()),
                Arguments.of((BiConsumer<AlarmSettings, Integer>) AlarmSettings::setGoal,
                        (Function<AlarmSettings, Integer>) AlarmSettings::getGoal,
                        INVALID_GOAL),
                // Tests for the 'intervalMinutes' property
                Arguments.of((BiConsumer<AlarmSettings, Integer>) AlarmSettings::setIntervalMinutes,
                        (Function<AlarmSettings, Integer>) AlarmSettings::getIntervalMinutes,
                        ALARM_SETTINGS.getIntervalMinutes()),
                Arguments.of((BiConsumer<AlarmSettings, Integer>) AlarmSettings::setIntervalMinutes,
                        (Function<AlarmSettings, Integer>) AlarmSettings::getIntervalMinutes,
                        INVALID_INTERVAL),
                // Test for the 'dailyStartTime' property
                Arguments.of((BiConsumer<AlarmSettings, OffsetDateTime>) AlarmSettings::setDailyStartTime,
                        (Function<AlarmSettings, OffsetDateTime>) AlarmSettings::getDailyStartTime,
                        START_TIME),
                // Tests for the 'dailyEndTime' property
                Arguments.of((BiConsumer<AlarmSettings, OffsetDateTime>) AlarmSettings::setDailyEndTime,
                        (Function<AlarmSettings, OffsetDateTime>) AlarmSettings::getDailyEndTime,
                        END_TIME),
                Arguments.of((BiConsumer<AlarmSettings, OffsetDateTime>) AlarmSettings::setDailyEndTime,
                        (Function<AlarmSettings, OffsetDateTime>) AlarmSettings::getDailyEndTime,
                        INVALID_END_TIME),
                // Test for the 'user' property
                Arguments.of((BiConsumer<AlarmSettings, User>) AlarmSettings::setUser,
                        (Function<AlarmSettings, User>) AlarmSettings::getUser,
                        USER)
        );
    }

    @Test
    public void givenValidArguments_whenInstantiatedWithConstructor_thenShouldCreateValidInstance() {
        var alarmSettings = new AlarmSettings(
                ALARM_SETTINGS.getGoal(),
                ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(),
                ALARM_SETTINGS.getDailyEndTime()
        );

        assertThat(alarmSettings).isNotNull();
        assertThat(alarmSettings.getGoal()).isEqualTo(ALARM_SETTINGS.getGoal());
        assertThat(alarmSettings.getIntervalMinutes()).isEqualTo(ALARM_SETTINGS.getIntervalMinutes());
        assertThat(alarmSettings.getDailyStartTime()).isEqualTo(ALARM_SETTINGS.getDailyStartTime());
        assertThat(alarmSettings.getDailyEndTime()).isEqualTo(ALARM_SETTINGS.getDailyEndTime());
    }

    @Test
    public void givenNegativeGoal_whenInstantiatedWithConstructor_thenShouldThrowException() {
        assertThatThrownBy(() -> new AlarmSettings(
                INVALID_GOAL,
                ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(),
                ALARM_SETTINGS.getDailyEndTime()
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Goal must be greater than zero");
    }

    @Test
    public void givenZeroGoal_whenInstantiatedWithConstructor_thenShouldThrowException() {
        assertThatThrownBy(() -> new AlarmSettings(
                0,
                ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(),
                ALARM_SETTINGS.getDailyEndTime()
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Goal must be greater than zero");
    }

    @Test
    public void givenNegativeInterval_whenInstantiatedWithConstructor_thenShouldThrowException() {
        assertThatThrownBy(() -> new AlarmSettings(
                ALARM_SETTINGS.getGoal(),
                -1,
                ALARM_SETTINGS.getDailyStartTime(),
                ALARM_SETTINGS.getDailyEndTime()
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Interval minutes must be greater than zero");
    }

    @Test
    public void givenZeroInterval_whenInstantiatedWithConstructor_thenShouldThrowException() {
        assertThatThrownBy(() -> new AlarmSettings(
                ALARM_SETTINGS.getGoal(),
                0,
                ALARM_SETTINGS.getDailyStartTime(),
                ALARM_SETTINGS.getDailyEndTime()
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Interval minutes must be greater than zero");
    }

    @Test
    public void givenNullStartTime_whenInstantiatedWithConstructor_thenShouldThrowException() {
        assertThatThrownBy(() -> new AlarmSettings(
                ALARM_SETTINGS.getGoal(),
                ALARM_SETTINGS.getIntervalMinutes(),
                null,
                ALARM_SETTINGS.getDailyEndTime()
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Daily start time cannot be null");
    }

    @Test
    public void givenNullEndTime_whenInstantiatedWithConstructor_thenShouldThrowException() {
        assertThatThrownBy(() -> new AlarmSettings(
                ALARM_SETTINGS.getGoal(),
                ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(),
                null
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Daily end time cannot be null");
    }

    @Test
    public void givenEndTimeBeforeStartTime_whenInstantiatedWithConstructor_thenShouldThrowException() {
        assertThatThrownBy(() -> new AlarmSettings(
                ALARM_SETTINGS.getGoal(),
                ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(),
                INVALID_END_TIME
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Daily end time cannot be before daily start time");
    }

    @Test
    public void givenSameInstance_whenEquals_thenShouldBeTrue() {
        var settings = new AlarmSettings(
                ALARM_SETTINGS.getGoal(),
                ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(),
                ALARM_SETTINGS.getDailyEndTime()
        );
        settings.setId(ID);
        settings.setUser(USER);

        assertThat(settings.equals(settings)).isTrue();
    }

    @Test
    public void givenTwoEqualAlarmSettings_whenCompared_thenShouldBeEqual() {
        var settings1 = new AlarmSettings(
                ALARM_SETTINGS.getGoal(),
                ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(),
                ALARM_SETTINGS.getDailyEndTime()
        );
        settings1.setId(ID);
        settings1.setUser(USER);

        var settings2 = new AlarmSettings(
                ALARM_SETTINGS.getGoal(),
                ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(),
                ALARM_SETTINGS.getDailyEndTime()
        );
        settings2.setId(ID);
        settings2.setUser(USER);

        assertThat(settings1).isEqualTo(settings2);
        assertThat(settings1.hashCode()).isEqualTo(settings2.hashCode());
    }

    @Test
    public void givenAlarmSettingsAndNull_whenEquals_thenShouldBeFalse() {
        var settings = new AlarmSettings(
                ALARM_SETTINGS.getGoal(),
                ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(),
                ALARM_SETTINGS.getDailyEndTime()
        );
        settings.setId(ID);
        settings.setUser(USER);

        assertThat(settings.equals(null)).isFalse();
    }

    @Test
    public void givenDifferentObjectType_whenEquals_thenShouldBeFalse() {
        var settings = new AlarmSettings(
                ALARM_SETTINGS.getGoal(),
                ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(),
                ALARM_SETTINGS.getDailyEndTime()
        );
        settings.setId(ID);
        settings.setUser(USER);

        final String differentType = "Not an AlarmSettings instance";

        assertThat(settings.equals(differentType)).isFalse();
    }

    @Test
    public void givenDifferentGoal_whenEquals_thenShouldReturnFalse() {
        var settings1 = new AlarmSettings(
                ALARM_SETTINGS.getGoal(),
                ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(),
                ALARM_SETTINGS.getDailyEndTime()
        );
        settings1.setId(ID);
        settings1.setUser(USER);

        var settings2 = new AlarmSettings(
                ALARM_SETTINGS.getGoal() + 1,
                ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(),
                ALARM_SETTINGS.getDailyEndTime()
        );
        settings2.setId(ID);
        settings2.setUser(USER);

        assertThat(settings1.equals(settings2)).isFalse();
    }

    @Test
    public void givenDifferentIntervalMinutes_whenEquals_thenShouldReturnFalse() {
        var settings1 = new AlarmSettings(
                ALARM_SETTINGS.getGoal(),
                ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(),
                ALARM_SETTINGS.getDailyEndTime()
        );
        settings1.setId(ID);
        settings1.setUser(USER);

        var settings2 = new AlarmSettings(
                ALARM_SETTINGS.getGoal(),
                ALARM_SETTINGS.getIntervalMinutes() + 10,
                ALARM_SETTINGS.getDailyStartTime(),
                ALARM_SETTINGS.getDailyEndTime()
        );
        settings2.setId(ID);
        settings2.setUser(USER);

        assertThat(settings1.equals(settings2)).isFalse();
    }

    @Test
    public void givenDifferentId_whenEquals_thenShouldReturnFalse() {
        var settings1 = new AlarmSettings(
                ALARM_SETTINGS.getGoal(),
                ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(),
                ALARM_SETTINGS.getDailyEndTime()
        );
        settings1.setId(ID);
        settings1.setUser(USER);

        var settings2 = new AlarmSettings(
                ALARM_SETTINGS.getGoal(),
                ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(),
                ALARM_SETTINGS.getDailyEndTime()
        );
        settings2.setId(999L);
        settings2.setUser(USER);

        assertThat(settings1.equals(settings2)).isFalse();
    }

    @Test
    public void givenDifferentDailyStartTime_whenEquals_thenShouldReturnFalse() {
        var settings1 = new AlarmSettings(
                ALARM_SETTINGS.getGoal(),
                ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(),
                ALARM_SETTINGS.getDailyEndTime()
        );
        settings1.setId(ID);
        settings1.setUser(USER);

        var settings2 = new AlarmSettings(
                ALARM_SETTINGS.getGoal(),
                ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime().plusHours(1),
                ALARM_SETTINGS.getDailyEndTime()
        );
        settings2.setId(ID);
        settings2.setUser(USER);

        assertThat(settings1.equals(settings2)).isFalse();
    }

    @Test
    public void givenDifferentDailyEndTime_whenEquals_thenShouldReturnFalse() {
        var settings1 = new AlarmSettings(
                ALARM_SETTINGS.getGoal(),
                ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(),
                ALARM_SETTINGS.getDailyEndTime()
        );
        settings1.setId(ID);
        settings1.setUser(USER);

        var settings2 = new AlarmSettings(
                ALARM_SETTINGS.getGoal(),
                ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(),
                ALARM_SETTINGS.getDailyEndTime().plusHours(1)
        );
        settings2.setId(ID);
        settings2.setUser(USER);

        assertThat(settings1.equals(settings2)).isFalse();
    }

    @Test
    public void givenDifferentUser_whenEquals_thenShouldReturnFalse() {
        var settings1 = new AlarmSettings(
                ALARM_SETTINGS.getGoal(),
                ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(),
                ALARM_SETTINGS.getDailyEndTime()
        );
        settings1.setId(ID);
        settings1.setUser(USER);

        var settings2 = new AlarmSettings(
                ALARM_SETTINGS.getGoal(),
                ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(),
                ALARM_SETTINGS.getDailyEndTime()
        );
        settings2.setId(ID);
        var differentUser = new User();
        differentUser.setId(12345L);
        settings2.setUser(differentUser);

        assertThat(settings1.equals(settings2)).isFalse();
    }

    @Test
    public void givenAlarmSettings_whenToString_thenShouldContainAllFields() {
        var settings = new AlarmSettings(
                ALARM_SETTINGS.getGoal(),
                ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(),
                ALARM_SETTINGS.getDailyEndTime()
        );
        settings.setId(ID);
        settings.setUser(USER);

        var toString = settings.toString();

        assertThat(toString)
                .contains(String.valueOf(ID))
                .contains(String.valueOf(ALARM_SETTINGS.getGoal()))
                .contains(String.valueOf(ALARM_SETTINGS.getIntervalMinutes()))
                .contains(ALARM_SETTINGS.getDailyStartTime().toString())
                .contains(ALARM_SETTINGS.getDailyEndTime().toString());
    }

    @Test
    public void givenNullUser_whenToString_thenShouldHandleNull() {
        var settings = new AlarmSettings(
                ALARM_SETTINGS.getGoal(),
                ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(),
                ALARM_SETTINGS.getDailyEndTime()
        );
        settings.setId(ID);
        settings.setUser(null);

        var toString = settings.toString();

        assertThat(toString)
                .contains("user=null")
                .doesNotContain("user.getId()");
    }

    @Test
    public void givenUserWithId_whenToString_thenShouldShowUserId() {
        var settings = new AlarmSettings(
                ALARM_SETTINGS.getGoal(),
                ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(),
                ALARM_SETTINGS.getDailyEndTime()
        );
        settings.setId(ID);
        var user = new User();
        user.setId(1L);
        settings.setUser(user);

        var toString = settings.toString();

        assertThat(toString).contains("user=1");
    }

    @Test
    public void givenDifferentFieldValues_whenHashCode_thenShouldReturnDifferentValues() {
        var settings1 = new AlarmSettings(
                ALARM_SETTINGS.getGoal(),
                ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(),
                ALARM_SETTINGS.getDailyEndTime()
        );
        settings1.setId(ID);
        settings1.setUser(USER);

        var settings2 = new AlarmSettings(
                ALARM_SETTINGS.getGoal() + 1,
                ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(),
                ALARM_SETTINGS.getDailyEndTime()
        );
        settings2.setId(ID);
        settings2.setUser(USER);

        assertThat(settings1.hashCode()).isNotEqualTo(settings2.hashCode());
    }
}