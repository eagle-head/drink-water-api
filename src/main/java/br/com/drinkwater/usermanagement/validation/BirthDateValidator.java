package br.com.drinkwater.usermanagement.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Period;

public class BirthDateValidator implements ConstraintValidator<ValidBirthDate, LocalDate> {

    private static final int MIN_AGE = 13;
    private static final int MAX_AGE = 99;
    
    @Override
    public void initialize(ValidBirthDate constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext context) {
        // If null, @NotNull should handle it
        if (birthDate == null) return true;
        
        LocalDate today = LocalDate.now();
        int age = Period.between(birthDate, today).getYears();
        
        // Invalidate if birthDate is in the future or age is not within limits
        return age >= MIN_AGE && age <= MAX_AGE;
    }
}
