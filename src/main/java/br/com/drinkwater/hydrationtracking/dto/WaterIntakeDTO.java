package br.com.drinkwater.hydrationtracking.dto;

import br.com.drinkwater.hydrationtracking.model.VolumeUnit;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import java.time.Instant;

/** Request DTO for creating or updating a water intake record. */
@Schema(description = "Request payload for creating or updating a water intake record")
public record WaterIntakeDTO(
        @Schema(
                        description = "UTC timestamp of the water intake",
                        example = "2025-06-15T14:30:00Z",
                        type = "string",
                        format = "date-time")
                @NotNull(message = "{water-intake.datetime.not-null}")
                @PastOrPresent(message = "{water-intake.datetime.past-or-present}")
                Instant dateTimeUTC,
        @Schema(
                        description = "Volume consumed in the specified unit",
                        example = "250",
                        minimum = "1",
                        maximum = "5000")
                @Positive(message = "{water-intake.volume.positive}")
                @Max(value = 5000, message = "{water-intake.volume.max}")
                int volume,
        @Schema(description = "Unit of measurement for the volume", example = "ML")
                @NotNull(message = "{water-intake.volume-unit.not-null}")
                VolumeUnit volumeUnit) {}
