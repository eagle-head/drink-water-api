package br.com.drinkwater.config;

import br.com.drinkwater.config.properties.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration class that enables all environment variable configuration properties.
 * <p>
 * This configuration ensures that all properties are loaded and validated at application
 * startup time. The properties are immutable after bootstrap and cannot be changed at runtime.
 * <p>
 * All property classes use record types to ensure immutability and include comprehensive
 * validation annotations to enforce fail-fast behavior.
 * <p>
 * The actual validation and fail-fast behavior is handled by:
 * <ul>
 *   <li>Individual property classes with validation annotations</li>
 *   <li>EnvironmentVariableValidator for comprehensive startup validation</li>
 *   <li>Spring Boot's ConfigurationProperties validation</li>
 * </ul>
 * <p>
 * The properties are automatically registered and injected by Spring Boot.
 * This configuration also provides the LoggingSystem bean for runtime configuration management.
 */
@Configuration
@EnableConfigurationProperties({
        ApplicationProperties.class,
        ServerProperties.class,
        DatabaseProperties.class,
        JpaProperties.class,
        SqlProperties.class,
        KeycloakProperties.class,
        CorsProperties.class,
        SecurityProperties.class,
        ActuatorProperties.class,
        MonitoringProperties.class,
        LoggingProperties.class,
        LocaleProperties.class,
        MessageSourceProperties.class,
        ContainerProperties.class
})
@Validated
public class EnvironmentVariableConfiguration {

    /**
     * Provides the LoggingSystem as a Spring bean for injection.
     * <p>
     * The LoggingSystem is required for runtime logging level changes and is not
     * automatically available as a bean in Spring Boot 3.x applications. This method
     * retrieves the current LoggingSystem instance and makes it available for dependency injection.
     * <p>
     * This bean is essential for runtime configuration management, allowing services to
     * dynamically update logging levels without application restart.
     *
     * @return the current LoggingSystem instance
     * @throws IllegalStateException if no LoggingSystem is available
     */
    @Bean
    public LoggingSystem loggingSystem() {
        LoggingSystem loggingSystem = LoggingSystem.get(getClass().getClassLoader());
        if (loggingSystem == null) {
            throw new IllegalStateException("No LoggingSystem available. This is required for runtime logging configuration.");
        }
        return loggingSystem;
    }
}