package br.com.drinkwater.hydrationtracking.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import br.com.drinkwater.core.MessageResolver;
import br.com.drinkwater.hydrationtracking.dto.WaterIntakeFilterDTO;
import jakarta.validation.ConstraintValidatorContext;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DateRangeValidatorUnitTest {

    @Mock private MessageResolver messageResolver;

    @Mock private ConstraintValidatorContext context;

    @Mock private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext
            nodeBuilder;

    private DateRangeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new DateRangeValidator(messageResolver);
    }

    @Test
    void givenNullStartDate_whenIsValid_thenReturnTrue() {
        var filter = buildFilter(null, Instant.now());

        assertThat(validator.isValid(filter, context)).isTrue();

        verifyNoInteractions(context);
    }

    @Test
    void givenNullEndDate_whenIsValid_thenReturnTrue() {
        var filter = buildFilter(Instant.now(), null);

        assertThat(validator.isValid(filter, context)).isTrue();

        verifyNoInteractions(context);
    }

    @Test
    void givenEndDateBeforeStartDate_whenIsValid_thenReturnFalse() {
        var now = Instant.now();
        var filter = buildFilter(now, now.minus(1, ChronoUnit.DAYS));

        when(messageResolver.resolve("water-intake.filter.date-range.end-before-start"))
                .thenReturn("End date must be after start date");
        when(context.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(violationBuilder);
        when(violationBuilder.addPropertyNode("endDate")).thenReturn(nodeBuilder);

        assertThat(validator.isValid(filter, context)).isFalse();

        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("End date must be after start date");
        verify(violationBuilder).addPropertyNode("endDate");
        verify(nodeBuilder).addConstraintViolation();
    }

    @Test
    void givenEndDateEqualToStartDate_whenIsValid_thenReturnTrue() {
        var now = Instant.now();
        var filter = buildFilter(now, now);

        assertThat(validator.isValid(filter, context)).isTrue();

        verifyNoInteractions(messageResolver);
    }

    @Test
    void givenEndDateAfterStartDate_whenIsValid_thenReturnTrue() {
        var now = Instant.now();
        var filter = buildFilter(now, now.plus(1, ChronoUnit.DAYS));

        assertThat(validator.isValid(filter, context)).isTrue();

        verifyNoInteractions(messageResolver);
    }

    private static WaterIntakeFilterDTO buildFilter(Instant startDate, Instant endDate) {
        return new WaterIntakeFilterDTO(
                startDate, endDate, null, null, null, 10, "dateTimeUTC", "DESC");
    }
}
