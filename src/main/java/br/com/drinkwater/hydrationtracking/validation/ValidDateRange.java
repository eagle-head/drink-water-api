package br.com.drinkwater.hydrationtracking.validation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Type-level constraint for {@link br.com.drinkwater.hydrationtracking.dto.WaterIntakeFilterDTO}
 * that validates the end date is not before the start date. Null dates are considered valid.
 */
@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
@Documented
public @interface ValidDateRange {

    String message() default "{water-intake.filter.date-range.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
