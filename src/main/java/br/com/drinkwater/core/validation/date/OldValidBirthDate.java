package br.com.drinkwater.core.validation.date;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = OldBirthDateValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OldValidBirthDate {

    String message() default "{validation.birthdate.invalid}";

    int minimumAge() default 18;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}