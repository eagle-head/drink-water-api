package br.com.drinkwater.hydrationtracking.dto;

import br.com.drinkwater.hydrationtracking.validation.ValidDateRange;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Range;

import java.time.Instant;
import java.util.Set;

@ValidDateRange
public record WaterIntakeFilterDTO(
        @NotNull(message = "{waterintakefilter.startdate.notnull}")
        @PastOrPresent(message = "{waterintakefilter.startdate.pastorpresent}")
        Instant startDate,

        @NotNull(message = "{waterintakefilter.enddate.notnull}")
        @PastOrPresent(message = "{waterintakefilter.enddate.pastorpresent}")
        Instant endDate,

        @Range(min = 50, max = 5000, message = "{waterintakefilter.volume.range}")
        Integer minVolume,

        @Range(min = 50, max = 5000, message = "{waterintakefilter.volume.range}")
        Integer maxVolume,

        @Range(min = 0, max = 50, message = "{waterintakefilter.page.range}")
        Integer page,

        @Range(min = 1, max = 50, message = "{waterintakefilter.size.range}")
        Integer size,

        @Pattern(regexp = "^(id|dateTimeUTC|volume|volumeUnit)$",
                message = "Sort field must be one of the following: id, dateTimeUTC, volume, volumeUnit")
        String sortField,

        @Pattern(regexp = "^(ASC|DESC)$",
                message = "Sort direction must be ASC or DESC")
        String sortDirection
) {
    // List of valid sort fields
    private static final Set<String> VALID_SORT_FIELDS =
            Set.of("id", "dateTimeUTC", "volume", "volumeUnit");

    public WaterIntakeFilterDTO {
        page = page == null ? 0 : page;
        size = size == null ? 10 : size;
        sortField = sortField == null ? "dateTimeUTC" : sortField;
        sortDirection = sortDirection == null ? "DESC" : sortDirection.toUpperCase();

        // Additional validation to prevent SQL Injection in sort fields
        if (!VALID_SORT_FIELDS.contains(sortField)) {
            sortField = "dateTimeUTC";
        }
    }
}