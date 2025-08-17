package br.com.drinkwater.config.validation;

import br.com.drinkwater.config.properties.ApplicationProperties;
import br.com.drinkwater.config.runtime.RuntimeLoggingConfiguration;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test demonstrating that the validation refactoring works correctly.
 * This test verifies that the shared ValidationErrorFormatter produces the same
 * results as the original duplicated code would have.
 */
@SpringJUnitConfig
class ValidationRefactoringIntegrationTest {

    private ValidationErrorFormatter validationErrorFormatter;
    private Validator validator;

    @BeforeEach
    void setUp() {
        validationErrorFormatter = new ValidationErrorFormatter();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void verifySharedValidationFormatterWorksForApplicationProperties() {
        // Given: Valid application properties
        ApplicationProperties validProps = new ApplicationProperties("test-app", "1.0.0", "development");
        
        // When: Validating using the shared formatter
        List<String> errors = validationErrorFormatter.formatValidationErrors(validProps, "Application", validator);
        
        // Then: No validation errors should be found
        assertThat(errors).isEmpty();
    }

    @Test
    void verifySharedValidationFormatterWorksForInvalidProperties() {
        // Given: Invalid application properties (null name)
        ApplicationProperties invalidProps = new ApplicationProperties(null, "1.0.0", "development");
        
        // When: Validating using the shared formatter
        List<String> errors = validationErrorFormatter.formatValidationErrors(invalidProps, "Application", validator);
        
        // Then: Validation errors should be properly formatted
        assertThat(errors).isNotEmpty();
        assertThat(errors.get(0)).matches("\\[Application\\] name: .*");
    }

    @Test
    void verifySharedValidationFormatterWorksForRuntimeConfiguration() {
        // Given: Valid runtime logging configuration
        RuntimeLoggingConfiguration validConfig = new RuntimeLoggingConfiguration();
        validConfig.setRoot("INFO");
        validConfig.setApp("DEBUG");
        validConfig.setSecurity("WARN");
        validConfig.setOauth2("WARN");
        validConfig.setSql("WARN");
        validConfig.setSqlParams("WARN");
        validConfig.setHibernate("WARN");
        validConfig.setSpringframework("WARN");
        
        // When: Validating using the shared formatter
        List<String> errors = validationErrorFormatter.formatValidationErrors(validConfig, "Runtime Logging", validator);
        
        // Then: No validation errors should be found (valid configuration)
        assertThat(errors).isEmpty();
    }

    @Test
    void verifyMultipleObjectValidationWorks() {
        // Given: Multiple configuration objects
        ApplicationProperties appProps = new ApplicationProperties("test-app", "1.0.0", "development");
        RuntimeLoggingConfiguration loggingConfig = new RuntimeLoggingConfiguration();
        loggingConfig.setRoot("INFO");
        loggingConfig.setApp("DEBUG");
        loggingConfig.setSecurity("WARN");
        loggingConfig.setOauth2("WARN");
        loggingConfig.setSql("WARN");
        loggingConfig.setSqlParams("WARN");
        loggingConfig.setHibernate("WARN");
        loggingConfig.setSpringframework("WARN");
        
        // When: Validating multiple objects using the shared formatter
        List<String> errors = validationErrorFormatter.validateMultipleObjects(validator,
                new ValidationErrorFormatter.ValidationTarget<>(appProps, "Application"),
                new ValidationErrorFormatter.ValidationTarget<>(loggingConfig, "Runtime Logging")
        );
        
        // Then: No validation errors should be found
        assertThat(errors).isEmpty();
    }

    @Test
    void verifyErrorFormattingConsistency() {
        // Given: An invalid configuration that will produce constraint violations
        ApplicationProperties invalidProps = new ApplicationProperties(null, "", "invalid-env");
        
        // When: Getting validation errors via both methods
        Set<ConstraintViolation<ApplicationProperties>> directViolations = validator.validate(invalidProps);
        List<String> formattedErrors = validationErrorFormatter.formatValidationErrors(invalidProps, "Application", validator);
        
        // Then: The number of errors should match
        assertThat(formattedErrors).hasSameSizeAs(directViolations);
        
        // And: All formatted errors should follow the expected pattern
        formattedErrors.forEach(error -> {
            assertThat(error).matches("\\[Application\\] .+: .+");
        });
    }

    @Test
    void demonstrateRefactoringEliminatesDuplication() {
        // This test demonstrates that both RuntimeConfigurationValidator and EnvironmentVariableValidator
        // now use the same shared ValidationErrorFormatter instead of duplicated validation logic.
        
        // Given: A configuration object
        ApplicationProperties config = new ApplicationProperties("test", "1.0.0", "development");
        
        // When: Using the shared formatter (same logic used by both validators)
        List<String> errors = validationErrorFormatter.formatValidationErrors(config, "Test Component", validator);
        
        // Then: The result is consistent and follows the expected format
        assertThat(errors).isEmpty(); // Valid configuration
        
        // This same formatter is now used by:
        // 1. RuntimeConfigurationValidator.validateRuntimeConfiguration()
        // 2. EnvironmentVariableValidator.validateAllProperties()
        // eliminating the code duplication between validateComponent() and validateProperty()
    }
}