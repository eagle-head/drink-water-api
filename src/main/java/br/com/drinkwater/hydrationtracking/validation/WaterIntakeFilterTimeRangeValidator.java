package br.com.drinkwater.hydrationtracking.validation;

import br.com.drinkwater.core.validation.BaseTimeRangeValidator;
import br.com.drinkwater.core.validation.date.DateTimeValidationConfig;
import br.com.drinkwater.hydrationtracking.dto.WaterIntakeFilterDTO;
import org.springframework.stereotype.Component;

@Component
public class WaterIntakeFilterTimeRangeValidator extends BaseTimeRangeValidator<WaterIntakeFilterDTO> {

    @Override
    protected DateTimeValidationConfig getDateTimeValidationConfig() {
        return DateTimeValidationConfig.forHydrationTracking();
    }
}