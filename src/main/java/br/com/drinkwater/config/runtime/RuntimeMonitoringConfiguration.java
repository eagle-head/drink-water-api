package br.com.drinkwater.config.runtime;

import br.com.drinkwater.config.shared.MonitoringConfigValidator;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Runtime-configurable monitoring properties that can be updated without application restart.
 *
 * <p><strong>Important:</strong> These properties are <em>informational only</em>. While values are
 * refreshed in this bean when {@code /actuator/refresh} is called, the underlying Prometheus {@code
 * MeterRegistry} and Zipkin tracing infrastructure are not dynamically reconfigured. A full
 * application restart is required for monitoring infrastructure changes to take effect. This bean
 * is useful for querying the <em>desired</em> runtime configuration and for validation purposes.
 */
@Component
@RefreshScope
@ConfigurationProperties(prefix = "runtime.monitoring")
@Validated
public class RuntimeMonitoringConfiguration {

    @NotNull(message = "Runtime prometheus metrics step is required")
    private Duration prometheusStep = Duration.ofSeconds(10);

    @NotNull(message = "Runtime tracing sampling rate is required")
    @DecimalMin(
            value = "0.0",
            message = "Runtime tracing sampling rate must be between 0.0 and 1.0")
    @DecimalMax(
            value = "1.0",
            message = "Runtime tracing sampling rate must be between 0.0 and 1.0")
    private Double tracingSamplingRate = 0.1;

    @NotBlank(message = "Runtime zipkin endpoint is required")
    @Pattern(
            regexp = "^https?://.*",
            message = "Runtime zipkin endpoint must be a valid HTTP or HTTPS URL")
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
        return MonitoringConfigValidator.isProductionReady(
                prometheusMetricsEnabled, tracingEnabled, tracingSamplingRate);
    }

    /**
     * Validates that tracing configuration is consistent.
     *
     * @return true if tracing is disabled or properly configured
     */
    public boolean isTracingConfigValid() {
        return MonitoringConfigValidator.isTracingConfigValid(
                tracingEnabled, zipkinEndpoint, tracingSamplingRate);
    }
}
