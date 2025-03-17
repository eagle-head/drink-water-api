package br.com.drinkwater.usermanagement.dto;

import java.time.LocalTime;

public record AlarmSettingsResponseDTO(

        int goal,
        int intervalMinutes,
        LocalTime dailyStartTime,
        LocalTime dailyEndTime
) {
}
