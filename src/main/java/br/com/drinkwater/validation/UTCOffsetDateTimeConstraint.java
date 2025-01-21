package br.com.drinkwater.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UTCOffsetDateTimeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UTCOffsetDateTimeConstraint {

    String message() default "DateTime must be in UTC";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}