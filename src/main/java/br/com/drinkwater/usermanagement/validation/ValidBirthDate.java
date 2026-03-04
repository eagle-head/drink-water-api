package br.com.drinkwater.usermanagement.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Validates that a {@link java.time.LocalDate} represents a birth date with an age between 13 and
 * 99 years (inclusive). Null values are considered valid (use {@code @NotNull} separately).
 */
@Documented
@Constraint(validatedBy = BirthDateValidator.class)
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface ValidBirthDate {

    String message() default "{validation.birthdate.too-young}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
