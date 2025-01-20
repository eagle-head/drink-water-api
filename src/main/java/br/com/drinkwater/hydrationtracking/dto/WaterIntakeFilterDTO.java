package br.com.drinkwater.hydrationtracking.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;

import java.time.OffsetDateTime;

public record WaterIntakeFilterDTO(

        @PastOrPresent
        OffsetDateTime startDate,

        @PastOrPresent
        OffsetDateTime endDate,

        @Min(0)
        @Max(5000)
        Integer minVolume,

        @Min(0)
        @Max(5000)
        Integer maxVolume,

        @Min(0)
        @Max(100)
        Integer page,

        @Min(1)
        @Max(50)
        Integer size,

        String sortField,
        String sortDirection
) {
    public WaterIntakeFilterDTO {
        page = page == null ? 0 : page;
        size = size == null ? 10 : size;
        sortField = sortField == null ? "dateTimeUTC" : sortField;
        sortDirection = sortDirection == null ? "DESC" : sortDirection.toUpperCase();
    }
}