package br.com.drinkwater.core.validation;

import br.com.drinkwater.hydrationtracking.validation.WaterIntakeFilterTimeRangeValidator;
import br.com.drinkwater.usermanagement.validation.AlarmSettingsTimeRangeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {WaterIntakeFilterTimeRangeValidator.class, AlarmSettingsTimeRangeValidator.class})
public @interface TimeRangeConstraint {

    String message() default "Invalid time range";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String startDateField();

    String endDateField();
}
