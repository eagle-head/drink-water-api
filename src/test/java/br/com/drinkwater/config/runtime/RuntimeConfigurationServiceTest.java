package br.com.drinkwater.config.runtime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
final class RuntimeConfigurationServiceTest {

    @Mock private RuntimeLoggingConfiguration loggingConfig;

    @Mock private RuntimeMonitoringConfiguration monitoringConfig;

    @Mock private RuntimeActuatorConfiguration actuatorConfig;

    @Mock private LoggingSystem loggingSystem;

    @Mock private ApplicationEventPublisher eventPublisher;

    private RuntimeConfigurationService service;

    @BeforeEach
    void setUp() {
        when(loggingConfig.getRoot()).thenReturn("INFO");
        when(loggingConfig.getApp()).thenReturn("INFO");
        when(loggingConfig.getSecurity()).thenReturn("WARN");
        when(loggingConfig.getOauth2()).thenReturn("WARN");
        when(loggingConfig.getSql()).thenReturn("WARN");
        when(loggingConfig.getSqlParams()).thenReturn("WARN");
        when(loggingConfig.getSpringframework()).thenReturn("WARN");
        when(loggingConfig.isProductionSafe()).thenReturn(false);
        when(loggingConfig.isDevelopmentMode()).thenReturn(false);

        when(monitoringConfig.getPrometheusStep()).thenReturn(Duration.ofSeconds(15));
        when(monitoringConfig.getTracingSamplingRate()).thenReturn(0.1);
        when(monitoringConfig.getTracingEnabled()).thenReturn(false);
        when(monitoringConfig.getZipkinEndpoint()).thenReturn("http://localhost:9411/api/v2/spans");
        when(monitoringConfig.isProductionReady()).thenReturn(true);

        when(actuatorConfig.getHealthShowDetails()).thenReturn("never");
        when(actuatorConfig.getHealthShowComponents()).thenReturn(false);
        when(actuatorConfig.isProductionSafe()).thenReturn(true);

        service =
                new RuntimeConfigurationService(
                        loggingConfig,
                        monitoringConfig,
                        actuatorConfig,
                        loggingSystem,
                        eventPublisher);
    }

    @Test
    void givenValidLoggerAndLevel_whenUpdateLogLevel_thenDelegatesToLoggingSystem() {
        service.updateLogLevel("br.com.drinkwater", "DEBUG");

        verify(loggingSystem).setLogLevel("br.com.drinkwater", LogLevel.DEBUG);
    }

    @Test
    void givenInvalidLogLevel_whenUpdateLogLevel_thenCompletesWithoutCallingLoggingSystem() {
        assertThatCode(() -> service.updateLogLevel("ROOT", "INVALID")).doesNotThrowAnyException();
        verify(loggingSystem, never()).setLogLevel(any(), any());
    }

    @Test
    void whenRefreshConfiguration_thenPublishesRefreshEvent() {
        service.refreshConfiguration();

        ArgumentCaptor<RefreshEvent> eventCaptor = ArgumentCaptor.forClass(RefreshEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        RefreshEvent event = eventCaptor.getValue();
        assertThat(event.getSource()).isSameAs(service);
    }

    @Test
    void whenGetConfigurationSummary_thenReturnsAllConfigValues() {
        var summary = service.getConfigurationSummary();

        assertThat(summary).containsKeys("logging", "monitoring", "actuator", "lastUpdated");
        assertThat(summary.get("logging")).isInstanceOf(java.util.Map.class);
        @SuppressWarnings("unchecked")
        var logging = (java.util.Map<String, Object>) summary.get("logging");
        assertThat(logging).containsEntry("root", "INFO");
        assertThat(logging).containsEntry("app", "INFO");
    }

    @Test
    void whenApplyLoggingChanges_thenSetsAllLoggerLevels() {
        service.applyLoggingChanges();

        verify(loggingSystem).setLogLevel(eq("ROOT"), any(LogLevel.class));
        verify(loggingSystem).setLogLevel(eq("br.com.drinkwater"), any(LogLevel.class));
        verify(loggingSystem).setLogLevel(eq("org.springframework.security"), any(LogLevel.class));
        verify(loggingSystem)
                .setLogLevel(eq("org.springframework.security.oauth2"), any(LogLevel.class));
        verify(loggingSystem).setLogLevel(eq("org.springframework.jdbc"), any(LogLevel.class));
        verify(loggingSystem).setLogLevel(eq("org.hibernate.orm.jdbc.bind"), any(LogLevel.class));
        verify(loggingSystem).setLogLevel(eq("org.springframework"), any(LogLevel.class));
    }

    @Test
    void givenRefreshEvent_whenOnRefreshEvent_thenAppliesLoggingAndLogsSummary() {
        var event = new RefreshEvent(this, null, "test");

        service.onRefreshEvent(event);

        verify(loggingSystem).setLogLevel(eq("ROOT"), any(LogLevel.class));
    }

    @Test
    void
            givenExceptionInLogConfigurationSummary_whenOnRefreshEvent_thenCatchesAndDoesNotPropagate() {
        when(monitoringConfig.getPrometheusStep()).thenThrow(new RuntimeException("summary error"));
        var event = new RefreshEvent(this, null, "test");

        assertThatCode(() -> service.onRefreshEvent(event)).doesNotThrowAnyException();
    }

    @Test
    void givenNullLogLevel_whenUpdateLogLevel_thenDoesNotCallLoggingSystem() {
        when(loggingConfig.getRoot()).thenReturn(null);

        assertThatCode(() -> service.applyLoggingChanges()).doesNotThrowAnyException();
    }

    @Test
    void givenEmptyLogLevel_whenUpdateLogLevel_thenDoesNotCallLoggingSystem() {
        when(loggingConfig.getRoot()).thenReturn("  ");

        assertThatCode(() -> service.applyLoggingChanges()).doesNotThrowAnyException();
    }

    @Test
    void givenLoggingSystemThrows_whenUpdateLogLevel_thenExceptionHandledInternally() {
        doThrow(new RuntimeException("System error")).when(loggingSystem).setLogLevel(any(), any());

        assertThatCode(() -> service.updateLogLevel("ROOT", "DEBUG")).doesNotThrowAnyException();
    }

    @Test
    void whenGetConfigurationSummary_thenIncludesMonitoringAndActuatorDetails() {
        var summary = service.getConfigurationSummary();

        @SuppressWarnings("unchecked")
        var monitoring = (java.util.Map<String, Object>) summary.get("monitoring");
        assertThat(monitoring).containsEntry("tracingEnabled", false);
        assertThat(monitoring)
                .containsEntry("zipkinEndpoint", "http://localhost:9411/api/v2/spans");

        @SuppressWarnings("unchecked")
        var actuator = (java.util.Map<String, Object>) summary.get("actuator");
        assertThat(actuator).containsEntry("healthShowComponents", false);
        assertThat(actuator).containsEntry("productionSafe", true);
    }

    @Test
    void givenInvalidLogLevelInConfig_whenApplyLoggingChanges_thenSkipsInvalidAndContinues() {
        when(loggingConfig.getRoot()).thenReturn("INVALID_LEVEL");

        assertThatCode(() -> service.applyLoggingChanges()).doesNotThrowAnyException();
        verify(loggingSystem, never()).setLogLevel(eq("ROOT"), any(LogLevel.class));
    }
}
