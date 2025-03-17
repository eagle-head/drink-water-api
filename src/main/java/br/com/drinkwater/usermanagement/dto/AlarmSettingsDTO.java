package br.com.drinkwater.usermanagement.dto;

import br.com.drinkwater.usermanagement.validation.ValidAlarmTime;
import jakarta.validation.constraints.*;

import java.time.LocalTime;

@ValidAlarmTime
public record AlarmSettingsDTO(
        @NotNull(message = "{alarmSettingsDTO.goal.notNull}")
        @Min(value = 50, message = "{alarmSettingsDTO.goal.min}")
        @Max(value = 10000, message = "{alarmSettingsDTO.goal.max}")
        int goal,

        @NotNull(message = "{alarmSettingsDTO.intervalMinutes.notNull}")
        @Min(value = 15, message = "{alarmSettingsDTO.intervalMinutes.min}")
        @Max(value = 240, message = "{alarmSettingsDTO.intervalMinutes.max}")
        int intervalMinutes,

        @NotNull(message = "{alarmSettingsDTO.dailyStartTime.notNull}")
        LocalTime dailyStartTime,

        @NotNull(message = "{alarmSettingsDTO.dailyEndTime.notNull}")
        LocalTime dailyEndTime
) {
}