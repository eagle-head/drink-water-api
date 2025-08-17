package br.com.drinkwater.config.properties;

import jakarta.validation.constraints.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

/**
 * Immutable configuration properties for monitoring and observability.
 * All properties are loaded at bootstrap time and cannot be modified at runtime.
 */
@ConfigurationProperties(prefix = "monitoring")
@Validated
public record MonitoringProperties(

        @NotNull(message = "PROMETHEUS_METRICS_ENABLED environment variable is required")
        Boolean prometheusEnabled,

        @NotNull(message = "PROMETHEUS_METRICS_EXPORT_ENABLED environment variable is required")
        Boolean prometheusExportEnabled,

        @NotNull(message = "PROMETHEUS_METRICS_STEP environment variable is required")
        Duration prometheusStep,

        @NotNull(message = "TRACING_ENABLED environment variable is required")
        Boolean tracingEnabled,

        @NotNull(message = "TRACING_SAMPLING_RATE environment variable is required")
        @DecimalMin(value = "0.0", message = "TRACING_SAMPLING_RATE must be between 0.0 and 1.0")
        @DecimalMax(value = "1.0", message = "TRACING_SAMPLING_RATE must be between 0.0 and 1.0")
        Double tracingSamplingRate,

        @NotBlank(message = "ZIPKIN_ENDPOINT environment variable is required")
        @Pattern(
                regexp = "^https?://.*",
                message = "ZIPKIN_ENDPOINT must be a valid HTTP or HTTPS URL"
        )
        String zipkinEndpoint
) {

    /**
     * Constructor with strict validation - no defaults.
     */
    public MonitoringProperties {
        // All properties are required - no defaults allowed
    }

    /**
     * Checks if monitoring is properly configured for production.
     *
     * @return true if monitoring is enabled with appropriate settings
     */
    public boolean isProductionReady() {
        return prometheusEnabled && prometheusExportEnabled && tracingEnabled && tracingSamplingRate > 0.0;
    }

    /**
     * Validates that tracing configuration is consistent.
     *
     * @return true if tracing is disabled or properly configured
     */
    public boolean isTracingConfigValid() {
        if (!tracingEnabled) {
            return true;
        }

        return zipkinEndpoint != null && !zipkinEndpoint.trim().isEmpty() && tracingSamplingRate > 0.0;
    }
}