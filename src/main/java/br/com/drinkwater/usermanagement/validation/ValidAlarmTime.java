package br.com.drinkwater.usermanagement.validation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Type-level constraint for {@link br.com.drinkwater.usermanagement.dto.AlarmSettingsDTO} that
 * validates alarm time rules: start time must be before end time, and both must fall within
 * business hours (06:00-22:00). Null start/end times are considered valid.
 */
@Documented
@Constraint(validatedBy = AlarmTimeValidator.class)
@Target({TYPE})
@Retention(RUNTIME)
public @interface ValidAlarmTime {

    String message() default "{alarm-settings.time.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
