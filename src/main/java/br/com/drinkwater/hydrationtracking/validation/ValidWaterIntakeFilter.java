package br.com.drinkwater.hydrationtracking.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = WaterIntakeFilterBeanValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidWaterIntakeFilter {

    String message() default "Invalid water intake filter";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
