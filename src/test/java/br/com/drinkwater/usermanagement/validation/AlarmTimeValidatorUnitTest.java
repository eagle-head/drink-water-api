package br.com.drinkwater.usermanagement.validation;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import br.com.drinkwater.core.MessageResolver;
import br.com.drinkwater.usermanagement.dto.AlarmSettingsDTO;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AlarmTimeValidatorUnitTest {

    @Mock private MessageResolver messageResolver;

    @Mock private ConstraintValidatorContext context;

    private AlarmTimeValidator validator;

    @BeforeEach
    void setup() {
        validator = new AlarmTimeValidator(messageResolver);
    }

    @Test
    void givenNullDto_whenIsValid_thenReturnTrue() {
        boolean result = validator.isValid(null, context);

        assertTrue(result);

        verifyNoInteractions(context);
    }

    @Test
    void givenNullStartTime_whenIsValid_thenReturnTrue() {
        AlarmSettingsDTO dto = new AlarmSettingsDTO(2000, 30, null, LocalTime.of(18, 0));

        boolean result = validator.isValid(dto, context);

        assertTrue(result);

        verifyNoInteractions(context);
    }

    @Test
    void givenNullEndTime_whenIsValid_thenReturnTrue() {
        AlarmSettingsDTO dto = new AlarmSettingsDTO(2000, 30, LocalTime.of(8, 0), null);

        boolean result = validator.isValid(dto, context);

        assertTrue(result);

        verifyNoInteractions(context);
    }
}
