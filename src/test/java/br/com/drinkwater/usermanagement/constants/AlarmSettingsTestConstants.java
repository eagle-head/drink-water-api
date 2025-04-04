package br.com.drinkwater.usermanagement.constants;

import br.com.drinkwater.usermanagement.dto.AlarmSettingsDTO;
import br.com.drinkwater.usermanagement.dto.AlarmSettingsResponseDTO;
import br.com.drinkwater.usermanagement.model.AlarmSettings;

import java.time.LocalTime;

public final class AlarmSettingsTestConstants {

    private AlarmSettingsTestConstants() {
    }

    public static final LocalTime START_TIME = LocalTime.of(8, 0, 0);
    public static final LocalTime END_TIME = LocalTime.of(22, 0, 0);
    public static final AlarmSettingsDTO ALARM_SETTINGS_DTO;
    public static final AlarmSettings ALARM_SETTINGS;
    public static final AlarmSettingsResponseDTO ALARM_SETTINGS_RESPONSE_DTO;
    public static final AlarmSettings EXISTING_ALARM_SETTINGS_FOR_TEST;

    static {
        ALARM_SETTINGS_DTO = new AlarmSettingsDTO(
                2000,
                30,
                START_TIME,
                END_TIME
        );

        ALARM_SETTINGS = createAlarmSettingsFromDTO();

        EXISTING_ALARM_SETTINGS_FOR_TEST = new AlarmSettings(
                500,  // goal
                60,   // intervalMinutes
                START_TIME,
                END_TIME
        );

        ALARM_SETTINGS_RESPONSE_DTO = new AlarmSettingsResponseDTO(
                ALARM_SETTINGS_DTO.goal(),
                ALARM_SETTINGS_DTO.intervalMinutes(),
                ALARM_SETTINGS_DTO.dailyStartTime(),
                ALARM_SETTINGS_DTO.dailyEndTime()
        );
    }

    private static AlarmSettings createAlarmSettingsFromDTO() {
        return new AlarmSettings(
                ALARM_SETTINGS_DTO.goal(),
                ALARM_SETTINGS_DTO.intervalMinutes(),
                ALARM_SETTINGS_DTO.dailyStartTime(),
                ALARM_SETTINGS_DTO.dailyEndTime()
        );
    }

    public static final LocalTime EXPECTED_START_TIME = START_TIME;
    public static final LocalTime EXPECTED_END_TIME = END_TIME;
    public static final AlarmSettings EXPECTED_ALARM_SETTINGS = new AlarmSettings(
            ALARM_SETTINGS_DTO.goal(),
            ALARM_SETTINGS_DTO.intervalMinutes(),
            EXPECTED_START_TIME,
            EXPECTED_END_TIME
    );
}
