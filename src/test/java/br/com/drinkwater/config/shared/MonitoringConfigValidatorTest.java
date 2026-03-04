package br.com.drinkwater.config.shared;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Tests for MonitoringConfigValidator utility class to ensure isProductionReady and
 * isTracingConfigValid evaluate monitoring configuration correctly.
 */
final class MonitoringConfigValidatorTest {

    @Test
    void givenAllTrueAndPositiveRate_whenIsProductionReady_thenReturnsTrue() {
        // Given
        boolean prometheusEnabled = true;
        boolean tracingEnabled = true;
        double rate = 0.1;

        // When
        boolean result =
                MonitoringConfigValidator.isProductionReady(
                        prometheusEnabled, tracingEnabled, rate);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void givenPrometheusFalse_whenIsProductionReady_thenReturnsFalse() {
        // Given
        boolean prometheusEnabled = false;
        boolean tracingEnabled = true;
        double rate = 0.1;

        // When
        boolean result =
                MonitoringConfigValidator.isProductionReady(
                        prometheusEnabled, tracingEnabled, rate);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void givenTracingDisabled_whenIsProductionReady_thenReturnsFalse() {
        // Given
        boolean result = MonitoringConfigValidator.isProductionReady(true, false, 0.1);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void givenZeroSamplingRate_whenIsProductionReady_thenReturnsFalse() {
        // Given
        boolean result = MonitoringConfigValidator.isProductionReady(true, true, 0.0);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void givenTracingDisabled_whenIsTracingConfigValid_thenReturnsTrue() {
        // Given
        boolean tracingEnabled = false;
        String endpoint = null;
        double rate = 0.0;

        // When
        boolean result =
                MonitoringConfigValidator.isTracingConfigValid(tracingEnabled, endpoint, rate);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void givenTracingEnabledWithNullEndpoint_whenIsTracingConfigValid_thenReturnsFalse() {
        // Given
        boolean tracingEnabled = true;
        String endpoint = null;
        double rate = 0.1;

        // When
        boolean result =
                MonitoringConfigValidator.isTracingConfigValid(tracingEnabled, endpoint, rate);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void givenTracingEnabledWithEmptyEndpoint_whenIsTracingConfigValid_thenReturnsFalse() {
        // Given
        boolean tracingEnabled = true;
        String endpoint = "";
        double rate = 0.1;

        // When
        boolean result =
                MonitoringConfigValidator.isTracingConfigValid(tracingEnabled, endpoint, rate);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void givenTracingEnabledWithBlankEndpoint_whenIsTracingConfigValid_thenReturnsFalse() {
        // Given
        boolean tracingEnabled = true;
        String endpoint = "   ";
        double rate = 0.1;

        // When
        boolean result =
                MonitoringConfigValidator.isTracingConfigValid(tracingEnabled, endpoint, rate);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void givenTracingEnabledWithZeroRate_whenIsTracingConfigValid_thenReturnsFalse() {
        // Given
        boolean tracingEnabled = true;
        String endpoint = "https://zipkin.example.com";
        double rate = 0.0;

        // When
        boolean result =
                MonitoringConfigValidator.isTracingConfigValid(tracingEnabled, endpoint, rate);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void givenTracingEnabledWithValidConfig_whenIsTracingConfigValid_thenReturnsTrue() {
        // Given
        boolean tracingEnabled = true;
        String endpoint = "https://zipkin.example.com";
        double rate = 0.1;

        // When
        boolean result =
                MonitoringConfigValidator.isTracingConfigValid(tracingEnabled, endpoint, rate);

        // Then
        assertThat(result).isTrue();
    }
}
