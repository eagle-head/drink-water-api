package br.com.drinkwater.usermanagement.validation;

import br.com.drinkwater.usermanagement.dto.AlarmSettingsDTO;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlarmTimeValidatorUnitTest {

    @Mock
    private MessageSource messageSource;

    @Mock
    private ConstraintValidatorContext context;

    private AlarmTimeValidator validator;

    @BeforeEach
    public void setup() {
        validator = new AlarmTimeValidator(messageSource);
    }

    @Test
    public void givenNullDto_whenIsValid_thenReturnTrue() {
        // Test when dto is null - should cover the first IF statement
        boolean result = validator.isValid(null, context);

        // Should return true
        assertTrue(result);

        // Verify that context methods were not called
        verifyNoInteractions(context);
    }

    @Test
    public void givenNullStartTime_whenIsValid_thenReturnTrue() {
        // Create DTO with null start time
        AlarmSettingsDTO dto = new AlarmSettingsDTO(
                2000,
                30,
                null,
                LocalTime.of(18, 0)
        );

        // Test when start time is null - should cover part of the second IF statement
        boolean result = validator.isValid(dto, context);

        // Should return true
        assertTrue(result);

        // Verify that disableDefaultConstraintViolation was NOT called
        verifyNoInteractions(context);
    }

    @Test
    public void givenNullEndTime_whenIsValid_thenReturnTrue() {
        // Create DTO with null end time
        AlarmSettingsDTO dto = new AlarmSettingsDTO(
                2000,
                30,
                LocalTime.of(8, 0),
                null
        );

        // Test when end time is null - should cover part of the second IF statement
        boolean result = validator.isValid(dto, context);

        // Should return true
        assertTrue(result);

        // Verify that disableDefaultConstraintViolation was NOT called
        verifyNoInteractions(context);
    }
}