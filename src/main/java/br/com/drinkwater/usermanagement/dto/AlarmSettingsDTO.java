package br.com.drinkwater.usermanagement.dto;

import br.com.drinkwater.usermanagement.validation.ValidAlarmTime;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

/** Request DTO for hydration reminder alarm settings. */
@ValidAlarmTime
@Schema(description = "Hydration reminder alarm settings")
public record AlarmSettingsDTO(
        @Schema(
                        description = "Daily water intake goal in milliliters",
                        example = "2000",
                        minimum = "50",
                        maximum = "10000")
                @Min(value = 50, message = "{alarm-settings.goal.min}")
                @Max(value = 10000, message = "{alarm-settings.goal.max}")
                int goal,
        @Schema(
                        description = "Reminder interval in minutes",
                        example = "60",
                        minimum = "15",
                        maximum = "240")
                @Min(value = 15, message = "{alarm-settings.interval-minutes.min}")
                @Max(value = 240, message = "{alarm-settings.interval-minutes.max}")
                int intervalMinutes,
        @Schema(
                        description = "Daily reminder start time",
                        example = "08:00:00",
                        type = "string",
                        format = "time")
                @NotNull(message = "{alarm-settings.daily-start-time.not-null}")
                LocalTime dailyStartTime,
        @Schema(
                        description = "Daily reminder end time",
                        example = "22:00:00",
                        type = "string",
                        format = "time")
                @NotNull(message = "{alarm-settings.daily-end-time.not-null}")
                LocalTime dailyEndTime) {}
