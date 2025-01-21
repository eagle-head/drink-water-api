package br.com.drinkwater.usermanagement.dto;

import br.com.drinkwater.validation.TimeRangeConstraint;
import jakarta.validation.constraints.*;

import java.time.OffsetDateTime;

@TimeRangeConstraint(
        startDateField = "dailyStartTime",
        endDateField = "dailyEndTime",
        requireSameDay = true,
        message = "{time.range.max.days}"
)
public record AlarmSettingsDTO(

        @NotNull(message = "Goal is required")
        @Min(value = 50, message = "Goal must be at least 50")
        @Max(value = 10000, message = "Goal cannot exceed 10000")
        int goal,

        @NotNull(message = "Interval minutes is required")
        @Min(value = 15, message = "Minimum interval is 15 minutes")
        @Max(value = 240, message = "Maximum interval is 240 minutes")
        int intervalMinutes,

        @NotNull(message = "Daily start time is required")
        OffsetDateTime dailyStartTime,

        @NotNull(message = "Daily end time is required")
        OffsetDateTime dailyEndTime
) {
}