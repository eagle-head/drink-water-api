package br.com.drinkwater.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.Duration;
import java.time.OffsetDateTime;

/**
 * Base validator for time ranges that incorporates
 * robust access checks via reflection:
 * - Verifies if the specified properties exist and are accessible.
 * - Confirms if properties are of type {@code OffsetDateTime}.
 * - Handles possible exceptions during property access.
 */
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

        OffsetDateTime startDate = this.getOffsetDateTimeProperty(value, constraint.startDateField());
        OffsetDateTime endDate = this.getOffsetDateTimeProperty(value, constraint.endDateField());

        if (startDate == null || endDate == null) {
            return true;
        }

        context.disableDefaultConstraintViolation();

        if (startDate.isAfter(endDate)) {
            addConstraintViolation(context, "time.range.order");
            return false;
        }

        if (constraint.requireSameDay() && !isSameDay(startDate, endDate)) {
            addConstraintViolation(context, "time.range.same.day");
            return false;
        }

        if (constraint.requireSameTimezone() && !hasSameOffset(startDate, endDate)) {
            addConstraintViolation(context, "time.range.same.timezone");
            return false;
        }

        if (constraint.maxDays() != Integer.MAX_VALUE &&
                Duration.between(startDate, endDate).toDays() > constraint.maxDays()) {

            String message = messageSource.getMessage("time.range.max.days",
                    new Object[]{constraint.maxDays()}, LocaleContextHolder.getLocale());

            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();

            return false;
        }

        return validateAdditionalConstraints(value, startDate, endDate, context);
    }

    /**
     * Helper method that obtains a property value, ensuring that:
     * <ul>
     *   <li>the property exists and is accessible</li>
     *   <li>the property type is {@code OffsetDateTime}</li>
     *   <li>any exceptions during access are caught and forwarded with a clear message</li>
     * </ul>
     *
     * @param value object to be validated
     * @param propertyName name of the property to be accessed
     * @return the property value as {@code OffsetDateTime}, or {@code null} if the value is null
     * @throws IllegalArgumentException if the property doesn't exist, isn't accessible,
     * or isn't of type {@code OffsetDateTime}
     */
    protected OffsetDateTime getOffsetDateTimeProperty(T value, String propertyName) {
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(value);

        if (!wrapper.isReadableProperty(propertyName)) {
            throw new IllegalArgumentException("Property '" + propertyName
                    + "' does not exist or is not accessible in class "
                    + value.getClass().getName());
        }

        Class<?> propertyType = wrapper.getPropertyType(propertyName);
        if (propertyType == null || !OffsetDateTime.class.isAssignableFrom(propertyType)) {
            throw new IllegalArgumentException("Property '" + propertyName
                    + "' is not of type OffsetDateTime in class "
                    + value.getClass().getName());
        }

        try {
            Object propertyValue = wrapper.getPropertyValue(propertyName);
            return (OffsetDateTime) propertyValue;
        } catch (Exception ex) {
            throw new IllegalArgumentException("Error accessing property '"
                    + propertyName + "' in class "
                    + value.getClass().getName(), ex);
        }
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
        String message = messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}