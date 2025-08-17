package br.com.drinkwater.config.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

/**
 * Service that manages runtime configuration changes and applies them to the running application.
 * This service coordinates between different runtime configuration components.
 * <p>
 * The service requires a LoggingSystem bean to be available for runtime logging level changes.
 * This is provided by the EnvironmentVariableConfiguration class.
 */
@Service
public class RuntimeConfigurationService {

    private static final Logger logger = LoggerFactory.getLogger(RuntimeConfigurationService.class);

    private final RuntimeLoggingConfiguration loggingConfig;
    private final RuntimeMonitoringConfiguration monitoringConfig;
    private final RuntimeActuatorConfiguration actuatorConfig;
    private final LoggingSystem loggingSystem;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Constructs a RuntimeConfigurationService with all required dependencies.
     *
     * @param loggingConfig runtime logging configuration
     * @param monitoringConfig runtime monitoring configuration
     * @param actuatorConfig runtime actuator configuration
     * @param loggingSystem Spring Boot LoggingSystem for runtime log level changes
     * @param eventPublisher Spring event publisher for configuration refresh events
     */
    public RuntimeConfigurationService(
            RuntimeLoggingConfiguration loggingConfig,
            RuntimeMonitoringConfiguration monitoringConfig,
            RuntimeActuatorConfiguration actuatorConfig,
            LoggingSystem loggingSystem,
            ApplicationEventPublisher eventPublisher) {
        this.loggingConfig = loggingConfig;
        this.monitoringConfig = monitoringConfig;
        this.actuatorConfig = actuatorConfig;
        this.loggingSystem = loggingSystem;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Applies logging configuration changes to the running application.
     * This method is called automatically when the configuration is refreshed.
     */
    @EventListener(RefreshEvent.class)
    public void onRefreshEvent(RefreshEvent event) {
        logger.info("Runtime configuration refresh event received, applying changes...");
        
        try {
            applyLoggingChanges();
            logConfigurationSummary();
            logger.info("Runtime configuration changes applied successfully");
        } catch (Exception e) {
            logger.error("Failed to apply runtime configuration changes", e);
        }
    }

    /**
     * Manually applies logging configuration changes.
     * Can be called directly to update logging levels at runtime.
     */
    public void applyLoggingChanges() {
        // Apply logging level changes
        setLogLevel("ROOT", loggingConfig.getRoot());
        setLogLevel("br.com.drinkwater", loggingConfig.getApp());
        setLogLevel("org.springframework.security", loggingConfig.getSecurity());
        setLogLevel("org.springframework.security.oauth2", loggingConfig.getOauth2());
        setLogLevel("org.hibernate.SQL", loggingConfig.getSql());
        setLogLevel("org.hibernate.type.descriptor.sql.BasicBinder", loggingConfig.getSqlParams());
        setLogLevel("org.hibernate", loggingConfig.getHibernate());
        setLogLevel("org.springframework", loggingConfig.getSpringframework());

        logger.info("Applied runtime logging configuration changes");
    }

    /**
     * Updates a specific logger level at runtime.
     *
     * @param loggerName the name of the logger
     * @param level the new log level
     */
    public void updateLogLevel(String loggerName, String level) {
        try {
            setLogLevel(loggerName, level);
            logger.info("Updated log level for '{}' to '{}'", loggerName, level);
        } catch (Exception e) {
            logger.error("Failed to update log level for '{}' to '{}'", loggerName, level, e);
            throw new RuntimeException("Failed to update log level", e);
        }
    }

    /**
     * Triggers a refresh of all runtime configurations.
     * This will publish a RefreshEvent that causes all @RefreshScope beans to be recreated.
     */
    public void refreshConfiguration() {
        logger.info("Triggering runtime configuration refresh...");
        eventPublisher.publishEvent(new RefreshEvent(this, null, "Manual refresh"));
    }

    /**
     * Gets the current runtime configuration summary.
     *
     * @return a map containing current runtime configuration values
     */
    public Map<String, Object> getConfigurationSummary() {
        return Map.of(
                "logging", Map.of(
                        "root", loggingConfig.getRoot(),
                        "app", loggingConfig.getApp(),
                        "security", loggingConfig.getSecurity(),
                        "oauth2", loggingConfig.getOauth2(),
                        "sql", loggingConfig.getSql(),
                        "productionSafe", loggingConfig.isProductionSafe(),
                        "developmentMode", loggingConfig.isDevelopmentMode()
                ),
                "monitoring", Map.of(
                        "prometheusStep", monitoringConfig.getPrometheusStep().toString(),
                        "tracingSamplingRate", monitoringConfig.getTracingSamplingRate(),
                        "tracingEnabled", monitoringConfig.getTracingEnabled(),
                        "zipkinEndpoint", monitoringConfig.getZipkinEndpoint(),
                        "productionReady", monitoringConfig.isProductionReady()
                ),
                "actuator", Map.of(
                        "healthShowDetails", actuatorConfig.getHealthShowDetails(),
                        "healthShowComponents", actuatorConfig.getHealthShowComponents(),
                        "productionSafe", actuatorConfig.isProductionSafe()
                ),
                "lastUpdated", Instant.now().toString()
        );
    }

    private void setLogLevel(String loggerName, String level) {
        if (level == null || level.trim().isEmpty()) {
            logger.debug("Skipping empty log level for logger '{}'", loggerName);
            return;
        }
        
        try {
            LogLevel logLevel = LogLevel.valueOf(level.toUpperCase().trim());
            loggingSystem.setLogLevel(loggerName, logLevel);
            logger.debug("Successfully set log level for '{}' to '{}'", loggerName, logLevel);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid log level '{}' for logger '{}', skipping. Valid levels are: TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF", 
                       level, loggerName);
        } catch (Exception e) {
            logger.error("Failed to set log level '{}' for logger '{}': {}", level, loggerName, e.getMessage());
        }
    }

    private void logConfigurationSummary() {
        StringBuilder summary = new StringBuilder(256);
        
        summary.append("=== RUNTIME CONFIGURATION SUMMARY ===").append(System.lineSeparator())
                .append("Logging Levels:").append(System.lineSeparator())
                .append("  Root: ").append(loggingConfig.getRoot()).append(System.lineSeparator())
                .append("  App: ").append(loggingConfig.getApp()).append(System.lineSeparator())
                .append("  Security: ").append(loggingConfig.getSecurity()).append(System.lineSeparator())
                .append("  OAuth2: ").append(loggingConfig.getOauth2()).append(System.lineSeparator())
                .append("  SQL: ").append(loggingConfig.getSql()).append(System.lineSeparator())
                .append("Monitoring:").append(System.lineSeparator())
                .append("  Prometheus Step: ").append(monitoringConfig.getPrometheusStep()).append(System.lineSeparator())
                .append("  Tracing Enabled: ").append(monitoringConfig.getTracingEnabled()).append(System.lineSeparator())
                .append("  Tracing Sampling Rate: ").append(monitoringConfig.getTracingSamplingRate()).append(System.lineSeparator())
                .append("Actuator:").append(System.lineSeparator())
                .append("  Health Show Details: ").append(actuatorConfig.getHealthShowDetails()).append(System.lineSeparator())
                .append("=== END RUNTIME CONFIGURATION SUMMARY ===");

        logger.info(summary.toString());
    }
}