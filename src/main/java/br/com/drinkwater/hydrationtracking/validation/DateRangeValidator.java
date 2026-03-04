package br.com.drinkwater.hydrationtracking.validation;

import br.com.drinkwater.core.MessageResolver;
import br.com.drinkwater.hydrationtracking.dto.WaterIntakeFilterDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validates that the end date in a water intake filter is not before the start date. Returns {@code
 * true} when either date is null (null-safety delegated to field-level constraints). Rejection adds
 * a violation on the {@code endDate} property node.
 */
public class DateRangeValidator
        implements ConstraintValidator<ValidDateRange, WaterIntakeFilterDTO> {

    private final MessageResolver messageResolver;

    public DateRangeValidator(MessageResolver messageResolver) {
        this.messageResolver = messageResolver;
    }

    @Override
    public boolean isValid(WaterIntakeFilterDTO filter, ConstraintValidatorContext context) {
        if (filter.startDate() == null || filter.endDate() == null) {
            return true;
        }

        if (filter.endDate().isBefore(filter.startDate())) {
            context.disableDefaultConstraintViolation();

            String message =
                    messageResolver.resolve("water-intake.filter.date-range.end-before-start");

            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode("endDate")
                    .addConstraintViolation();

            return false;
        }

        return true;
    }
}
