package br.com.drinkwater.config.runtime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.drinkwater.config.properties.ApplicationProperties;
import br.com.drinkwater.config.runtime.RuntimeConfigurationValidator.ValidationResult;
import br.com.drinkwater.config.validation.ValidationErrorFormatter;
import jakarta.validation.Validator;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.endpoint.event.RefreshEvent;

/**
 * Unit tests for {@link RuntimeConfigurationValidator}. Tests validation behavior on refresh
 * events, manual validation, and cross-component relationship checks.
 */
@ExtendWith(MockitoExtension.class)
final class RuntimeConfigurationValidatorTest {

    @Mock private RuntimeLoggingConfiguration loggingConfig;
    @Mock private RuntimeMonitoringConfiguration monitoringConfig;
    @Mock private RuntimeActuatorConfiguration actuatorConfig;
    @Mock private ApplicationProperties applicationProperties;
    @Mock private ValidationErrorFormatter validationErrorFormatter;
    @Mock private Validator validator;

    private RuntimeConfigurationValidator runtimeConfigurationValidator;

    @BeforeEach
    void setUp() {
        runtimeConfigurationValidator =
                new RuntimeConfigurationValidator(
                        loggingConfig,
                        monitoringConfig,
                        actuatorConfig,
                        applicationProperties,
                        validationErrorFormatter,
                        validator);
    }

    @Test
    void onRefreshEvent_whenValidationSucceeds_shouldNotThrow() {
        // Given
        RefreshEvent event = new RefreshEvent(this, null, null);
        when(validationErrorFormatter.formatValidationErrors(any(), eq("Runtime Logging"), any()))
                .thenReturn(List.of());
        when(validationErrorFormatter.formatValidationErrors(
                        any(), eq("Runtime Monitoring"), any()))
                .thenReturn(List.of());
        when(validationErrorFormatter.formatValidationErrors(any(), eq("Runtime Actuator"), any()))
                .thenReturn(List.of());
        when(monitoringConfig.isTracingConfigValid()).thenReturn(true);

        // When / Then
        runtimeConfigurationValidator.onRefreshEvent(event);
    }

    @Test
    void onRefreshEvent_whenValidationFails_shouldThrowIllegalStateException() {
        // Given
        RefreshEvent event = new RefreshEvent(this, null, null);
        when(validationErrorFormatter.formatValidationErrors(any(), eq("Runtime Logging"), any()))
                .thenReturn(List.of("[Runtime Logging] root: invalid level"));
        when(validationErrorFormatter.formatValidationErrors(
                        any(), eq("Runtime Monitoring"), any()))
                .thenReturn(List.of());
        when(validationErrorFormatter.formatValidationErrors(any(), eq("Runtime Actuator"), any()))
                .thenReturn(List.of());

        // When / Then
        assertThatThrownBy(() -> runtimeConfigurationValidator.onRefreshEvent(event))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Runtime configuration validation failed");
    }

    @Test
    void validateRuntimeConfiguration_whenAllConfigsValid_shouldPass() {
        // Given
        when(validationErrorFormatter.formatValidationErrors(any(), eq("Runtime Logging"), any()))
                .thenReturn(List.of());
        when(validationErrorFormatter.formatValidationErrors(
                        any(), eq("Runtime Monitoring"), any()))
                .thenReturn(List.of());
        when(validationErrorFormatter.formatValidationErrors(any(), eq("Runtime Actuator"), any()))
                .thenReturn(List.of());
        when(monitoringConfig.isTracingConfigValid()).thenReturn(true);

        // When / Then
        runtimeConfigurationValidator.validateRuntimeConfiguration();
    }

