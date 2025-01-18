package br.com.drinkwater.hydrationtracking.dto;

import br.com.drinkwater.hydrationtracking.model.VolumeUnit;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.OffsetDateTime;

public record WaterIntakeDTO(

        @NotNull(message = "DateTime is required")
        @PastOrPresent(message = "DateTime cannot be in the future")
        OffsetDateTime dateTimeUTC,

        @Positive(message = "Volume must be greater than zero")
        @Max(value = 5000, message = "Volume cannot be greater than 5000ml")
        int volume,

        @NotNull(message = "Volume unit is required")
        VolumeUnit volumeUnit
) {
}