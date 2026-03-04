package br.com.drinkwater.usermanagement.validation;

import br.com.drinkwater.core.MessageResolver;
import br.com.drinkwater.usermanagement.dto.AlarmSettingsDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalTime;

/**
 * Validates that alarm settings comply with business time rules: start time must precede end time,
 * and both must be within 06:00-22:00. Multiple violations can be reported on individual fields
 * ({@code dailyStartTime}, {@code dailyEndTime}) in a single validation pass.
 */
public class AlarmTimeValidator implements ConstraintValidator<ValidAlarmTime, AlarmSettingsDTO> {

    private static final LocalTime BUSINESS_START = LocalTime.of(6, 0);
    private static final LocalTime BUSINESS_END = LocalTime.of(22, 0);

    private final MessageResolver messageResolver;

    public AlarmTimeValidator(MessageResolver messageResolver) {
        this.messageResolver = messageResolver;
    }

    @Override
    public boolean isValid(AlarmSettingsDTO dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }

        var start = dto.dailyStartTime();
        var end = dto.dailyEndTime();

        if (start == null || end == null) {
            return true;
        }

        var valid = true;
        var defaultDisabled = false;

        if (!start.isBefore(end)) {
            defaultDisabled = disableDefaultOnce(context, defaultDisabled);
            addFieldViolation(context, "alarm-settings.start-before-end", "dailyStartTime");
            valid = false;
        }

        if (start.isBefore(BUSINESS_START) || start.isAfter(BUSINESS_END)) {
            defaultDisabled = disableDefaultOnce(context, defaultDisabled);
            addFieldViolation(
                    context,
                    "alarm-settings.start.business-hours",
                    "dailyStartTime",
                    BUSINESS_START,
                    BUSINESS_END);
            valid = false;
        }

        if (end.isBefore(BUSINESS_START) || end.isAfter(BUSINESS_END)) {
            disableDefaultOnce(context, defaultDisabled);
            addFieldViolation(
                    context,
                    "alarm-settings.end.business-hours",
                    "dailyEndTime",
                    BUSINESS_START,
                    BUSINESS_END);
            valid = false;
        }

        return valid;
    }

    private static boolean disableDefaultOnce(
            ConstraintValidatorContext context, boolean alreadyDisabled) {
        if (!alreadyDisabled) {
            context.disableDefaultConstraintViolation();
        }
        return true;
    }

    private void addFieldViolation(
            ConstraintValidatorContext context, String messageKey, String field, Object... args) {
        context.buildConstraintViolationWithTemplate(messageResolver.resolve(messageKey, args))
                .addPropertyNode(field)
                .addConstraintViolation();
    }
}
