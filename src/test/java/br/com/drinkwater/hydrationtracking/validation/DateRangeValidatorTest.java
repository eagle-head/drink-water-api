package br.com.drinkwater.hydrationtracking.validation;

import br.com.drinkwater.config.TestMessageSourceConfig;
import br.com.drinkwater.hydrationtracking.dto.WaterIntakeFilterDTO;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(TestMessageSourceConfig.class)
@ActiveProfiles("test")
public class DateRangeValidatorTest {

    @Autowired
    private Validator validator;

    @Autowired
    private MessageSource messageSource;

    @Test
    public void givenStartAfterEnd_whenValidate_thenReturnsLocalizedMessage() {
        Instant startDate = Instant.now();
        Instant endDate = startDate.minus(1, ChronoUnit.DAYS);

        WaterIntakeFilterDTO dto = new WaterIntakeFilterDTO(
                startDate,
                endDate,
                null,
                null,
                null,
                null,
                null,
                null
        );

        var violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        var violation = violations.iterator().next();
        String expectedMessage = messageSource.getMessage(
                "validation.timerange.start.before.end",
                null,
                Locale.US
        );
        assertThat(violation.getMessage()).isEqualTo(expectedMessage);
    }
}
