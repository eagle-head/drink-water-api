package br.com.drinkwater.config.runtime;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link RuntimeMonitoringConfiguration}. Tests production readiness, tracing config
 * validation, and getters/setters.
 */
final class RuntimeMonitoringConfigurationTest {

    private RuntimeMonitoringConfiguration config;

    @BeforeEach
    void setUp() {
        config = new RuntimeMonitoringConfiguration();
        config.setPrometheusStep(Duration.ofSeconds(10));
        config.setTracingSamplingRate(0.1);
        config.setZipkinEndpoint("http://localhost:9411/api/v2/spans");
        config.setTracingEnabled(false);
        config.setPrometheusMetricsEnabled(true);
    }

    @Test
    void
            isProductionReady_whenPrometheusEnabledAndTracingEnabledAndRatePositive_shouldReturnTrue() {
        // Given
        config.setPrometheusMetricsEnabled(true);
        config.setTracingEnabled(true);
        config.setTracingSamplingRate(0.5);

        // When
        boolean result = config.isProductionReady();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void isProductionReady_whenPrometheusDisabled_shouldReturnFalse() {
        // Given
        config.setPrometheusMetricsEnabled(false);
        config.setTracingEnabled(true);
        config.setTracingSamplingRate(0.5);

        // When
        boolean result = config.isProductionReady();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void isTracingConfigValid_whenTracingDisabled_shouldReturnTrue() {
        // Given
        config.setTracingEnabled(false);

        // When
        boolean result = config.isTracingConfigValid();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void isTracingConfigValid_whenTracingEnabledWithValidEndpointAndRate_shouldReturnTrue() {
        // Given
        config.setTracingEnabled(true);
        config.setZipkinEndpoint("http://zipkin:9411/api/v2/spans");
        config.setTracingSamplingRate(0.1);

        // When
        boolean result = config.isTracingConfigValid();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void isTracingConfigValid_whenTracingEnabledWithNullEndpoint_shouldReturnFalse() {
        // Given
        config.setTracingEnabled(true);
        config.setZipkinEndpoint(null);
        config.setTracingSamplingRate(0.1);

        // When
        boolean result = config.isTracingConfigValid();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void getZipkinEndpoint_shouldReturnValue() {
        assertThat(config.getZipkinEndpoint()).isEqualTo("http://localhost:9411/api/v2/spans");
    }

    @Test
    void getPrometheusMetricsEnabled_whenSet_shouldReturnValue() {
        // Given
        config.setPrometheusMetricsEnabled(false);

        // When
        Boolean result = config.getPrometheusMetricsEnabled();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void setPrometheusMetricsEnabled_shouldUpdateValue() {
        // When
        config.setPrometheusMetricsEnabled(true);

        // Then
        assertThat(config.getPrometheusMetricsEnabled()).isTrue();
    }

    @Test
    void getTracingEnabled_whenSet_shouldReturnValue() {
        // Given
        config.setTracingEnabled(true);

        // When
        Boolean result = config.getTracingEnabled();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void setTracingEnabled_shouldUpdateValue() {
        // When
        config.setTracingEnabled(false);

        // Then
        assertThat(config.getTracingEnabled()).isFalse();
    }
}
