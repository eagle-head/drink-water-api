package br.com.drinkwater.usermanagement.model;

import br.com.drinkwater.usermanagement.constants.UserManagementTestData;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AlarmSettingsTest {

    @Test
    public void givenAlarmSettings_whenSetId_thenIdShouldBeUpdated() {
        AlarmSettings alarmSettings = new AlarmSettings();
        alarmSettings.setId(UserManagementTestData.DEFAULT_ID);

        assertThat(alarmSettings.getId())
                .isEqualTo(UserManagementTestData.DEFAULT_ID);
    }

    @Test
    public void givenAlarmSettings_whenSetGoal_thenGoalShouldBeUpdated() {
        AlarmSettings alarmSettings = new AlarmSettings();
        alarmSettings.setGoal(UserManagementTestData.DEFAULT_GOAL);

        assertThat(alarmSettings.getGoal())
                .isEqualTo(UserManagementTestData.DEFAULT_GOAL);
    }

    @Test
    public void givenAlarmSettings_whenSetIntervalMinutes_thenIntervalMinutesShouldBeUpdated() {
        AlarmSettings alarmSettings = new AlarmSettings();
        alarmSettings.setIntervalMinutes(UserManagementTestData.DEFAULT_INTERVAL);

        assertThat(alarmSettings.getIntervalMinutes())
                .isEqualTo(UserManagementTestData.DEFAULT_INTERVAL);
    }

    @Test
    public void givenAlarmSettings_whenSetDailyStartTime_thenDailyStartTimeShouldBeUpdated() {
        AlarmSettings alarmSettings = new AlarmSettings();
        alarmSettings.setDailyStartTime(UserManagementTestData.START_OF_DAY);

        assertThat(alarmSettings.getDailyStartTime())
                .isEqualTo(UserManagementTestData.START_OF_DAY);
    }

    @Test
    public void givenAlarmSettings_whenSetDailyEndTime_thenDailyEndTimeShouldBeUpdated() {
        AlarmSettings alarmSettings = new AlarmSettings();
        alarmSettings.setDailyEndTime(UserManagementTestData.END_OF_DAY);

        assertThat(alarmSettings.getDailyEndTime())
                .isEqualTo(UserManagementTestData.END_OF_DAY);
    }

    @Test
    public void givenAlarmSettings_whenSetUser_thenUserShouldBeUpdated() {
        AlarmSettings alarmSettings = new AlarmSettings();
        User user = UserManagementTestData.DEFAULT_USER;
        alarmSettings.setUser(user);

        assertThat(alarmSettings.getUser())
                .isSameAs(user);
    }

    @Test
    public void givenNewAlarmSettings_whenInstantiated_thenShouldNotBeNull() {
        AlarmSettings alarmSettings = new AlarmSettings();

        assertThat(alarmSettings).isNotNull();
    }
}