package br.com.drinkwater.usermanagement.constants.model;

import br.com.drinkwater.usermanagement.constants.primitive.DateTimeTestData;
import br.com.drinkwater.usermanagement.model.AlarmSettings;

public final class AlarmSettingsTestData {

    private AlarmSettingsTestData() {
    }

    // Valid data
    public static final AlarmSettings DEFAULT = createDefault();
    public static final AlarmSettings WITH_MAX_VALUES = createWithMaxValues();
    public static final AlarmSettings WITH_MIN_VALUES = createWithMinValues();

    // Invalid data
    public static final AlarmSettings NULL = null;
    public static final AlarmSettings WITH_NULL_START_TIME = createWithNullStartTime();
    public static final AlarmSettings WITH_NULL_END_TIME = createWithNullEndTime();
    public static final AlarmSettings WITH_NEGATIVE_GOAL = createWithNegativeGoal();
    public static final AlarmSettings WITH_NEGATIVE_INTERVAL = createWithNegativeInterval();
    public static final AlarmSettings WITH_ZERO_GOAL = createWithZeroGoal();
    public static final AlarmSettings WITH_ZERO_INTERVAL = createWithZeroInterval();
    public static final AlarmSettings WITH_END_BEFORE_START = createWithEndBeforeStart();

    private static AlarmSettings createDefault() {
        AlarmSettings settings = new AlarmSettings();
        settings.setGoal(2000);
        settings.setIntervalMinutes(60);
        settings.setDailyStartTime(DateTimeTestData.START_OF_DAY);
        settings.setDailyEndTime(DateTimeTestData.END_OF_DAY);
        return settings;
    }

    private static AlarmSettings createWithMaxValues() {
        AlarmSettings settings = new AlarmSettings();
        settings.setGoal(5000);
        settings.setIntervalMinutes(120);
        settings.setDailyStartTime(DateTimeTestData.START_OF_DAY);
        settings.setDailyEndTime(DateTimeTestData.END_OF_DAY);
        return settings;
    }

    private static AlarmSettings createWithMinValues() {
        AlarmSettings settings = new AlarmSettings();
        settings.setGoal(500);
        settings.setIntervalMinutes(30);
        settings.setDailyStartTime(DateTimeTestData.START_OF_DAY);
        settings.setDailyEndTime(DateTimeTestData.END_OF_DAY);
        return settings;
    }

    private static AlarmSettings createWithNullStartTime() {
        AlarmSettings settings = new AlarmSettings();
        settings.setGoal(2000);
        settings.setIntervalMinutes(60);
        settings.setDailyStartTime(null);
        settings.setDailyEndTime(DateTimeTestData.END_OF_DAY);
        return settings;
    }

    private static AlarmSettings createWithNullEndTime() {
        AlarmSettings settings = new AlarmSettings();
        settings.setGoal(2000);
        settings.setIntervalMinutes(60);
        settings.setDailyStartTime(DateTimeTestData.START_OF_DAY);
        settings.setDailyEndTime(null);
        return settings;
    }

    private static AlarmSettings createWithNegativeGoal() {
        AlarmSettings settings = new AlarmSettings();
        settings.setGoal(-2000);
        settings.setIntervalMinutes(60);
        settings.setDailyStartTime(DateTimeTestData.START_OF_DAY);
        settings.setDailyEndTime(DateTimeTestData.END_OF_DAY);
        return settings;
    }

    private static AlarmSettings createWithNegativeInterval() {
        AlarmSettings settings = new AlarmSettings();
        settings.setGoal(2000);
        settings.setIntervalMinutes(-60);
        settings.setDailyStartTime(DateTimeTestData.START_OF_DAY);
        settings.setDailyEndTime(DateTimeTestData.END_OF_DAY);
        return settings;
    }

    private static AlarmSettings createWithZeroGoal() {
        AlarmSettings settings = new AlarmSettings();
        settings.setGoal(0);
        settings.setIntervalMinutes(60);
        settings.setDailyStartTime(DateTimeTestData.START_OF_DAY);
        settings.setDailyEndTime(DateTimeTestData.END_OF_DAY);
        return settings;
    }

    private static AlarmSettings createWithZeroInterval() {
        AlarmSettings settings = new AlarmSettings();
        settings.setGoal(2000);
        settings.setIntervalMinutes(0);
        settings.setDailyStartTime(DateTimeTestData.START_OF_DAY);
        settings.setDailyEndTime(DateTimeTestData.END_OF_DAY);
        return settings;
    }

    private static AlarmSettings createWithEndBeforeStart() {
        AlarmSettings settings = new AlarmSettings();
        settings.setGoal(2000);
        settings.setIntervalMinutes(60);
        settings.setDailyStartTime(DateTimeTestData.END_OF_DAY);
        settings.setDailyEndTime(DateTimeTestData.START_OF_DAY);
        return settings;
    }
}