package br.com.drinkwater.hydrationtracking.validation;

import br.com.drinkwater.core.MessageResolver;
import br.com.drinkwater.hydrationtracking.dto.WaterIntakeFilterDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validates that the maximum volume in a water intake filter is not less than the minimum volume.
 * Returns {@code true} when either volume is null (null-safety delegated to field-level
 * constraints). Rejection adds a violation on the {@code maxVolume} property node.
 */
public class VolumeRangeValidator
        implements ConstraintValidator<ValidVolumeRange, WaterIntakeFilterDTO> {

    private final MessageResolver messageResolver;

    public VolumeRangeValidator(MessageResolver messageResolver) {
        this.messageResolver = messageResolver;
    }

    @Override
    public boolean isValid(WaterIntakeFilterDTO filter, ConstraintValidatorContext context) {
        if (filter.minVolume() == null || filter.maxVolume() == null) {
            return true;
        }

        if (filter.maxVolume() < filter.minVolume()) {
            context.disableDefaultConstraintViolation();

            String message = messageResolver.resolve("water-intake.filter.volume-range.invalid");

            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode("maxVolume")
                    .addConstraintViolation();

            return false;
        }

        return true;
    }
}
