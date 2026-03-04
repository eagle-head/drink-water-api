package br.com.drinkwater.config.runtime;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for runtime configuration functionality. Tests the runtime configuration classes
 * independently.
 */
class RuntimeConfigurationTest {

    private RuntimeLoggingConfiguration loggingConfig;
    private RuntimeMonitoringConfiguration monitoringConfig;
    private RuntimeActuatorConfiguration actuatorConfig;

    @BeforeEach
    void setUp() {
        loggingConfig = new RuntimeLoggingConfiguration();
        loggingConfig.setRoot("INFO");
        loggingConfig.setApp("DEBUG");
        loggingConfig.setSecurity("WARN");
        loggingConfig.setOauth2("WARN");
        loggingConfig.setSql("WARN");
        loggingConfig.setSqlParams("WARN");
        loggingConfig.setSpringframework("WARN");

        monitoringConfig = new RuntimeMonitoringConfiguration();
        monitoringConfig.setPrometheusStep(Duration.ofSeconds(15));
        monitoringConfig.setTracingSamplingRate(0.2);
        monitoringConfig.setTracingEnabled(false);
        monitoringConfig.setPrometheusMetricsEnabled(true);
        monitoringConfig.setZipkinEndpoint("http://localhost:9411/api/v2/spans");

        actuatorConfig = new RuntimeActuatorConfiguration();
        actuatorConfig.setHealthShowDetails("always");
        actuatorConfig.setHealthShowComponents(true);
    }

    @Test
    void shouldLoadRuntimeConfiguration() {
        // Then
        assertThat(loggingConfig).isNotNull();
        assertThat(loggingConfig.getRoot()).isEqualTo("INFO");
        assertThat(loggingConfig.getApp()).isEqualTo("DEBUG");

        assertThat(monitoringConfig).isNotNull();
        assertThat(monitoringConfig.getPrometheusStep()).hasSeconds(15);
        assertThat(monitoringConfig.getTracingSamplingRate()).isEqualTo(0.2);

        assertThat(actuatorConfig).isNotNull();
        assertThat(actuatorConfig.getHealthShowDetails()).isEqualTo("always");
    }

    @Test
    void shouldDetectDevelopmentMode() {
        // Then
        assertThat(loggingConfig.isDevelopmentMode()).isFalse();

        // When
        loggingConfig.setRoot("DEBUG");
        // Then
        assertThat(loggingConfig.isDevelopmentMode()).isTrue();

        // Given & When
        loggingConfig.setRoot("INFO"); // Reset root
        loggingConfig.setSql("DEBUG");
        // Then
        assertThat(loggingConfig.isDevelopmentMode()).isTrue();
    }

    @Test
    void shouldDetectProductionSafety() {
        // Then
        assertThat(loggingConfig.isProductionSafe()).isFalse();

        // Then
        assertThat(actuatorConfig.isProductionSafe()).isFalse();
    }

    @Test
    void shouldValidateTracingConfiguration() {
        // Then
        assertThat(monitoringConfig.isTracingConfigValid()).isTrue();

        // When
        monitoringConfig.setTracingEnabled(true);
        // Then
        assertThat(monitoringConfig.isTracingConfigValid()).isTrue();
    }

    @Test
    void shouldUpdateLoggingLevelsAtRuntime() {
        // When
        loggingConfig.setRoot("DEBUG");
        loggingConfig.setApp("TRACE");

        // Then
        assertThat(loggingConfig.getRoot()).isEqualTo("DEBUG");
        assertThat(loggingConfig.getApp()).isEqualTo("TRACE");
        assertThat(loggingConfig.isDevelopmentMode()).isTrue();
    }
}
