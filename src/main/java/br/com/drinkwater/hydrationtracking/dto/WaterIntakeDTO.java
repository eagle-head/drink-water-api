package br.com.drinkwater.hydrationtracking.dto;

import br.com.drinkwater.hydrationtracking.model.VolumeUnit;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

public record WaterIntakeDTO(

        @NotNull(message = "{waterintake.datetime.notnull}")
        @PastOrPresent(message = "{waterintake.datetime.pastorpresent}")
        Instant dateTimeUTC,

        @Positive(message = "{waterintake.volume.positive}")
        @Max(value = 5000, message = "{waterintake.volume.max}")
        int volume,

        @NotNull(message = "{waterintake.volumeunit.notnull}")
        VolumeUnit volumeUnit
) {
}