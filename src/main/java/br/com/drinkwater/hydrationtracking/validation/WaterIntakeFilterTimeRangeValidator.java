package br.com.drinkwater.hydrationtracking.validation;

import br.com.drinkwater.validation.BaseTimeRangeValidator;
import br.com.drinkwater.hydrationtracking.dto.WaterIntakeFilterDTO;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import java.time.OffsetDateTime;

@Component
public class WaterIntakeFilterTimeRangeValidator extends BaseTimeRangeValidator<WaterIntakeFilterDTO> {

    public WaterIntakeFilterTimeRangeValidator(MessageSource messageSource) {
        super(messageSource);
    }

    @Override
    protected boolean validateAdditionalConstraints(
            WaterIntakeFilterDTO value,
            OffsetDateTime startDate,
            OffsetDateTime endDate,
            ConstraintValidatorContext context
    ) {
        return true;
    }
}