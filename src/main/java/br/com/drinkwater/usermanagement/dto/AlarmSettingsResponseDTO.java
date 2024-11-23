package br.com.drinkwater.usermanagement.dto;

import java.time.OffsetDateTime;

public record AlarmSettingsResponseDTO(

        int intervalMinutes,
        OffsetDateTime startTime,
        OffsetDateTime endTime
) {
}
