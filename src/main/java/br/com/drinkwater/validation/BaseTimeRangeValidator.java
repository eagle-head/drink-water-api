package br.com.drinkwater.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public abstract class BaseTimeRangeValidator<T> implements ConstraintValidator<TimeRangeConstraint, T> {

    protected TimeRangeConstraint constraint;

    @Override
    public void initialize(TimeRangeConstraint constraint) {
        this.constraint = constraint;
    }

    @Override
    public boolean isValid(T value, ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }

        OffsetDateTime startDate = this.getOffsetDateTimeProperty(value, constraint.startDateField());
        OffsetDateTime endDate = this.getOffsetDateTimeProperty(value, constraint.endDateField());
        if (startDate == null || endDate == null) {
            return true;
        }

        context.disableDefaultConstraintViolation();

        if (!this.validateBothDatesAreInUTC(startDate, endDate, context)) {
            return false;
        }

        if (!this.validateThatStartDateIsBeforeEndDate(startDate, endDate, context)) {
            return false;
        }

        return this.validateMinimumIntervalOfFifteenMinutesBetweenDates(startDate, endDate, context);
    }

    private boolean validateThatStartDateIsBeforeEndDate(OffsetDateTime startDate,
                                                         OffsetDateTime endDate,
                                                         ConstraintValidatorContext context) {
        if (!startDate.isBefore(endDate)) {
            addConstraintViolation(context, "Start date must be before end date.", constraint.endDateField());
            return false;
        }

        return true;
    }

    private boolean validateMinimumIntervalOfFifteenMinutesBetweenDates(OffsetDateTime startDate,
                                                                        OffsetDateTime endDate,
                                                                        ConstraintValidatorContext context) {
        long minutesBetween = Duration.between(startDate, endDate).toMinutes();
        if (minutesBetween < 15) {
            addConstraintViolation(context, "The time range must be at least 15 minutes.", constraint.startDateField());
            return false;
        }

        return true;
    }

    private boolean validateBothDatesAreInUTC(OffsetDateTime startDate,
                                              OffsetDateTime endDate,
                                              ConstraintValidatorContext context) {
        if (startDate.getOffset() == null) {
            addConstraintViolation(context, "Start date is missing timezone information.", constraint.startDateField());
            return false;
        }

        if (endDate.getOffset() == null) {
            addConstraintViolation(context, "End date is missing timezone information.", constraint.endDateField());
            return false;
        }

        if (!startDate.getOffset().equals(ZoneOffset.UTC)) {
            addConstraintViolation(context, "Start date must be in UTC.", constraint.startDateField());
            return false;
        }

        if (!endDate.getOffset().equals(ZoneOffset.UTC)) {
            addConstraintViolation(context, "End date must be in UTC.", constraint.endDateField());
            return false;
        }

        return true;
    }

    protected OffsetDateTime getOffsetDateTimeProperty(T value, String propertyName) {

        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(value);
        if (!wrapper.isReadableProperty(propertyName)) {
            throw new IllegalArgumentException(
                    "Property '" + propertyName + "' does not exist or is not accessible in class " + value.getClass().getName()
            );
        }

        Class<?> propertyType = wrapper.getPropertyType(propertyName);
        if (propertyType == null || !OffsetDateTime.class.isAssignableFrom(propertyType)) {
            throw new IllegalArgumentException(
                    "Property '" + propertyName + "' is not of type OffsetDateTime in class " + value.getClass().getName()
            );
        }

        try {
            Object propertyValue = wrapper.getPropertyValue(propertyName);
            return (OffsetDateTime) propertyValue;
        } catch (Exception ex) {
            throw new IllegalArgumentException(
                    "Error accessing property '" + propertyName + "' in class " + value.getClass().getName(), ex
            );
        }
    }

    protected void addConstraintViolation(ConstraintValidatorContext context, String errorMessage, String fieldName) {
        context.buildConstraintViolationWithTemplate(errorMessage)
                .addPropertyNode(fieldName)
                .addConstraintViolation();
    }
}
