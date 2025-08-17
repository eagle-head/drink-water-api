package br.com.drinkwater.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Immutable configuration properties for core application settings.
 * All properties are loaded at bootstrap time and cannot be modified at runtime.
 */
@ConfigurationProperties(prefix = "app")
@Validated
public record ApplicationProperties(
    
    @NotBlank(message = "APP_NAME environment variable is required")
    String name,
    
    @NotBlank(message = "APP_VERSION environment variable is required")
    String version,
    
    @NotBlank(message = "APP_ENVIRONMENT environment variable is required")
    @Pattern(
        regexp = "^(development|dev|staging|stage|production|prod)$", 
        message = "APP_ENVIRONMENT must be one of: development, dev, staging, stage, production, prod"
    )
    String environment
) {
    
    /**
     * Checks if the application is running in production environment.
     * @return true if environment is 'production' or 'prod'
     */
    public boolean isProduction() {
        return "production".equals(environment) || "prod".equals(environment);
    }
    
    /**
     * Checks if the application is running in development environment.
     * @return true if environment is 'development' or 'dev'
     */
    public boolean isDevelopment() {
        return "development".equals(environment) || "dev".equals(environment);
    }
    
    /**
     * Checks if the application is running in staging environment.
     * @return true if environment is 'staging' or 'stage'
     */
    public boolean isStaging() {
        return "staging".equals(environment) || "stage".equals(environment);
    }
}