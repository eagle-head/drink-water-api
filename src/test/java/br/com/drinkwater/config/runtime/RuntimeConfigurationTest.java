package br.com.drinkwater.config.runtime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for runtime configuration functionality.
 * Tests the runtime configuration classes independently.
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
        loggingConfig.setHibernate("WARN");
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
        // Initially should be false (root=INFO, sql=WARN)
        assertThat(loggingConfig.isDevelopmentMode()).isFalse();
        
        // Set root to DEBUG - should now be true
        loggingConfig.setRoot("DEBUG");
        assertThat(loggingConfig.isDevelopmentMode()).isTrue();
        
        // Set SQL to DEBUG - should also be true
        loggingConfig.setRoot("INFO"); // Reset root
        loggingConfig.setSql("DEBUG");
        assertThat(loggingConfig.isDevelopmentMode()).isTrue();
    }

    @Test
    void shouldDetectProductionSafety() {
        // With current test configuration (root=INFO, app=DEBUG), it's not production safe
        assertThat(loggingConfig.isProductionSafe()).isFalse();
        
        // Actuator with "always" is not production safe
        assertThat(actuatorConfig.isProductionSafe()).isFalse();
    }

    @Test
    void shouldValidateTracingConfiguration() {
        // Default tracing is disabled, so config should be valid
        assertThat(monitoringConfig.isTracingConfigValid()).isTrue();
        
        // Enable tracing - should still be valid with default zipkin endpoint
        monitoringConfig.setTracingEnabled(true);
        assertThat(monitoringConfig.isTracingConfigValid()).isTrue();
    }

    @Test
    void shouldUpdateLoggingLevelsAtRuntime() {
        // Test that logging levels can be changed at runtime
        loggingConfig.setRoot("DEBUG");
        loggingConfig.setApp("TRACE");
        
        assertThat(loggingConfig.getRoot()).isEqualTo("DEBUG");
        assertThat(loggingConfig.getApp()).isEqualTo("TRACE");
        assertThat(loggingConfig.isDevelopmentMode()).isTrue();
    }
}