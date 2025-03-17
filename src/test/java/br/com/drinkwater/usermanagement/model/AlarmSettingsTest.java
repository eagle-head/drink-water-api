package br.com.drinkwater.usermanagement.model;

import static br.com.drinkwater.usermanagement.constants.UserTestConstants.USER;
import static br.com.drinkwater.usermanagement.constants.AlarmSettingsTestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalTime;
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

    @ParameterizedTest(name = "Test setter/getter with value: {2}")
    @MethodSource("provideSetterGetterPairs")
    public <T> void givenSetterGetterPair_whenSetting_thenValueIsRetrieved(BiConsumer<AlarmSettings, T> setter,
                                                                           Function<AlarmSettings, T> getter,
                                                                           T expectedValue) {
        AlarmSettings settings = new AlarmSettings(
                ALARM_SETTINGS.getGoal(),
                ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(),
                ALARM_SETTINGS.getDailyEndTime()
        );
        setter.accept(settings, expectedValue);
        assertThat(getter.apply(settings)).isEqualTo(expectedValue);
    }

    private static Stream<Arguments> provideSetterGetterPairs() {
        return Stream.of(
                Arguments.of((BiConsumer<AlarmSettings, Long>) AlarmSettings::setId,
                        (Function<AlarmSettings, Long>) AlarmSettings::getId,
                        ID),
                Arguments.of((BiConsumer<AlarmSettings, Integer>) AlarmSettings::setGoal,
                        (Function<AlarmSettings, Integer>) AlarmSettings::getGoal,
                        ALARM_SETTINGS.getGoal()),
                Arguments.of((BiConsumer<AlarmSettings, Integer>) AlarmSettings::setIntervalMinutes,
                        (Function<AlarmSettings, Integer>) AlarmSettings::getIntervalMinutes,
                        ALARM_SETTINGS.getIntervalMinutes()),
                Arguments.of((BiConsumer<AlarmSettings, LocalTime>) AlarmSettings::setDailyStartTime,
                        (Function<AlarmSettings, LocalTime>) AlarmSettings::getDailyStartTime,
                        START_TIME),
                Arguments.of((BiConsumer<AlarmSettings, LocalTime>) AlarmSettings::setDailyEndTime,
                        (Function<AlarmSettings, LocalTime>) AlarmSettings::getDailyEndTime,
                        END_TIME),
                Arguments.of((BiConsumer<AlarmSettings, User>) AlarmSettings::setUser,
                        (Function<AlarmSettings, User>) AlarmSettings::getUser,
                        USER)
        );
    }

    @Test
    public void givenValidArguments_whenInstantiatedWithConstructor_thenShouldCreateValidInstance() {
        AlarmSettings settings = new AlarmSettings(
                ALARM_SETTINGS.getGoal(),
                ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(),
                ALARM_SETTINGS.getDailyEndTime()
        );
        assertThat(settings).isNotNull();
        assertThat(settings.getGoal()).isEqualTo(ALARM_SETTINGS.getGoal());
        assertThat(settings.getIntervalMinutes()).isEqualTo(ALARM_SETTINGS.getIntervalMinutes());
        assertThat(settings.getDailyStartTime()).isEqualTo(ALARM_SETTINGS.getDailyStartTime());
        assertThat(settings.getDailyEndTime()).isEqualTo(ALARM_SETTINGS.getDailyEndTime());
    }

    @Test
    public void givenNegativeGoal_whenInstantiatedWithConstructor_thenShouldThrowException() {
        assertThatThrownBy(() -> new AlarmSettings(INVALID_GOAL, ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Goal must be greater than zero");
    }

    @Test
    public void givenZeroGoal_whenInstantiatedWithConstructor_thenShouldThrowException() {
        assertThatThrownBy(() -> new AlarmSettings(0, ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Goal must be greater than zero");
    }

    @Test
    public void givenNegativeInterval_whenInstantiatedWithConstructor_thenShouldThrowException() {
        assertThatThrownBy(() -> new AlarmSettings(ALARM_SETTINGS.getGoal(), -1,
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Interval minutes must be greater than zero");
    }

    @Test
    public void givenZeroInterval_whenInstantiatedWithConstructor_thenShouldThrowException() {
        assertThatThrownBy(() -> new AlarmSettings(ALARM_SETTINGS.getGoal(), 0,
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Interval minutes must be greater than zero");
    }

    @Test
    public void givenNullStartTime_whenInstantiatedWithConstructor_thenShouldThrowException() {
        assertThatThrownBy(() -> new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes(),
                null, ALARM_SETTINGS.getDailyEndTime()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Daily start time cannot be null");
    }

    @Test
    public void givenNullEndTime_whenInstantiatedWithConstructor_thenShouldThrowException() {
        assertThatThrownBy(() -> new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Daily end time cannot be null");
    }

    @Test
    public void givenEndTimeBeforeStartTime_whenInstantiatedWithConstructor_thenShouldThrowException() {
        LocalTime invalidEnd = START_TIME.minusHours(1);
        assertThatThrownBy(() -> new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes(),
                START_TIME, invalidEnd))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Daily end time cannot be before daily start time");
    }

    @Test
    public void givenSameInstance_whenEquals_thenShouldBeTrue() {
        AlarmSettings settings = new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime());
        settings.setId(ID);
        settings.setUser(USER);
        assertThat(settings.equals(settings)).isTrue();
    }

    @Test
    public void givenTwoEqualAlarmSettings_whenCompared_thenShouldBeEqual() {
        AlarmSettings as1 = new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime());
        as1.setId(ID);
        as1.setUser(USER);

        AlarmSettings as2 = new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime());
        as2.setId(ID);
        as2.setUser(USER);

        assertThat(as1).isEqualTo(as2);
        assertThat(as1.hashCode()).isEqualTo(as2.hashCode());
    }

    @Test
    public void givenAlarmSettingsAndNull_whenEquals_thenShouldReturnFalse() {
        AlarmSettings settings = new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime());
        settings.setId(ID);
        settings.setUser(USER);
        assertThat(settings.equals(null)).isFalse();
    }

    @Test
    public void givenDifferentObjectType_whenEquals_thenShouldReturnFalse() {
        AlarmSettings settings = new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime());
        settings.setId(ID);
        settings.setUser(USER);
        String notAnAlarmSettings = "Not an AlarmSettings instance";
        assertThat(settings.equals(notAnAlarmSettings)).isFalse();
    }

    @Test
    public void givenDifferentGoal_whenEquals_thenShouldReturnFalse() {
        AlarmSettings as1 = new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime());
        as1.setId(ID);
        as1.setUser(USER);

        AlarmSettings as2 = new AlarmSettings(ALARM_SETTINGS.getGoal() + 1, ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime());
        as2.setId(ID);
        as2.setUser(USER);

        assertThat(as1).isNotEqualTo(as2);
    }

    @Test
    public void givenDifferentIntervalMinutes_whenEquals_thenShouldReturnFalse() {
        AlarmSettings as1 = new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime());
        as1.setId(ID);
        as1.setUser(USER);

        AlarmSettings as2 = new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes() + 10,
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime());
        as2.setId(ID);
        as2.setUser(USER);

        assertThat(as1).isNotEqualTo(as2);
    }

    @Test
    public void givenDifferentId_whenEquals_thenShouldReturnFalse() {
        AlarmSettings as1 = new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime());
        as1.setId(ID);
        as1.setUser(USER);

        AlarmSettings as2 = new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime());
        as2.setId(999L);
        as2.setUser(USER);

        assertThat(as1).isNotEqualTo(as2);
    }

    @Test
    public void givenDifferentDailyStartTime_whenEquals_thenShouldReturnFalse() {
        AlarmSettings as1 = new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime());
        as1.setId(ID);
        as1.setUser(USER);

        AlarmSettings as2 = new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime().plusHours(1), ALARM_SETTINGS.getDailyEndTime());
        as2.setId(ID);
        as2.setUser(USER);

        assertThat(as1).isNotEqualTo(as2);
    }

    @Test
    public void givenDifferentDailyEndTime_whenEquals_thenShouldReturnFalse() {
        AlarmSettings as1 = new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime());
        as1.setId(ID);
        as1.setUser(USER);

        AlarmSettings as2 = new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime().plusHours(1));
        as2.setId(ID);
        as2.setUser(USER);

        assertThat(as1).isNotEqualTo(as2);
    }

    @Test
    public void givenDifferentUser_whenEquals_thenShouldReturnFalse() {
        AlarmSettings as1 = new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime());
        as1.setId(ID);
        as1.setUser(USER);

        AlarmSettings as2 = new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime());
        as2.setId(ID);
        User differentUser = new User();
        differentUser.setPublicId(java.util.UUID.randomUUID());
        as2.setUser(differentUser);

        assertThat(as1).isNotEqualTo(as2);
    }

    // Additional tests for hashCode differences
    @Test
    public void givenDifferentGoal_whenHashCode_thenShouldReturnDifferentValue() {
        AlarmSettings as1 = new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime());
        as1.setId(ID);
        as1.setUser(USER);

        AlarmSettings as2 = new AlarmSettings(ALARM_SETTINGS.getGoal() + 1, ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime());
        as2.setId(ID);
        as2.setUser(USER);

        assertThat(as1.hashCode()).isNotEqualTo(as2.hashCode());
    }

    @Test
    public void givenDifferentInterval_whenHashCode_thenShouldReturnDifferentValue() {
        AlarmSettings as1 = new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime());
        as1.setId(ID);
        as1.setUser(USER);

        AlarmSettings as2 = new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes() + 10,
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime());
        as2.setId(ID);
        as2.setUser(USER);

        assertThat(as1.hashCode()).isNotEqualTo(as2.hashCode());
    }

    @Test
    public void givenDifferentDailyStartTime_whenHashCode_thenShouldReturnDifferentValue() {
        AlarmSettings as1 = new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime());
        as1.setId(ID);
        as1.setUser(USER);

        AlarmSettings as2 = new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime().plusMinutes(30), ALARM_SETTINGS.getDailyEndTime());
        as2.setId(ID);
        as2.setUser(USER);

        assertThat(as1.hashCode()).isNotEqualTo(as2.hashCode());
    }

    @Test
    public void givenDifferentDailyEndTime_whenHashCode_thenShouldReturnDifferentValue() {
        AlarmSettings as1 = new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime());
        as1.setId(ID);
        as1.setUser(USER);

        AlarmSettings as2 = new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime().plusMinutes(30));
        as2.setId(ID);
        as2.setUser(USER);

        assertThat(as1.hashCode()).isNotEqualTo(as2.hashCode());
    }

    @Test
    public void givenDifferentUserPublicId_whenHashCode_thenShouldReturnDifferentValue() {
        AlarmSettings as1 = new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime());
        as1.setId(ID);
        as1.setUser(USER);

        AlarmSettings as2 = new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime());
        as2.setId(ID);
        User differentUser = new User();
        differentUser.setPublicId(java.util.UUID.randomUUID());
        as2.setUser(differentUser);

        assertThat(as1.hashCode()).isNotEqualTo(as2.hashCode());
    }

    @Test
    public void givenAlarmSettings_whenToString_thenShouldContainAllFields() {
        AlarmSettings as = new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime());
        as.setId(ID);
        as.setUser(USER);
        String str = as.toString();
        assertThat(str)
                .contains(String.valueOf(ID))
                .contains(String.valueOf(ALARM_SETTINGS.getGoal()))
                .contains(String.valueOf(ALARM_SETTINGS.getIntervalMinutes()))
                .contains(ALARM_SETTINGS.getDailyStartTime().toString())
                .contains(ALARM_SETTINGS.getDailyEndTime().toString());
    }

    @Test
    public void givenNullUser_whenToString_thenShouldHandleNull() {
        AlarmSettings as = new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime());
        as.setId(ID);
        as.setUser(null);
        String str = as.toString();
        assertThat(str).contains("user=null").doesNotContain("user.getId()");
    }

    @Test
    public void givenUserWithId_whenToString_thenShouldShowUserId() {
        AlarmSettings as = new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime());
        as.setId(ID);
        User user = new User();
        user.setId(1L);
        as.setUser(user);
        String str = as.toString();
        assertThat(str).contains("user=1");
    }

    @Test
    public void givenBothUsersNull_whenEquals_thenShouldReturnTrue() {
        AlarmSettings as1 = new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime());
        as1.setId(ID);
        as1.setUser(null);

        AlarmSettings as2 = new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime());
        as2.setId(ID);
        as2.setUser(null);

        assertThat(as1).isEqualTo(as2);
        assertThat(as1.hashCode()).isEqualTo(as2.hashCode());
    }

    @Test
    public void givenOneUserNull_whenEquals_thenShouldReturnFalse() {
        AlarmSettings as1 = new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime());
        as1.setId(ID);
        as1.setUser(null);

        AlarmSettings as2 = new AlarmSettings(ALARM_SETTINGS.getGoal(), ALARM_SETTINGS.getIntervalMinutes(),
                ALARM_SETTINGS.getDailyStartTime(), ALARM_SETTINGS.getDailyEndTime());
        as2.setId(ID);
        as2.setUser(USER);

        assertThat(as1).isNotEqualTo(as2);
        assertThat(as2).isNotEqualTo(as1);
    }
}
