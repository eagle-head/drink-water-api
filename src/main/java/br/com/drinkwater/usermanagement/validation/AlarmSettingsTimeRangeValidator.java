package br.com.drinkwater.usermanagement.validation;

import br.com.drinkwater.validation.BaseTimeRangeValidator;
import br.com.drinkwater.usermanagement.dto.AlarmSettingsDTO;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class AlarmSettingsTimeRangeValidator extends BaseTimeRangeValidator<AlarmSettingsDTO> {

    private static final long FIFTEEN_MINUTES = 15;
    private static final int MIN_NOTIFICATIONS = 2;

    public AlarmSettingsTimeRangeValidator(MessageSource messageSource) {
        super(messageSource);
    }

    @Override
    protected boolean validateAdditionalConstraints(
            AlarmSettingsDTO value,
            OffsetDateTime startDate,
            OffsetDateTime endDate,
            ConstraintValidatorContext context
    ) {
        long totalMinutes = ChronoUnit.MINUTES.between(startDate, endDate);
        if (totalMinutes < FIFTEEN_MINUTES) {
            addConstraintViolation(context, "time.range.min.interval");
            return false;
        }

        if (value.intervalMinutes() > totalMinutes) {
            addConstraintViolation(context, "time.range.exceeds.duration");
            return false;
        }

        if (totalMinutes % value.intervalMinutes() != 0) {
            addConstraintViolation(context, "time.range.interval.multiple");
            return false;
        }

        long numberOfNotifications = totalMinutes / value.intervalMinutes();
        if (numberOfNotifications < MIN_NOTIFICATIONS) {
            addConstraintViolation(context, "time.range.min.notifications");
            return false;
        }

        return true;
    }
}