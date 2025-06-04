package br.com.drinkwater.hydrationtracking.validation;

import br.com.drinkwater.hydrationtracking.dto.WaterIntakeFilterDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, WaterIntakeFilterDTO> {

    @Override
    public boolean isValid(WaterIntakeFilterDTO filter, ConstraintValidatorContext context) {
        // Check if both dates are present
        if (filter.startDate() == null || filter.endDate() == null) {
            return true; // The @NotNull validation will handle this case
        }

        // Check if the end date is after the start date
        if (filter.endDate().isBefore(filter.startDate())) {
            // Disable default constraint violation
            context.disableDefaultConstraintViolation();

            // Add custom message using message key
            context.buildConstraintViolationWithTemplate(
                            "{validation.timerange.start.before.end}")
                    .addPropertyNode("endDate")
                    .addConstraintViolation();

            return false;
        }

        return true;
    }
}