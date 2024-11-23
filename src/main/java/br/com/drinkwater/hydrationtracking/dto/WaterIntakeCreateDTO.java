package br.com.drinkwater.hydrationtracking.dto;

import br.com.drinkwater.hydrationtracking.model.VolumeUnit;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.OffsetDateTime;

public record WaterIntakeCreateDTO(

    @Positive
    int volume,

    @NotNull
    @PastOrPresent
    OffsetDateTime dateTimeUTC,

    @NotNull
    VolumeUnit volumeUnit
) {
}
