package br.com.drinkwater.hydrationtracking.dto;

import br.com.drinkwater.hydrationtracking.model.VolumeUnit;
import jakarta.validation.constraints.*;

import java.time.OffsetDateTime;

public record WaterIntakeUpdateDTO(

    @Positive
    @Min(50)
    @Max(1000)
    int volume,

    @NotNull
    @PastOrPresent
    OffsetDateTime dateTimeUTC,

    @NotNull
    VolumeUnit volumeUnit
) {
}
