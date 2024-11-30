package br.com.drinkwater.usermanagement.dto;

import br.com.drinkwater.usermanagement.validation.TimeRangeConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

@TimeRangeConstraint
public record AlarmSettingsDTO(

        @Min(15)
        int intervalMinutes,

        @NotNull
        OffsetDateTime startTime,

        @NotNull
        OffsetDateTime endTime
) {
}