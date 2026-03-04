package br.com.drinkwater.usermanagement.validation;

import br.com.drinkwater.core.MessageResolver;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneOffset;

/**
 * Validates that a birth date corresponds to an age between 13 and 99 years (inclusive). Returns
 * {@code true} for null values (null-safety delegated to {@code @NotNull}). Rejection produces a
 * localized message indicating whether the user is too young or too old.
 */
public class BirthDateValidator implements ConstraintValidator<ValidBirthDate, LocalDate> {

    private static final int MIN_AGE = 13;
    private static final int MAX_AGE = 99;

    private final MessageResolver messageResolver;

    public BirthDateValidator(MessageResolver messageResolver) {
        this.messageResolver = messageResolver;
    }

    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext context) {
        if (birthDate == null) {
            return true;
        }

        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        int age = Period.between(birthDate, today).getYears();

        if (age < MIN_AGE) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            messageResolver.resolve("validation.birthdate.too-young", MIN_AGE))
                    .addConstraintViolation();
            return false;
        }

        if (age > MAX_AGE) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            messageResolver.resolve("validation.birthdate.too-old", MAX_AGE))
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
