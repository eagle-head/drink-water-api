package br.com.drinkwater.hydrationtracking.validation;

import br.com.drinkwater.core.validation.BaseTimeRangeValidator;
import br.com.drinkwater.core.validation.date.DateTimeValidationRules;
import br.com.drinkwater.hydrationtracking.dto.WaterIntakeFilterDTO;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import java.time.OffsetDateTime;

@Component
public class WaterIntakeFilterTimeRangeValidator extends BaseTimeRangeValidator<WaterIntakeFilterDTO> {

    protected WaterIntakeFilterTimeRangeValidator(MessageSource messageSource) {
        super(messageSource);
    }

    @Override
    protected OffsetDateTime extractStartDate(WaterIntakeFilterDTO value) {
        return value.startDate();
    }

    @Override
    protected OffsetDateTime extractEndDate(WaterIntakeFilterDTO value) {
        return value.endDate();
    }

    @Override
    protected DateTimeValidationRules getDateTimeValidationConfig() {
        return DateTimeValidationRules.forHydrationTracking();
    }

    @Override
    protected boolean validateCustomRules(OffsetDateTime startDate,
                                          OffsetDateTime endDate,
                                          DateTimeValidationRules config,
                                          ConstraintValidatorContext context) {
        return true; // No custom rules for water intake
    }
}