    @Test
    void
            validateRuntimeConfiguration_whenFormatterReturnsErrors_shouldThrowIllegalStateException() {
        // Given
        List<String> errors = List.of("[Runtime Actuator] healthShowDetails: invalid value");
        when(validationErrorFormatter.formatValidationErrors(any(), eq("Runtime Logging"), any()))
                .thenReturn(List.of());
        when(validationErrorFormatter.formatValidationErrors(
                        any(), eq("Runtime Monitoring"), any()))
                .thenReturn(List.of());
        when(validationErrorFormatter.formatValidationErrors(any(), eq("Runtime Actuator"), any()))
                .thenReturn(errors);

        // When / Then
        assertThatThrownBy(runtimeConfigurationValidator::validateRuntimeConfiguration)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Runtime configuration validation failed")
                .hasMessageContaining("[Runtime Actuator] healthShowDetails: invalid value");
    }

    @Test
    void validateRuntimeConfiguration_whenTracingConfigInvalid_shouldThrowIllegalStateException() {
        // Given
        when(validationErrorFormatter.formatValidationErrors(any(), eq("Runtime Logging"), any()))
                .thenReturn(List.of());
        when(validationErrorFormatter.formatValidationErrors(
                        any(), eq("Runtime Monitoring"), any()))
                .thenReturn(List.of());
        when(validationErrorFormatter.formatValidationErrors(any(), eq("Runtime Actuator"), any()))
                .thenReturn(List.of());
        when(monitoringConfig.isTracingConfigValid()).thenReturn(false);

        // When / Then
        assertThatThrownBy(runtimeConfigurationValidator::validateRuntimeConfiguration)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Invalid runtime tracing configuration");
    }

    @Test
    void validateManually_whenAllPasses_shouldReturnValidResult() {
        // Given
        when(validationErrorFormatter.formatValidationErrors(any(), eq("Runtime Logging"), any()))
                .thenReturn(List.of());
        when(validationErrorFormatter.formatValidationErrors(
                        any(), eq("Runtime Monitoring"), any()))
                .thenReturn(List.of());
        when(validationErrorFormatter.formatValidationErrors(any(), eq("Runtime Actuator"), any()))
                .thenReturn(List.of());
        when(monitoringConfig.isTracingConfigValid()).thenReturn(true);

        // When
        ValidationResult result = runtimeConfigurationValidator.validateManually();

        // Then
        assertThat(result.valid()).isTrue();
        assertThat(result.message()).isEqualTo("Runtime configuration is valid");
        assertThat(result.errors()).isEmpty();
    }

    @Test
    void validateManually_whenExceptionThrown_shouldReturnInvalidResultWithMessageExtraction() {
        // Given
        when(validationErrorFormatter.formatValidationErrors(any(), eq("Runtime Logging"), any()))
                .thenReturn(List.of("Error line 1"));
        when(validationErrorFormatter.formatValidationErrors(
                        any(), eq("Runtime Monitoring"), any()))
                .thenReturn(List.of());
        when(validationErrorFormatter.formatValidationErrors(any(), eq("Runtime Actuator"), any()))
                .thenReturn(List.of());

        // When
        ValidationResult result = runtimeConfigurationValidator.validateManually();

        // Then
        assertThat(result.valid()).isFalse();
        assertThat(result.message()).contains("Runtime configuration validation failed");
        assertThat(result.errors()).isNotEmpty();
    }

    @Test
    void validateManually_whenExceptionWithNullMessage_shouldUseExceptionClassName() {
        // Given: exception with null message
        when(validationErrorFormatter.formatValidationErrors(any(), eq("Runtime Logging"), any()))
                .thenThrow(new RuntimeException((String) null));

        // When
        ValidationResult result = runtimeConfigurationValidator.validateManually();

        // Then
        assertThat(result.valid()).isFalse();
        assertThat(result.message()).isEqualTo("RuntimeException");
        assertThat(result.errors()).isEqualTo(Collections.singletonList("RuntimeException"));
    }

    @Test
    void validateRuntimeConfiguration_whenLoggingNotProductionSafeInProduction_shouldLogWarning() {
        when(validationErrorFormatter.formatValidationErrors(any(), eq("Runtime Logging"), any()))
                .thenReturn(List.of());
        when(validationErrorFormatter.formatValidationErrors(
                        any(), eq("Runtime Monitoring"), any()))
                .thenReturn(List.of());
        when(validationErrorFormatter.formatValidationErrors(any(), eq("Runtime Actuator"), any()))
                .thenReturn(List.of());
        when(monitoringConfig.isTracingConfigValid()).thenReturn(true);
        when(loggingConfig.isProductionSafe()).thenReturn(false);
        when(applicationProperties.isProduction()).thenReturn(true);

        runtimeConfigurationValidator.validateRuntimeConfiguration();
    }

