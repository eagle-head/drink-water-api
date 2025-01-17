package br.com.drinkwater.usermanagement.dto;

import java.time.OffsetDateTime;

public record AlarmSettingsResponseDTO(

        int goal,
        int intervalMinutes,
        OffsetDateTime dailyStartTime,
        OffsetDateTime dailyEndTime
) {
}
