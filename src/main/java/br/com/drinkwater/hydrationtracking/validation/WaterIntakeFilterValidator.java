package br.com.drinkwater.hydrationtracking.validation;

import br.com.drinkwater.hydrationtracking.dto.WaterIntakeFilterDTO;
import br.com.drinkwater.hydrationtracking.exception.InvalidFilterException;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class WaterIntakeFilterValidator {

    private static final int MAX_DATE_RANGE_DAYS = 31;
    private static final Set<String> VALID_SORT_FIELDS = Set.of(
            "dateTimeUTC", "volume", "createdAt", "updatedAt"
    );
    private static final Set<String> VALID_SORT_DIRECTIONS = Set.of("ASC", "DESC");

    public void validate(WaterIntakeFilterDTO filter) {
        List<String> errors = new ArrayList<>();

        validateDateRange(filter, errors);
        validateVolumeRange(filter, errors);
        validateSortCriteria(filter, errors);

        if (!errors.isEmpty()) {
            throw new InvalidFilterException(errors);
        }
    }

    private void validateDateRange(WaterIntakeFilterDTO filter, List<String> errors) {
        if (filter.startDate() != null && filter.endDate() != null) {
            if (filter.startDate().isAfter(filter.endDate())) {
                errors.add("Start date must be before end date");
            }
            
            long daysBetween = Duration.between(
                filter.startDate(), filter.endDate()).toDays();
            if (daysBetween > MAX_DATE_RANGE_DAYS) {
                errors.add("Date range cannot exceed " + MAX_DATE_RANGE_DAYS + " days");
            }
        }
    }

    private void validateVolumeRange(WaterIntakeFilterDTO filter, List<String> errors) {
        if (filter.minVolume() != null && filter.maxVolume() != null 
            && filter.minVolume() > filter.maxVolume()) {
            errors.add("Minimum volume must be less than or equal to maximum volume");
        }
    }

    private void validateSortCriteria(WaterIntakeFilterDTO filter, List<String> errors) {
        if (!VALID_SORT_FIELDS.contains(filter.sortField())) {
            errors.add("Invalid sort field: " + filter.sortField());
        }
        if (!VALID_SORT_DIRECTIONS.contains(filter.sortDirection())) {
            errors.add("Invalid sort direction: " + filter.sortDirection());
        }
    }
}