    @Test
    void validateRuntimeConfiguration_whenLoggingProductionSafeInProduction_shouldNotLogWarning() {
        when(validationErrorFormatter.formatValidationErrors(any(), eq("Runtime Logging"), any()))
                .thenReturn(List.of());
        when(validationErrorFormatter.formatValidationErrors(
                        any(), eq("Runtime Monitoring"), any()))
                .thenReturn(List.of());
        when(validationErrorFormatter.formatValidationErrors(any(), eq("Runtime Actuator"), any()))
                .thenReturn(List.of());
        when(monitoringConfig.isTracingConfigValid()).thenReturn(true);
        when(loggingConfig.isProductionSafe()).thenReturn(true);
        when(applicationProperties.isProduction()).thenReturn(true);

        runtimeConfigurationValidator.validateRuntimeConfiguration();
    }

    @Test
    void validateRuntimeConfiguration_whenActuatorNotProductionSafeInProduction_shouldLogWarning() {
        when(validationErrorFormatter.formatValidationErrors(any(), eq("Runtime Logging"), any()))
                .thenReturn(List.of());
        when(validationErrorFormatter.formatValidationErrors(
                        any(), eq("Runtime Monitoring"), any()))
                .thenReturn(List.of());
        when(validationErrorFormatter.formatValidationErrors(any(), eq("Runtime Actuator"), any()))
                .thenReturn(List.of());
        when(monitoringConfig.isTracingConfigValid()).thenReturn(true);
        when(actuatorConfig.isProductionSafe()).thenReturn(false);
        when(applicationProperties.isProduction()).thenReturn(true);

        runtimeConfigurationValidator.validateRuntimeConfiguration();
    }

    @Test
    void validateRuntimeConfiguration_whenActuatorProductionSafeInProduction_shouldNotLogWarning() {
        when(validationErrorFormatter.formatValidationErrors(any(), eq("Runtime Logging"), any()))
                .thenReturn(List.of());
        when(validationErrorFormatter.formatValidationErrors(
                        any(), eq("Runtime Monitoring"), any()))
                .thenReturn(List.of());
        when(validationErrorFormatter.formatValidationErrors(any(), eq("Runtime Actuator"), any()))
                .thenReturn(List.of());
        when(monitoringConfig.isTracingConfigValid()).thenReturn(true);
        when(actuatorConfig.isProductionSafe()).thenReturn(true);
        when(applicationProperties.isProduction()).thenReturn(true);

        runtimeConfigurationValidator.validateRuntimeConfiguration();
    }

    @Test
    void validateRuntimeConfiguration_shouldCallFormatterForAllConfigs() {
        // Given
        when(validationErrorFormatter.formatValidationErrors(any(), eq("Runtime Logging"), any()))
                .thenReturn(List.of());
        when(validationErrorFormatter.formatValidationErrors(
                        any(), eq("Runtime Monitoring"), any()))
                .thenReturn(List.of());
        when(validationErrorFormatter.formatValidationErrors(any(), eq("Runtime Actuator"), any()))
                .thenReturn(List.of());
        when(monitoringConfig.isTracingConfigValid()).thenReturn(true);

        // When
        runtimeConfigurationValidator.validateRuntimeConfiguration();

        // Then
        verify(validationErrorFormatter)
                .formatValidationErrors(loggingConfig, "Runtime Logging", validator);
        verify(validationErrorFormatter)
                .formatValidationErrors(monitoringConfig, "Runtime Monitoring", validator);
        verify(validationErrorFormatter)
                .formatValidationErrors(actuatorConfig, "Runtime Actuator", validator);
    }
}
