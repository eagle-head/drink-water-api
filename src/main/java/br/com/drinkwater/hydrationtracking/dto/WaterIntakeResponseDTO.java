package br.com.drinkwater.hydrationtracking.dto;

import br.com.drinkwater.hydrationtracking.model.VolumeUnit;

import java.time.OffsetDateTime;

public record WaterIntakeResponseDTO(
    Long id,
    int volume,
    VolumeUnit volumeUnit,
    OffsetDateTime dateTimeUTC
) {
}
