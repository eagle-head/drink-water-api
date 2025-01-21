package br.com.drinkwater.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class UTCOffsetDateTimeValidator implements ConstraintValidator<UTCOffsetDateTimeConstraint, OffsetDateTime> {

    @Override
    public boolean isValid(OffsetDateTime value, ConstraintValidatorContext context) {

        if (value == null) {
            return true; // Let @NotNull handle null validation
        }

        return ZoneOffset.UTC.equals(value.getOffset());
    }
}