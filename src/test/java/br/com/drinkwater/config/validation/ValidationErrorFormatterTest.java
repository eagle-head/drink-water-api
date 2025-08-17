package br.com.drinkwater.config.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ValidationErrorFormatter}.
 * Tests the shared validation error formatting functionality.
 */
@ExtendWith(MockitoExtension.class)
class ValidationErrorFormatterTest {

    @Mock
    private Validator validator;

    private ValidationErrorFormatter validationErrorFormatter;

    @BeforeEach
    void setUp() {
        validationErrorFormatter = new ValidationErrorFormatter();
    }

    @Test
    void formatValidationErrors_withValidObject_returnsEmptyList() {
        // given
        TestConfig validConfig = new TestConfig("valid", 42);
        when(validator.validate(validConfig)).thenReturn(Set.of());

        // when
        List<String> errors = validationErrorFormatter.formatValidationErrors(validConfig, "Test", validator);

        // then
        assertThat(errors).isEmpty();
    }

    @Test
    void formatValidationErrors_withInvalidObject_returnsFormattedErrors() {
        // given
        TestConfig invalidConfig = new TestConfig(null, -1);
        
        ConstraintViolation<TestConfig> violation1 = mockConstraintViolation("name", "must not be null");
        ConstraintViolation<TestConfig> violation2 = mockConstraintViolation("value", "must be greater than 0");
        
        when(validator.validate(invalidConfig)).thenReturn(Set.of(violation1, violation2));

        // when
        List<String> errors = validationErrorFormatter.formatValidationErrors(invalidConfig, "Test Config", validator);

        // then
        assertThat(errors).hasSize(2);
        assertThat(errors).containsExactlyInAnyOrder(
                "[Test Config] name: must not be null",
                "[Test Config] value: must be greater than 0"
        );
    }

    @Test
    void formatValidationErrors_withNullConfigObject_throwsException() {
        // when / then
        assertThatThrownBy(() -> validationErrorFormatter.formatValidationErrors(null, "Test", validator))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Configuration object cannot be null");
    }

    @Test
    void formatValidationErrors_withNullComponentName_throwsException() {
        // given
        TestConfig config = new TestConfig("test", 1);

        // when / then
        assertThatThrownBy(() -> validationErrorFormatter.formatValidationErrors(config, null, validator))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Component name cannot be null");
    }

    @Test
    void formatValidationErrors_withNullValidator_throwsException() {
        // given
        TestConfig config = new TestConfig("test", 1);

        // when / then
        assertThatThrownBy(() -> validationErrorFormatter.formatValidationErrors(config, "Test", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Validator cannot be null");
    }

    @Test
    void validateMultipleObjects_withValidObjects_returnsEmptyList() {
        // given
        TestConfig config1 = new TestConfig("valid1", 10);
        TestConfig config2 = new TestConfig("valid2", 20);
        
        when(validator.validate(config1)).thenReturn(Set.of());
        when(validator.validate(config2)).thenReturn(Set.of());

        // when
        List<String> errors = validationErrorFormatter.validateMultipleObjects(validator,
                new ValidationErrorFormatter.ValidationTarget<>(config1, "Config1"),
                new ValidationErrorFormatter.ValidationTarget<>(config2, "Config2")
        );

        // then
        assertThat(errors).isEmpty();
    }

    @Test
    void validateMultipleObjects_withInvalidObjects_returnsAggregatedErrors() {
        // given
        TestConfig config1 = new TestConfig(null, 10);
        TestConfig config2 = new TestConfig("valid", -5);
        
        ConstraintViolation<TestConfig> violation1 = mockConstraintViolation("name", "must not be null");
        ConstraintViolation<TestConfig> violation2 = mockConstraintViolation("value", "must be greater than 0");
        
        when(validator.validate(config1)).thenReturn(Set.of(violation1));
        when(validator.validate(config2)).thenReturn(Set.of(violation2));

        // when
        List<String> errors = validationErrorFormatter.validateMultipleObjects(validator,
                new ValidationErrorFormatter.ValidationTarget<>(config1, "Config1"),
                new ValidationErrorFormatter.ValidationTarget<>(config2, "Config2")
        );

        // then
        assertThat(errors).hasSize(2);
        assertThat(errors).containsExactlyInAnyOrder(
                "[Config1] name: must not be null",
                "[Config2] value: must be greater than 0"
        );
    }

    @Test
    void validateMultipleObjects_withNullValidator_throwsException() {
        // given
        TestConfig config = new TestConfig("test", 1);
        ValidationErrorFormatter.ValidationTarget<TestConfig> target = 
                new ValidationErrorFormatter.ValidationTarget<>(config, "Test");

        // when / then
        assertThatThrownBy(() -> validationErrorFormatter.validateMultipleObjects(null, target))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Validator cannot be null");
    }

    @Test
    void validateMultipleObjects_withNullTarget_throwsException() {
        // when / then
        assertThatThrownBy(() -> validationErrorFormatter.validateMultipleObjects(validator, (ValidationErrorFormatter.ValidationTarget<TestConfig>) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Validation target cannot be null");
    }

    @Test
    void validationTarget_withNullConfigObject_throwsException() {
        // when / then
        assertThatThrownBy(() -> new ValidationErrorFormatter.ValidationTarget<>(null, "Test"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Configuration object cannot be null");
    }

    @Test
    void validationTarget_withNullComponentName_throwsException() {
        // given
        TestConfig config = new TestConfig("test", 1);

        // when / then
        assertThatThrownBy(() -> new ValidationErrorFormatter.ValidationTarget<>(config, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Component name cannot be null or empty");
    }

    @Test
    void validationTarget_withEmptyComponentName_throwsException() {
        // given
        TestConfig config = new TestConfig("test", 1);

        // when / then
        assertThatThrownBy(() -> new ValidationErrorFormatter.ValidationTarget<>(config, "  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Component name cannot be null or empty");
    }

    @SuppressWarnings("unchecked")
    private ConstraintViolation<TestConfig> mockConstraintViolation(String propertyPath, String message) {
        ConstraintViolation<TestConfig> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn(propertyPath);
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn(message);
        return violation;
    }

    /**
     * Test configuration class for validation testing.
     */
    static class TestConfig {
        @NotNull
        private final String name;

        @Positive
        private final int value;

        public TestConfig(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }
    }
}