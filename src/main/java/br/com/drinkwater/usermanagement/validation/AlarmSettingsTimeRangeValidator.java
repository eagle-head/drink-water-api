package br.com.drinkwater.usermanagement.validation;

import br.com.drinkwater.core.validation.BaseTimeRangeValidator;
import br.com.drinkwater.core.validation.date.DateTimeValidationConfig;
import br.com.drinkwater.usermanagement.dto.AlarmSettingsDTO;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class AlarmSettingsTimeRangeValidator extends BaseTimeRangeValidator<AlarmSettingsDTO> {

    private static final int BUSINESS_HOURS_START = 6; // 06:00 AM
    private static final int BUSINESS_HOURS_END = 22;  // 22:00 PM

    @Override
    protected DateTimeValidationConfig getDateTimeValidationConfig() {
        return DateTimeValidationConfig.forAlarmSettings();
    }

    @Override
    protected boolean validateDateRange(OffsetDateTime startDate,
                                        OffsetDateTime endDate,
                                        DateTimeValidationConfig config,
                                        ConstraintValidatorContext context) {

        if (!super.validateDateRange(startDate, endDate, config, context)) {
            return false;
        }

        int startHour = startDate.getHour();
        int endHour = endDate.getHour();
        if (startHour < BUSINESS_HOURS_START || startHour > BUSINESS_HOURS_END ||
                endHour < BUSINESS_HOURS_START || endHour > BUSINESS_HOURS_END) {

            addConstraintViolation(
                    context,
                    String.format("Alarm times must be between %d:00 and %d:00",
                            BUSINESS_HOURS_START, BUSINESS_HOURS_END),
                    constraint.startDateField()
            );

            return false;
        }

        return true;
    }
}