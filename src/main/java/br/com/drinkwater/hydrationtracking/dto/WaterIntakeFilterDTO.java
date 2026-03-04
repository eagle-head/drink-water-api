package br.com.drinkwater.hydrationtracking.dto;

import br.com.drinkwater.hydrationtracking.validation.ValidDateRange;
import br.com.drinkwater.hydrationtracking.validation.ValidVolumeRange;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import org.hibernate.validator.constraints.Range;
import org.springframework.lang.Nullable;

/**
 * Query parameters for searching water intake records with cursor-based pagination. Supports
 * filtering by date range and volume range, with configurable sort field and direction. Default
 * sort is {@code dateTimeUTC DESC} with a page size of 10. The opaque {@code cursor} string is
 * decoded by {@link br.com.drinkwater.core.PageCursor} to resume pagination from a previous
 * position.
 */
@ValidDateRange
@ValidVolumeRange
@Schema(description = "Query parameters for searching water intake records")
public record WaterIntakeFilterDTO(
        @Schema(
                        description = "Start of the date range (inclusive)",
                        example = "2025-06-01T00:00:00Z",
                        type = "string",
                        format = "date-time")
                @NotNull(message = "{water-intake.filter.start-date.not-null}")
                @PastOrPresent(message = "{water-intake.filter.start-date.past-or-present}")
                Instant startDate,
        @Schema(
                        description = "End of the date range (inclusive)",
                        example = "2025-06-30T23:59:59Z",
                        type = "string",
                        format = "date-time")
                @NotNull(message = "{water-intake.filter.end-date.not-null}")
                @PastOrPresent(message = "{water-intake.filter.end-date.past-or-present}")
                Instant endDate,
        @Schema(
                        description = "Minimum volume filter (ml)",
                        example = "100",
                        minimum = "50",
                        maximum = "5000")
                @Range(min = 50, max = 5000, message = "{water-intake.filter.volume.range}")
                Integer minVolume,
        @Schema(
                        description = "Maximum volume filter (ml)",
                        example = "2000",
                        minimum = "50",
                        maximum = "5000")
                @Range(min = 50, max = 5000, message = "{water-intake.filter.volume.range}")
                Integer maxVolume,
        @Schema(description = "Opaque cursor for pagination (from previous response's nextCursor)")
                @Nullable
                String cursor,
        @Schema(
                        description = "Page size",
                        example = "10",
                        defaultValue = "10",
                        minimum = "1",
                        maximum = "50")
                @Range(min = 1, max = 50, message = "{water-intake.filter.size.range}")
                Integer size,
        @Schema(
                        description = "Sort field",
                        example = "dateTimeUTC",
                        defaultValue = "dateTimeUTC",
                        allowableValues = {"id", "dateTimeUTC", "volume", "volumeUnit"})
                @Pattern(
                        regexp = "^(id|dateTimeUTC|volume|volumeUnit)$",
                        message = "{water-intake.filter.sort-field.pattern}")
                String sortField,
        @Schema(
                        description = "Sort direction",
                        example = "DESC",
                        defaultValue = "DESC",
                        allowableValues = {"ASC", "DESC"})
                @Pattern(
                        regexp = "^(ASC|DESC)$",
                        message = "{water-intake.filter.sort-direction.pattern}")
                String sortDirection) {

    private static final Set<String> VALID_SORT_FIELDS =
            Set.of("id", "dateTimeUTC", "volume", "volumeUnit");

    public WaterIntakeFilterDTO {
        size = Objects.requireNonNullElse(size, 10);
        sortField = Objects.requireNonNullElse(sortField, "dateTimeUTC");
        sortDirection = sortDirection == null ? "DESC" : sortDirection.toUpperCase(Locale.ROOT);

        if (!VALID_SORT_FIELDS.contains(sortField)) {
            sortField = "dateTimeUTC";
        }
    }
}
