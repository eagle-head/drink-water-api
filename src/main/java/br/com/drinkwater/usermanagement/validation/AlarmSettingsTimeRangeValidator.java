package br.com.drinkwater.usermanagement.validation;

import br.com.drinkwater.core.validation.BaseTimeRangeValidator;
import br.com.drinkwater.core.validation.date.DateTimeValidationRules;
import br.com.drinkwater.usermanagement.dto.AlarmSettingsDTO;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.OffsetDateTime;

@Component
public class AlarmSettingsTimeRangeValidator extends BaseTimeRangeValidator<AlarmSettingsDTO> {

    public static final int BUSINESS_HOURS_START = 6;
    public static final int BUSINESS_HOURS_END = 22;

    public AlarmSettingsTimeRangeValidator(MessageSource messageSource) {
        super(messageSource);
    }

    @Override
    protected OffsetDateTime extractStartDate(AlarmSettingsDTO value) {
        return value.dailyStartTime();
    }

    @Override
    protected OffsetDateTime extractEndDate(AlarmSettingsDTO value) {
        return value.dailyEndTime();
    }

    @Override
    protected DateTimeValidationRules getDateTimeValidationConfig() {
        return DateTimeValidationRules.forAlarmSettings();
    }

    @Override
    protected boolean validateCustomRules(OffsetDateTime startDate,
                                          OffsetDateTime endDate,
                                          DateTimeValidationRules config,
                                          ConstraintValidatorContext context) {

        LocalTime startTime = startDate.toLocalTime();
        LocalTime endTime = endDate.toLocalTime();
        LocalTime businessStart = LocalTime.of(BUSINESS_HOURS_START, 0);
        LocalTime businessEnd = LocalTime.of(BUSINESS_HOURS_END, 0);

        String message = messageSource.getMessage(
                "validation.time.businesshours.detail",
                new Object[]{BUSINESS_HOURS_START, BUSINESS_HOURS_END},
                LocaleContextHolder.getLocale()
        );

        boolean isValid = true;

        if (startTime.isBefore(businessStart) || startTime.isAfter(businessEnd)) {
            addConstraintViolation(context, message, constraint.startDateField());
            isValid = false;
        }

        if (endTime.isBefore(businessStart) || endTime.isAfter(businessEnd)) {
            addConstraintViolation(context, message, constraint.endDateField());
            isValid = false;
        }

        return isValid;
    }
}