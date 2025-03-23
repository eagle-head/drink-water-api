package br.com.drinkwater.hydrationtracking.dto;

import br.com.drinkwater.hydrationtracking.model.VolumeUnit;

import java.time.Instant;

public record ResponseWaterIntakeDTO(

        Long id,
        Instant dateTimeUTC,
        int volume,
        VolumeUnit volumeUnit
) {
}