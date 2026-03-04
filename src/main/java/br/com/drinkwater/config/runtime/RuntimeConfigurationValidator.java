package br.com.drinkwater.config.runtime;

import br.com.drinkwater.config.properties.ApplicationProperties;
import br.com.drinkwater.config.validation.ValidationErrorFormatter;
import jakarta.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Validator for runtime configuration changes. Validates runtime configuration whenever a refresh
 * event occurs.
 */
@Component
public class RuntimeConfigurationValidator {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(RuntimeConfigurationValidator.class);

    private final RuntimeLoggingConfiguration loggingConfig;
    private final RuntimeMonitoringConfiguration monitoringConfig;
    private final RuntimeActuatorConfiguration actuatorConfig;
    private final ApplicationProperties applicationProperties;
    private final ValidationErrorFormatter validationErrorFormatter;
    private final Validator validator;

    public RuntimeConfigurationValidator(
            RuntimeLoggingConfiguration loggingConfig,
            RuntimeMonitoringConfiguration monitoringConfig,
            RuntimeActuatorConfiguration actuatorConfig,
            ApplicationProperties applicationProperties,
            ValidationErrorFormatter validationErrorFormatter,
            Validator validator) {
        this.loggingConfig = loggingConfig;
        this.monitoringConfig = monitoringConfig;
        this.actuatorConfig = actuatorConfig;
        this.applicationProperties = applicationProperties;
        this.validationErrorFormatter = validationErrorFormatter;
        this.validator = validator;
    }

    /**
     * Validates runtime configuration on refresh events.
     *
     * @param event the refresh event
     */
    @EventListener(RefreshEvent.class)
    public void onRefreshEvent(RefreshEvent event) {
        LOGGER.debug("Validating runtime configuration after refresh...");

        try {
            validateRuntimeConfiguration();
            LOGGER.debug("Runtime configuration validation passed");
        } catch (Exception e) {
            LOGGER.error("Runtime configuration validation failed", e);
            throw new IllegalStateException(
                    "Runtime configuration validation failed: " + e.getMessage(), e);
        }
    }

    /**
     * Validates all runtime configuration components.
     *
     * @throws IllegalStateException if validation fails
     */
    public void validateRuntimeConfiguration() {
        List<String> validationErrors = new ArrayList<>();

        // Validate each runtime configuration component using shared utility
        validationErrors.addAll(
                validationErrorFormatter.formatValidationErrors(
                        loggingConfig, "Runtime Logging", validator));
        validationErrors.addAll(
                validationErrorFormatter.formatValidationErrors(
                        monitoringConfig, "Runtime Monitoring", validator));
        validationErrors.addAll(
                validationErrorFormatter.formatValidationErrors(
                        actuatorConfig, "Runtime Actuator", validator));

        // Validate cross-component relationships
        validationErrors.addAll(validateCrossComponentRelationships());

        if (!validationErrors.isEmpty()) {
            String errorMessage =
                    "Runtime configuration validation failed:\n"
                            + String.join("\n", validationErrors);
            throw new IllegalStateException(errorMessage);
        }
    }

    private List<String> validateCrossComponentRelationships() {
        List<String> errors = new ArrayList<>();

        // Validate monitoring configuration consistency
        if (!monitoringConfig.isTracingConfigValid()) {
            errors.add(
                    "Invalid runtime tracing configuration: if tracing is enabled,"
                            + " valid zipkin endpoint and sampling rate are required");
        }

        if (!loggingConfig.isProductionSafe() && applicationProperties.isProduction()) {
            LOGGER.warn(
                    "Runtime logging configuration is not production-safe"
                            + " but application is running in production environment");
        }

        if (!actuatorConfig.isProductionSafe() && applicationProperties.isProduction()) {
            LOGGER.warn(
                    "Runtime actuator configuration is not production-safe"
                            + " but application is running in production environment");
        }

        return errors;
    }

    /**
     * Manually validates runtime configuration. Can be called directly to validate configuration
     * before applying changes.
     *
     * @return validation result with any errors
     */
    public ValidationResult validateManually() {
        try {
            validateRuntimeConfiguration();
            return new ValidationResult(true, "Runtime configuration is valid", List.of());
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            return new ValidationResult(false, message, List.of(message.split("\n")));
        }
    }

    /** Result of runtime configuration validation. */
    public record ValidationResult(boolean valid, String message, List<String> errors) {}
}
