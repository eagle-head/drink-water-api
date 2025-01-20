package br.com.drinkwater.hydrationtracking.dto;

import br.com.drinkwater.hydrationtracking.model.VolumeUnit;

import java.time.OffsetDateTime;

public record ResponseWaterIntakeDTO(

        Long id,
        OffsetDateTime dateTimeUTC,
        int volume,
        VolumeUnit volumeUnit
) {
}