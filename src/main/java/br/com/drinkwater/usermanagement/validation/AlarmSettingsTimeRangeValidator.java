package br.com.drinkwater.usermanagement.validation;

import br.com.drinkwater.validation.BaseTimeRangeValidator;
import br.com.drinkwater.usermanagement.dto.AlarmSettingsDTO;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class AlarmSettingsTimeRangeValidator extends BaseTimeRangeValidator<AlarmSettingsDTO> {

    public AlarmSettingsTimeRangeValidator() {
        super();
    }

    @Override
    public boolean isValid(AlarmSettingsDTO value, ConstraintValidatorContext context) {

        boolean baseValid = super.isValid(value, context);
        if (!baseValid) {
            return false;
        }

        if (!value.dailyStartTime().toLocalDate().equals(value.dailyEndTime().toLocalDate())) {
            addConstraintViolation(
                    context,
                    "Start and end times must be on the same day.",
                    "dailyStartTime");

            return false;
        }

        return true;
    }
}
