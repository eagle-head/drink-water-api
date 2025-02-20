package br.com.drinkwater.hydrationtracking.dto;

import br.com.drinkwater.core.validation.TimeRangeConstraint;
import br.com.drinkwater.hydrationtracking.validation.ValidWaterIntakeFilter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.hibernate.validator.constraints.Range;

import java.time.OffsetDateTime;

@ValidWaterIntakeFilter
@TimeRangeConstraint(
        startDateField = "startDate",
        endDateField = "endDate"
)
public record WaterIntakeFilterDTO(

        @NotNull(message = "{waterintakefilter.startdate.notnull}")
        @PastOrPresent(message = "{waterintakefilter.startdate.pastorpresent}")
        OffsetDateTime startDate,

        @NotNull(message = "{waterintakefilter.enddate.notnull}")
        @PastOrPresent(message = "{waterintakefilter.enddate.pastorpresent}")
        OffsetDateTime endDate,

        @Range(min = 50, max = 5000, message = "{waterintakefilter.volume.range}")
        Integer minVolume,

        @Range(min = 50, max = 5000, message = "{waterintakefilter.volume.range}")
        Integer maxVolume,

        @Range(min = 0, max = 50, message = "{waterintakefilter.page.range}")
        Integer page,

        @Range(min = 1, max = 50, message = "{waterintakefilter.size.range}")
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
