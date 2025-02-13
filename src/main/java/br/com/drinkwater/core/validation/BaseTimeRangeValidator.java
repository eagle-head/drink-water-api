package br.com.drinkwater.core.validation;

import br.com.drinkwater.core.validation.date.DateTimeValidationRules;
import br.com.drinkwater.core.validation.date.DateTimeValidator;
import br.com.drinkwater.core.validation.date.ValidationResult;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.OffsetDateTime;

public abstract class BaseTimeRangeValidator<T> implements ConstraintValidator<TimeRangeConstraint, T> {

    protected TimeRangeConstraint constraint;
    protected final MessageSource messageSource;
    protected final DateTimeValidator dateTimeValidator;

    protected BaseTimeRangeValidator(MessageSource messageSource) {
        this.messageSource = messageSource;
        this.dateTimeValidator = new DateTimeValidator(messageSource);
    }

    @Override
    public void initialize(TimeRangeConstraint constraint) {
        this.constraint = constraint;
    }

    @Override
    public final boolean isValid(T value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        DateTimeValidationRules config = getDateTimeValidationConfig();

        OffsetDateTime startDate = extractStartDate(value);
        OffsetDateTime endDate = extractEndDate(value);

        if (startDate == null || endDate == null) {
            addConstraintViolation(context,
                    getMessage("validation.timerange.null.dates"),
                    constraint.startDateField());
            return false;
        }

        ValidationResult<OffsetDateTime> startValidation = validateDate(startDate, config);
        if (!startValidation.isValid()) {
            addConstraintViolation(context, startValidation.getErrorMessage(),
                    constraint.startDateField());
            return false;
        }

        ValidationResult<OffsetDateTime> endValidation = validateDate(endDate, config);
        if (!endValidation.isValid()) {
            addConstraintViolation(context, endValidation.getErrorMessage(),
                    constraint.endDateField());
            return false;
        }

        return validateTimeRange(startDate, endDate, config, context) &&
                validateCustomRules(startDate, endDate, config, context);
    }

    protected abstract OffsetDateTime extractStartDate(T value);

    protected abstract OffsetDateTime extractEndDate(T value);

    protected abstract DateTimeValidationRules getDateTimeValidationConfig();

    protected abstract boolean validateCustomRules(OffsetDateTime startDate,
                                                   OffsetDateTime endDate,
                                                   DateTimeValidationRules config,
                                                   ConstraintValidatorContext context);

    private boolean validateTimeRange(OffsetDateTime startDate,
                                      OffsetDateTime endDate,
                                      DateTimeValidationRules config,
                                      ConstraintValidatorContext context) {
        if (!startDate.isBefore(endDate)) {
            addConstraintViolation(context,
                    getMessage("validation.timerange.start.before.end"),
                    constraint.startDateField());
            return false;
        }

        if (config.requireSameDay() &&
                !startDate.toLocalDate().equals(endDate.toLocalDate())) {
            addConstraintViolation(context,
                    getMessage("validation.timerange.same.day"),
                    constraint.startDateField());
            return false;
        }

        if (config.minimumMinutesInterval() != null) {
            long minutesBetween = java.time.Duration.between(startDate, endDate).toMinutes();
            if (minutesBetween < config.minimumMinutesInterval()) {
                addConstraintViolation(context,
                        getMessage("validation.timerange.minimum.interval",
                                new Object[]{config.minimumMinutesInterval()}),
                        constraint.startDateField());
                return false;
            }
        }

        return true;
    }

    private ValidationResult<OffsetDateTime> validateDate(OffsetDateTime date,
                                                          DateTimeValidationRules config) {
        return dateTimeValidator.validateUTCDateTime(date, config);
    }

    protected void addConstraintViolation(ConstraintValidatorContext context,
                                          String message,
                                          String field) {
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(field)
                .addConstraintViolation();
    }

    private String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}