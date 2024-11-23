package br.com.drinkwater.usermanagement.validation;

import br.com.drinkwater.usermanagement.dto.AlarmSettingsDTO;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TimeRangeValidator implements ConstraintValidator<TimeRangeConstraint, AlarmSettingsDTO> {

    private static final long FIFTEEN_MINUTES = 15;
    private final MessageSource messageSource;

    public TimeRangeValidator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public boolean isValid(AlarmSettingsDTO dto, ConstraintValidatorContext context) {
        if (dto.startTime() == null || dto.endTime() == null) {
            return true;
        }

        if (!isSameDay(dto.startTime(), dto.endTime())) {
            addConstraintViolation(context, "time.range.same.day");
            return false;
        }

        if (dto.startTime().isAfter(dto.endTime())) {
            addConstraintViolation(context, "time.range.order");
            return false;
        }

        long totalMinutes = ChronoUnit.MINUTES.between(dto.startTime(), dto.endTime());
        if (totalMinutes < FIFTEEN_MINUTES) {
            addConstraintViolation(context, "time.range.min.interval");
            return false;
        }

        if (dto.intervalMinutes() > totalMinutes) {
            addConstraintViolation(context, "time.range.exceeds.duration");
            return false;
        }

        return true;
    }

    private boolean isSameDay(OffsetDateTime start, OffsetDateTime end) {
        return start.toLocalDate().equals(end.toLocalDate());
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String messageKey) {
        context.disableDefaultConstraintViolation();
        String message = messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
