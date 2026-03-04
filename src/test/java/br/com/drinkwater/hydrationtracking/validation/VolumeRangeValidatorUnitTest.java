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
class VolumeRangeValidatorUnitTest {

    @Mock private MessageResolver messageResolver;

    @Mock private ConstraintValidatorContext context;

    @Mock private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext
            nodeBuilder;

    private VolumeRangeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new VolumeRangeValidator(messageResolver);
    }

    @Test
    void givenNullMinVolume_whenIsValid_thenReturnTrue() {
        var filter = buildFilter(null, 500);

        assertThat(validator.isValid(filter, context)).isTrue();

        verifyNoInteractions(context);
    }

    @Test
    void givenNullMaxVolume_whenIsValid_thenReturnTrue() {
        var filter = buildFilter(100, null);

        assertThat(validator.isValid(filter, context)).isTrue();

        verifyNoInteractions(context);
    }

    @Test
    void givenBothNull_whenIsValid_thenReturnTrue() {
        var filter = buildFilter(null, null);

        assertThat(validator.isValid(filter, context)).isTrue();

        verifyNoInteractions(context);
    }

    @Test
    void givenMaxVolumeLessThanMinVolume_whenIsValid_thenReturnFalse() {
        var filter = buildFilter(500, 100);

        when(messageResolver.resolve("water-intake.filter.volume-range.invalid"))
                .thenReturn("Max volume must be >= min volume");
        when(context.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(violationBuilder);
        when(violationBuilder.addPropertyNode("maxVolume")).thenReturn(nodeBuilder);

        assertThat(validator.isValid(filter, context)).isFalse();

        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("Max volume must be >= min volume");
        verify(violationBuilder).addPropertyNode("maxVolume");
        verify(nodeBuilder).addConstraintViolation();
    }

    @Test
    void givenMaxVolumeEqualToMinVolume_whenIsValid_thenReturnTrue() {
        var filter = buildFilter(500, 500);

        assertThat(validator.isValid(filter, context)).isTrue();

        verifyNoInteractions(messageResolver);
    }

    @Test
    void givenMaxVolumeGreaterThanMinVolume_whenIsValid_thenReturnTrue() {
        var filter = buildFilter(100, 500);

        assertThat(validator.isValid(filter, context)).isTrue();

        verifyNoInteractions(messageResolver);
    }

    private static WaterIntakeFilterDTO buildFilter(Integer minVolume, Integer maxVolume) {
        return new WaterIntakeFilterDTO(
                Instant.now().minus(7, ChronoUnit.DAYS),
                Instant.now(),
                minVolume,
                maxVolume,
                null,
                10,
                "dateTimeUTC",
                "DESC");
    }
}
