package br.com.drinkwater.hydrationtracking.validation;

import br.com.drinkwater.hydrationtracking.dto.WaterIntakeFilterDTO;
import br.com.drinkwater.hydrationtracking.exception.InvalidFilterException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Validator for WaterIntakeFilterDTO
 * Handles validations that cannot be expressed through annotations
 */
@Component
public class WaterIntakeFilterValidator {

    private static final Set<String> VALID_SORT_FIELDS = Set.of("id", "dateTimeUTC", "volume", "volumeUnit");
    private static final Set<String> VALID_SORT_DIRECTIONS = Set.of("ASC", "DESC");

    /**
     * Validates the filter and throws InvalidFilterException if invalid
     */
    public void validate(WaterIntakeFilterDTO filter) {
        List<String> errors = new ArrayList<>();

        // Validate date range
        if (filter.startDate() != null && filter.endDate() != null && filter.endDate().isBefore(filter.startDate())) {
            errors.add("End date must be equal to or after start date");
        }

        // Validate volume range
        if (filter.minVolume() != null && filter.maxVolume() != null && filter.maxVolume() < filter.minVolume()) {
            errors.add("Maximum volume must be greater than or equal to minimum volume");
        }

        // Validate sort field
        if (!VALID_SORT_FIELDS.contains(filter.sortField())) {
            errors.add(String.format("Invalid sort field '%s'. Valid values are: %s",
                    filter.sortField(), String.join(", ", VALID_SORT_FIELDS)));
        }

        // Validate sort direction
        if (!VALID_SORT_DIRECTIONS.contains(filter.sortDirection())) {
            errors.add(String.format("Invalid sort direction '%s'. Valid values are: %s",
                    filter.sortDirection(), String.join(", ", VALID_SORT_DIRECTIONS)));
        }

        // If there are validation errors, throw exception
        if (!errors.isEmpty()) {
            throw new InvalidFilterException(errors);
        }
    }
}