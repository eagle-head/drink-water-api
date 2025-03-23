package br.com.drinkwater.hydrationtracking.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
@Documented
public @interface ValidDateRange {

    String message() default "Invalid date range";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}