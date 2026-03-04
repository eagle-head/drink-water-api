package br.com.drinkwater.usermanagement.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.drinkwater.core.MessageResolver;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
final class BirthDateValidatorUnitTest {

    @Mock private MessageResolver messageResolver;

    @Mock private ConstraintValidatorContext context;

    @Mock private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    private BirthDateValidator validator;

    @BeforeEach
    void setUp() {
        validator = new BirthDateValidator(messageResolver);
        when(context.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(violationBuilder);
    }

    @Test
    void givenNullBirthDate_whenIsValid_thenReturnsTrue() {
        assertThat(validator.isValid(null, context)).isTrue();
    }

    @Test
    void givenAge12_whenIsValid_thenReturnsFalseAndBuildsViolation() {
        when(messageResolver.resolve(eq("validation.birthdate.too-young"), eq(13)))
                .thenReturn("You must be at least 13 years old");
        var birthDate = LocalDate.now(ZoneOffset.UTC).minusYears(12);

        assertThat(validator.isValid(birthDate, context)).isFalse();

        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("You must be at least 13 years old");
        verify(violationBuilder).addConstraintViolation();
    }

    @Test
    void givenAge100_whenIsValid_thenReturnsFalseAndBuildsViolation() {
        when(messageResolver.resolve(eq("validation.birthdate.too-old"), eq(99)))
                .thenReturn("Age must not exceed 99 years");
        var birthDate = LocalDate.now(ZoneOffset.UTC).minusYears(100);

        assertThat(validator.isValid(birthDate, context)).isFalse();

        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("Age must not exceed 99 years");
        verify(violationBuilder).addConstraintViolation();
    }

    @Test
    void givenAge25_whenIsValid_thenReturnsTrue() {
        var birthDate = LocalDate.now(ZoneOffset.UTC).minusYears(25);

        assertThat(validator.isValid(birthDate, context)).isTrue();
    }
}
