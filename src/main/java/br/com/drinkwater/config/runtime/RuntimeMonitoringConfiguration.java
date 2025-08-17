package br.com.drinkwater.config.runtime;

import jakarta.validation.constraints.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

/**
 * Runtime-configurable monitoring properties that can be updated without application restart.
 * Changes take effect immediately when the /actuator/refresh endpoint is called.
 */
@Component
@RefreshScope
@ConfigurationProperties(prefix = "runtime.monitoring")
@Validated
public class RuntimeMonitoringConfiguration {

    @NotNull(message = "Runtime prometheus metrics step is required")
    private Duration prometheusStep = Duration.ofSeconds(10);

    @NotNull(message = "Runtime tracing sampling rate is required")
    @DecimalMin(value = "0.0", message = "Runtime tracing sampling rate must be between 0.0 and 1.0")
    @DecimalMax(value = "1.0", message = "Runtime tracing sampling rate must be between 0.0 and 1.0")
    private Double tracingSamplingRate = 0.1;

    @NotBlank(message = "Runtime zipkin endpoint is required")
    @Pattern(
            regexp = "^https?://.*",
            message = "Runtime zipkin endpoint must be a valid HTTP or HTTPS URL"
    )
    private String zipkinEndpoint = "http://localhost:9411/api/v2/spans";

    private Boolean tracingEnabled = false;

    private Boolean prometheusMetricsEnabled = true;

    // Getters and setters
    public Duration getPrometheusStep() {
        return prometheusStep;
    }

    public void setPrometheusStep(Duration prometheusStep) {
        this.prometheusStep = prometheusStep;
    }

    public Double getTracingSamplingRate() {
        return tracingSamplingRate;
    }

    public void setTracingSamplingRate(Double tracingSamplingRate) {
        this.tracingSamplingRate = tracingSamplingRate;
    }

    public String getZipkinEndpoint() {
        return zipkinEndpoint;
    }

    public void setZipkinEndpoint(String zipkinEndpoint) {
        this.zipkinEndpoint = zipkinEndpoint;
    }

    public Boolean getTracingEnabled() {
        return tracingEnabled;
    }

    public void setTracingEnabled(Boolean tracingEnabled) {
        this.tracingEnabled = tracingEnabled;
    }

    public Boolean getPrometheusMetricsEnabled() {
        return prometheusMetricsEnabled;
    }

    public void setPrometheusMetricsEnabled(Boolean prometheusMetricsEnabled) {
        this.prometheusMetricsEnabled = prometheusMetricsEnabled;
    }

    /**
     * Checks if monitoring is properly configured for production.
     *
     * @return true if monitoring is enabled with appropriate settings
     */
    public boolean isProductionReady() {
        return prometheusMetricsEnabled && tracingEnabled && tracingSamplingRate > 0.0;
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