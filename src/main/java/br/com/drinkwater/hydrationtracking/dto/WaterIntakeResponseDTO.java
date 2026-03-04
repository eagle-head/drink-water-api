package br.com.drinkwater.hydrationtracking.dto;

import br.com.drinkwater.hydrationtracking.model.VolumeUnit;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

/** Response DTO representing a water intake record returned by the API. */
@Schema(description = "Water intake record")
public record WaterIntakeResponseDTO(
        @Schema(description = "Record ID", example = "1") Long id,
        @Schema(
                        description = "UTC timestamp of the water intake",
                        example = "2025-06-15T14:30:00Z",
                        type = "string",
                        format = "date-time")
                Instant dateTimeUTC,
        @Schema(description = "Volume consumed in the specified unit", example = "250") int volume,
        @Schema(description = "Unit of measurement", example = "ML") VolumeUnit volumeUnit) {}
