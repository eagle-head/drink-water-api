package br.com.drinkwater.hydrationtracking.dto;

import br.com.drinkwater.hydrationtracking.model.VolumeUnit;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;

public record WaterIntakeFilterDTO(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        OffsetDateTime startDate,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        OffsetDateTime endDate,

        @Min(50)
        @Max(1000)
        int minVolume,

        @Min(50)
        @Max(1000)
        int maxVolume,

        VolumeUnit volumeUnit,

        @NotBlank
        @Pattern(regexp = "dateTimeUTC|volume")
        String sortBy,

        @NotBlank
        @Pattern(regexp = "asc|desc")
        String direction,

        @PositiveOrZero
        int page,

        @Min(5)
        @Max(20)
        int size
) {
}
