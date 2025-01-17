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
    private static final long MAX_HOURS = 24;
    private static final int MIN_NOTIFICATIONS = 2;

    private final MessageSource messageSource;

    public TimeRangeValidator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public boolean isValid(AlarmSettingsDTO dto, ConstraintValidatorContext context) {
        if (dto.dailyStartTime() == null || dto.dailyEndTime() == null) {
            return true;
        }

        if (!isSameDay(dto.dailyStartTime(), dto.dailyEndTime())) {
            addConstraintViolation(context, "time.range.same.day");
            return false;
        }

        if (!hasSameOffset(dto.dailyStartTime(), dto.dailyEndTime())) {
            addConstraintViolation(context, "time.range.same.timezone");
            return false;
        }

        if (dto.dailyStartTime().isAfter(dto.dailyEndTime())) {
            addConstraintViolation(context, "time.range.order");
            return false;
        }

        long totalMinutes = ChronoUnit.MINUTES.between(dto.dailyStartTime(), dto.dailyEndTime());
        if (totalMinutes < FIFTEEN_MINUTES) {
            addConstraintViolation(context, "time.range.min.interval");
            return false;
        }

        long totalHours = ChronoUnit.HOURS.between(dto.dailyStartTime(), dto.dailyEndTime());
        if (totalHours > MAX_HOURS) {
            addConstraintViolation(context, "time.range.max.interval");
            return false;
        }

        if (dto.intervalMinutes() > totalMinutes) {
            addConstraintViolation(context, "time.range.exceeds.duration");
            return false;
        }

        if (totalMinutes % dto.intervalMinutes() != 0) {
            addConstraintViolation(context, "time.range.interval.multiple");
            return false;
        }

        long numberOfNotifications = totalMinutes / dto.intervalMinutes();
        if (numberOfNotifications < MIN_NOTIFICATIONS) {
            addConstraintViolation(context, "time.range.min.notifications");
            return false;
        }

        return true;
    }

    private boolean isSameDay(OffsetDateTime start, OffsetDateTime end) {
        return start.toLocalDate().equals(end.toLocalDate());
    }

    private boolean hasSameOffset(OffsetDateTime start, OffsetDateTime end) {
        return start.getOffset().equals(end.getOffset());
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String messageKey) {
        context.disableDefaultConstraintViolation();
        String message = messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}