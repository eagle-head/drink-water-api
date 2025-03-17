package br.com.drinkwater.usermanagement.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = AlarmTimeValidator.class)
@Target({ TYPE })
@Retention(RUNTIME)
public @interface ValidAlarmTime {

    String message() default "Invalid alarm times";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
