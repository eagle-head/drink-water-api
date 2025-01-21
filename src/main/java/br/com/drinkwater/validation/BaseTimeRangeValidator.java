package br.com.drinkwater.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.Duration;
import java.time.OffsetDateTime;

public abstract class BaseTimeRangeValidator<T> implements ConstraintValidator<TimeRangeConstraint, T> {

    protected final MessageSource messageSource;
    protected TimeRangeConstraint constraint;

    protected BaseTimeRangeValidator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void initialize(TimeRangeConstraint constraint) {
        this.constraint = constraint;
    }

    @Override
    public boolean isValid(T value, ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }

        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(value);
        OffsetDateTime startDate = (OffsetDateTime) wrapper.getPropertyValue(constraint.startDateField());
        OffsetDateTime endDate = (OffsetDateTime) wrapper.getPropertyValue(constraint.endDateField());

        if (startDate == null || endDate == null) {
            return true;
        }

        context.disableDefaultConstraintViolation();

        if (startDate.isAfter(endDate)) {
            this.addConstraintViolation(context, "time.range.order");
            return false;
        }

        if (this.constraint.requireSameDay() && !this.isSameDay(startDate, endDate)) {
            this.addConstraintViolation(context, "time.range.same.day");
            return false;
        }

        if (this.constraint.requireSameTimezone() && !this.hasSameOffset(startDate, endDate)) {
            this.addConstraintViolation(context, "time.range.same.timezone");
            return false;
        }

        if (this.constraint.maxDays() != Integer.MAX_VALUE) {
            long daysBetween = Duration.between(startDate, endDate).toDays();
            if (daysBetween > this.constraint.maxDays()) {
                String message = this.messageSource.getMessage("time.range.max.days",
                        new Object[]{constraint.maxDays()}, LocaleContextHolder.getLocale());
                context.buildConstraintViolationWithTemplate(message).addConstraintViolation();

                return false;
            }
        }

        return this.validateAdditionalConstraints(value, startDate, endDate, context);
    }

    protected abstract boolean validateAdditionalConstraints(
            T value,
            OffsetDateTime startDate,
            OffsetDateTime endDate,
            ConstraintValidatorContext context
    );

    protected boolean isSameDay(OffsetDateTime start, OffsetDateTime end) {
        return start.toLocalDate().equals(end.toLocalDate());
    }

    protected boolean hasSameOffset(OffsetDateTime start, OffsetDateTime end) {
        return start.getOffset().equals(end.getOffset());
    }

    protected void addConstraintViolation(ConstraintValidatorContext context, String messageKey) {
        String message = this.messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}