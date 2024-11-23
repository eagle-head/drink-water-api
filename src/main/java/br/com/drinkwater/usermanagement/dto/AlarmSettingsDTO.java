package br.com.drinkwater.usermanagement.dto;

import br.com.drinkwater.usermanagement.validation.TimeRangeConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.OffsetDateTime;

@TimeRangeConstraint
public record AlarmSettingsDTO(

        @Positive
        int intervalMinutes,

        @NotNull
        OffsetDateTime startTime,

        @NotNull
        OffsetDateTime endTime
) {
}