package br.com.drinkwater.hydrationtracking.validation;

import br.com.drinkwater.hydrationtracking.dto.WaterIntakeFilterDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class WaterIntakeFilterBeanValidator implements ConstraintValidator<ValidWaterIntakeFilter, WaterIntakeFilterDTO> {

    private static final Set<String> VALID_SORT_FIELDS = Set.of("dateTimeUTC", "volume", "createdAt", "updatedAt");
    private static final Set<String> VALID_SORT_DIRECTIONS = Set.of("ASC", "DESC");

    @Override
    public boolean isValid(WaterIntakeFilterDTO filter, ConstraintValidatorContext context) {

        if (filter == null) {
            return true; // Nulls are handled by other annotations if needed.
        }

        boolean valid = true;
        context.disableDefaultConstraintViolation();

        // Volume range validation: minVolume must be less than or equal to maxVolume, if both are not null.
        if (filter.minVolume() != null && filter.maxVolume() != null && filter.minVolume() > filter.maxVolume()) {
            context
                    .buildConstraintViolationWithTemplate("Minimum volume must be less than or equal to maximum volume")
                    .addPropertyNode("minVolume")
                    .addConstraintViolation();

            valid = false;
        }

        // Sort field validation: must be one of the allowed fields.
        if (!VALID_SORT_FIELDS.contains(filter.sortField())) {
            context
                    .buildConstraintViolationWithTemplate("Invalid sort field: " + filter.sortField())
                    .addPropertyNode("sortField")
                    .addConstraintViolation();

            valid = false;
        }

        // Sort direction validation: must be either "ASC" or "DESC".
        if (!VALID_SORT_DIRECTIONS.contains(filter.sortDirection())) {
            context
                    .buildConstraintViolationWithTemplate("Invalid sort direction: " + filter.sortDirection())
                    .addPropertyNode("sortDirection")
                    .addConstraintViolation();

            valid = false;
        }

        return valid;
    }
}