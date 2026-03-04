package br.com.drinkwater.usermanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;

/** Response DTO representing hydration reminder alarm settings returned by the API. */
@Schema(description = "Hydration reminder alarm settings")
public record AlarmSettingsResponseDTO(
        @Schema(description = "Daily water intake goal in milliliters", example = "2000") int goal,
        @Schema(description = "Reminder interval in minutes", example = "60") int intervalMinutes,
        @Schema(
                        description = "Daily reminder start time",
                        example = "08:00:00",
                        type = "string",
                        format = "time")
                LocalTime dailyStartTime,
        @Schema(
                        description = "Daily reminder end time",
                        example = "22:00:00",
                        type = "string",
                        format = "time")
                LocalTime dailyEndTime) {}
