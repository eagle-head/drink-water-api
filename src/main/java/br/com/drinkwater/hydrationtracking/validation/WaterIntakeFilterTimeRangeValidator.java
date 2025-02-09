package br.com.drinkwater.hydrationtracking.validation;

import br.com.drinkwater.validation.BaseTimeRangeValidator;
import br.com.drinkwater.hydrationtracking.dto.WaterIntakeFilterDTO;
import org.springframework.stereotype.Component;

@Component
public class WaterIntakeFilterTimeRangeValidator extends BaseTimeRangeValidator<WaterIntakeFilterDTO> {

    public WaterIntakeFilterTimeRangeValidator() {
        super();
    }
}
