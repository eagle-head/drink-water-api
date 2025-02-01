package br.com.drinkwater.usermanagement.constants.dto;

import br.com.drinkwater.usermanagement.constants.primitive.DateTimeTestData;
import br.com.drinkwater.usermanagement.dto.AlarmSettingsDTO;

public final class AlarmSettingsDtoTestData {

    private AlarmSettingsDtoTestData() {
    }

    // Valid data
    public static final AlarmSettingsDTO DEFAULT = new AlarmSettingsDTO(
            2000,
            60,
            DateTimeTestData.START_OF_DAY,
            DateTimeTestData.END_OF_DAY
    );

    public static final AlarmSettingsDTO WITH_MAX_VALUES = new AlarmSettingsDTO(
            10000,
            240,
            DateTimeTestData.START_OF_DAY,
            DateTimeTestData.END_OF_DAY
    );

    public static final AlarmSettingsDTO WITH_MIN_VALUES = new AlarmSettingsDTO(
            50,
            15,
            DateTimeTestData.START_OF_DAY,
            DateTimeTestData.END_OF_DAY
    );

    // Invalid data - Null values
    public static final AlarmSettingsDTO WITH_NULL_START_TIME = new AlarmSettingsDTO(
            2000,
            60,
            null,
            DateTimeTestData.END_OF_DAY
    );

    public static final AlarmSettingsDTO WITH_NULL_END_TIME = new AlarmSettingsDTO(
            2000,
            60,
            DateTimeTestData.START_OF_DAY,
            null
    );

    // Invalid data - Out of range values
    public static final AlarmSettingsDTO WITH_GOAL_BELOW_MIN = new AlarmSettingsDTO(
            49,
            60,
            DateTimeTestData.START_OF_DAY,
            DateTimeTestData.END_OF_DAY
    );

    public static final AlarmSettingsDTO WITH_GOAL_ABOVE_MAX = new AlarmSettingsDTO(
            10001,
            60,
            DateTimeTestData.START_OF_DAY,
            DateTimeTestData.END_OF_DAY
    );

    public static final AlarmSettingsDTO WITH_INTERVAL_BELOW_MIN = new AlarmSettingsDTO(
            2000,
            14,
            DateTimeTestData.START_OF_DAY,
            DateTimeTestData.END_OF_DAY
    );

    public static final AlarmSettingsDTO WITH_INTERVAL_ABOVE_MAX = new AlarmSettingsDTO(
            2000,
            241,
            DateTimeTestData.START_OF_DAY,
            DateTimeTestData.END_OF_DAY
    );

    public static final AlarmSettingsDTO WITH_END_BEFORE_START = new AlarmSettingsDTO(
            2000,
            60,
            DateTimeTestData.END_OF_DAY,
            DateTimeTestData.START_OF_DAY
    );
}