package br.com.drinkwater.hydrationtracking.dto;

import br.com.drinkwater.validation.TimeRangeConstraint;
import br.com.drinkwater.validation.UTCOffsetDateTimeConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.OffsetDateTime;

@TimeRangeConstraint(
        startDateField = "startDate",
        endDateField = "endDate",
        maxDays = 31,
        message = "{time.range.max.days}"
)
public record WaterIntakeFilterDTO(

        @NotNull(message = "The start date must not be null.")
        @PastOrPresent(message = "The start date must be a past or present date.")
        @UTCOffsetDateTimeConstraint
        OffsetDateTime startDate,

        @NotNull(message = "The end date must not be null.")
        @PastOrPresent(message = "The end date must be a past or present date.")
        @UTCOffsetDateTimeConstraint
        OffsetDateTime endDate,

        @Min(value = 0, message = "The minimum volume must be at least 0.")
        @Max(value = 5000, message = "The minimum volume cannot exceed 5000.")
        Integer minVolume,

        @Min(value = 0, message = "The maximum volume must be at least 0.")
        @Max(value = 5000, message = "The maximum volume cannot exceed 5000.")
        Integer maxVolume,

        @Min(value = 0, message = "The page number must be at least 0.")
        @Max(value = 100, message = "The page number cannot exceed 100.")
        Integer page,

        @Min(value = 1, message = "The page size must be at least 1.")
        @Max(value = 50, message = "The page size cannot exceed 50.")
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