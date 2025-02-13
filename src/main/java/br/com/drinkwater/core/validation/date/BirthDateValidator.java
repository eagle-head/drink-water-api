package br.com.drinkwater.core.validation.date;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.Period;

@Component
public class BirthDateValidator implements ConstraintValidator<ValidBirthDate, OffsetDateTime> {

    private int minimumAge;
    private final MessageSource messageSource;
    private final DateTimeValidator dateTimeValidator;

    public BirthDateValidator(MessageSource messageSource) {
        this.messageSource = messageSource;
        this.dateTimeValidator = new DateTimeValidator(messageSource);
    }

    @Override
    public void initialize(ValidBirthDate constraintAnnotation) {
        this.minimumAge = constraintAnnotation.minimumAge();
    }

    @Override
    public boolean isValid(OffsetDateTime birthDate, ConstraintValidatorContext context) {
        if (birthDate == null) {
            return true; // Let @NotNull handle it
        }

        context.disableDefaultConstraintViolation();

        ValidationResult<OffsetDateTime> result = dateTimeValidator.validateUTCDateTime(
                birthDate,
                DateTimeValidationRules.forBirthDate()
        );

        if (!result.isValid()) {
            context.buildConstraintViolationWithTemplate(result.getErrorMessage())
                    .addConstraintViolation();
            return false;
        }

        LocalDate birthLocalDate = birthDate.toLocalDate();
        LocalDate now = LocalDate.now();
        Period age = Period.between(birthLocalDate, now);

        if (age.getYears() < minimumAge) {
            context.buildConstraintViolationWithTemplate(
                    messageSource.getMessage("validation.birthdate.minimum.age",
                            new Object[]{minimumAge},
                            LocaleContextHolder.getLocale())
            ).addConstraintViolation();

            return false;
        }

        return true;
    }
}