package br.com.drinkwater.usermanagement.model;

import static br.com.drinkwater.usermanagement.constants.UserTestConstants.USER;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public final class AlarmSettingsTest {

    private static final Long ID = 1L;
    private static final int GOAL = 2000;
    private static final int INVALID_GOAL = -1;
    private static final int INTERVAL = 30;
    private static final int INVALID_INTERVAL = 0;
    private static final OffsetDateTime START_OF_DAY = OffsetDateTime
            .of(2024, 1, 1, 8, 0, 0, 0, ZoneOffset.UTC);
    private static final OffsetDateTime END_OF_DAY = OffsetDateTime
            .of(2024, 1, 1, 22, 0, 0, 0, ZoneOffset.UTC);
    private static final OffsetDateTime INVALID_END_TIME = START_OF_DAY.minusHours(1);

    @ParameterizedTest(name = "Testing setter/getter with value: {2}")
    @MethodSource("provideSetterGetterPairs")
    public <T> void testGetterSetter(final BiConsumer<AlarmSettings, T> setter,
                                     final Function<AlarmSettings, T> getter,
                                     final T expectedValue) {
        var alarmSettings = new AlarmSettings();
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
                        GOAL),
                Arguments.of((BiConsumer<AlarmSettings, Integer>) AlarmSettings::setGoal,
                        (Function<AlarmSettings, Integer>) AlarmSettings::getGoal,
                        INVALID_GOAL),
                // Tests for the 'intervalMinutes' property
                Arguments.of((BiConsumer<AlarmSettings, Integer>) AlarmSettings::setIntervalMinutes,
                        (Function<AlarmSettings, Integer>) AlarmSettings::getIntervalMinutes,
                        INTERVAL),
                Arguments.of((BiConsumer<AlarmSettings, Integer>) AlarmSettings::setIntervalMinutes,
                        (Function<AlarmSettings, Integer>) AlarmSettings::getIntervalMinutes,
                        INVALID_INTERVAL),
                // Test for the 'dailyStartTime' property
                Arguments.of((BiConsumer<AlarmSettings, OffsetDateTime>) AlarmSettings::setDailyStartTime,
                        (Function<AlarmSettings, OffsetDateTime>) AlarmSettings::getDailyStartTime,
                        START_OF_DAY),
                // Tests for the 'dailyEndTime' property
                Arguments.of((BiConsumer<AlarmSettings, OffsetDateTime>) AlarmSettings::setDailyEndTime,
                        (Function<AlarmSettings, OffsetDateTime>) AlarmSettings::getDailyEndTime,
                        END_OF_DAY),
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
    public void givenNewAlarmSettings_whenInstantiated_thenShouldNotBeNull() {
        var alarmSettings = new AlarmSettings();

        assertThat(alarmSettings).isNotNull();
    }

    private AlarmSettings createDefaultAlarmSettings() {
        var settings = new AlarmSettings();
        settings.setId(ID);
        settings.setGoal(GOAL);
        settings.setIntervalMinutes(INTERVAL);
        settings.setDailyStartTime(START_OF_DAY);
        settings.setDailyEndTime(END_OF_DAY);
        settings.setUser(USER);

        return settings;
    }

    @Test
    public void givenSameInstance_whenEquals_thenShouldBeTrue() {
        var settings = createDefaultAlarmSettings();

        assertThat(settings.equals(settings)).isTrue();
    }

    @Test
    public void givenTwoEqualAlarmSettings_whenCompared_thenShouldBeEqual() {
        var settings1 = createDefaultAlarmSettings();
        var settings2 = createDefaultAlarmSettings();

        assertThat(settings1).isEqualTo(settings2);
        assertThat(settings1.hashCode()).isEqualTo(settings2.hashCode());
    }

    @Test
    public void givenAlarmSettingsAndNull_whenEquals_thenShouldBeFalse() {
        var settings = createDefaultAlarmSettings();

        assertThat(settings.equals(null)).isFalse();
    }

    @Test
    public void givenDifferentObjectType_whenEquals_thenShouldBeFalse() {
        var settings = createDefaultAlarmSettings();
        final String differentType = "Not an AlarmSettings instance";

        assertThat(settings.equals(differentType)).isFalse();
    }

    @Test
    public void givenDifferentGoal_whenEquals_thenShouldReturnFalse() {
        var settings1 = createDefaultAlarmSettings();
        var settings2 = createDefaultAlarmSettings();
        settings2.setGoal(GOAL + 1); // Modify goal

        assertThat(settings1.equals(settings2)).isFalse();
    }

    @Test
    public void givenDifferentIntervalMinutes_whenEquals_thenShouldReturnFalse() {
        var settings1 = createDefaultAlarmSettings();
        var settings2 = createDefaultAlarmSettings();
        settings2.setIntervalMinutes(INTERVAL + 10); // Modify intervalMinutes

        assertThat(settings1.equals(settings2)).isFalse();
    }

    @Test
    public void givenDifferentId_whenEquals_thenShouldReturnFalse() {
        var settings1 = createDefaultAlarmSettings();
        var settings2 = createDefaultAlarmSettings();
        settings2.setId(999L); // Modify id

        assertThat(settings1.equals(settings2)).isFalse();
    }

    @Test
    public void givenDifferentDailyStartTime_whenEquals_thenShouldReturnFalse() {
        var settings1 = createDefaultAlarmSettings();
        var settings2 = createDefaultAlarmSettings();
        settings2.setDailyStartTime(START_OF_DAY.plusHours(1)); // Modify dailyStartTime

        assertThat(settings1.equals(settings2)).isFalse();
    }

    @Test
    public void givenDifferentDailyEndTime_whenEquals_thenShouldReturnFalse() {
        var settings1 = createDefaultAlarmSettings();
        var settings2 = createDefaultAlarmSettings();
        settings2.setDailyEndTime(END_OF_DAY.plusHours(1)); // Modify dailyEndTime

        assertThat(settings1.equals(settings2)).isFalse();
    }

    @Test
    public void givenDifferentUser_whenEquals_thenShouldReturnFalse() {
        var settings1 = createDefaultAlarmSettings();
        var settings2 = createDefaultAlarmSettings();
        var differentUser = new User();
        differentUser.setId(12345L); // A different user id
        settings2.setUser(differentUser);

        assertThat(settings1.equals(settings2)).isFalse();
    }

    @Test
    public void givenAlarmSettings_whenToString_thenShouldContainAllFields() {
        var settings = createDefaultAlarmSettings();
        var toString = settings.toString();

        assertThat(toString)
                .contains(String.valueOf(ID))
                .contains(String.valueOf(GOAL))
                .contains(String.valueOf(INTERVAL))
                .contains(START_OF_DAY.toString())
                .contains(END_OF_DAY.toString());
    }

    @Test
    public void givenNullUser_whenToString_thenShouldHandleNull() {
        var settings = createDefaultAlarmSettings();
        settings.setUser(null);
        var toString = settings.toString();

        assertThat(toString)
                .contains("user=null")
                .doesNotContain("user.getId()");
    }

    @Test
    public void givenUserWithId_whenToString_thenShouldShowUserId() {
        var settings = createDefaultAlarmSettings();
        var user = new User();
        user.setId(1L);
        settings.setUser(user);
        var toString = settings.toString();

        assertThat(toString).contains("user=1");
    }

    @Test
    public void givenDifferentFieldValues_whenHashCode_thenShouldReturnDifferentValues() {
        var settings1 = createDefaultAlarmSettings();
        var settings2 = createDefaultAlarmSettings();
        // Modify one field in settings2 to force a difference in hash code
        settings2.setGoal(GOAL + 1);

        assertThat(settings1.hashCode()).isNotEqualTo(settings2.hashCode());
    }
}
