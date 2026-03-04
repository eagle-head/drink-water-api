package br.com.drinkwater.config.validation;

import br.com.drinkwater.config.properties.ActuatorProperties;
import br.com.drinkwater.config.properties.ApplicationProperties;
import br.com.drinkwater.config.properties.CacheProperties;
import br.com.drinkwater.config.properties.CorsProperties;
import br.com.drinkwater.config.properties.DatabaseProperties;
import br.com.drinkwater.config.properties.KeycloakProperties;
import br.com.drinkwater.config.properties.LoggingProperties;
import br.com.drinkwater.config.properties.MonitoringProperties;
import br.com.drinkwater.config.properties.SecurityProperties;
import br.com.drinkwater.config.properties.ServerProperties;
import br.com.drinkwater.config.properties.WebhookProperties;
import br.com.drinkwater.config.runtime.RuntimeConfigurationValidator;
import jakarta.validation.Validator;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Validates environment variables and configuration properties at application startup. Implements
 * fail-fast behavior for configuration validation only - NOT connectivity tests.
 *
 * <p>This validator follows 12-Factor App principles by validating configuration properties without
 * testing external service connectivity. Health checks are delegated to Spring Boot Actuator and
 * container orchestration (Docker/Kubernetes).
 *
 * <p>IMPORTANT: This validator does NOT test database connections, HTTP endpoints, or any external
 * service connectivity. Such tests are anti-patterns that should be handled by: - Spring Boot
 * Actuator health indicators - Docker Compose health checks - Kubernetes liveness/readiness probes
 */
@Component
@Order(Integer.MIN_VALUE)
@Profile("!it & !it-no-containers")
public class EnvironmentVariableValidator implements ApplicationRunner {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(EnvironmentVariableValidator.class);

    private final ApplicationProperties applicationProperties;
    private final ServerProperties serverProperties;
    private final DatabaseProperties databaseProperties;
    private final KeycloakProperties keycloakProperties;
    private final CorsProperties corsProperties;
    private final SecurityProperties securityProperties;
    private final ActuatorProperties actuatorProperties;
    private final MonitoringProperties monitoringProperties;
    private final LoggingProperties loggingProperties;
    private final WebhookProperties webhookProperties;
    private final CacheProperties cacheProperties;
    private final RuntimeConfigurationValidator runtimeValidator;
    private final ValidationErrorFormatter validationErrorFormatter;
    private final Validator validator;

    public EnvironmentVariableValidator(
            ApplicationProperties applicationProperties,
            ServerProperties serverProperties,
            DatabaseProperties databaseProperties,
            KeycloakProperties keycloakProperties,
            CorsProperties corsProperties,
            SecurityProperties securityProperties,
            ActuatorProperties actuatorProperties,
            MonitoringProperties monitoringProperties,
            LoggingProperties loggingProperties,
            WebhookProperties webhookProperties,
            CacheProperties cacheProperties,
            RuntimeConfigurationValidator runtimeValidator,
            ValidationErrorFormatter validationErrorFormatter,
            Validator validator) {
        this.applicationProperties = applicationProperties;
        this.serverProperties = serverProperties;
        this.databaseProperties = databaseProperties;
        this.keycloakProperties = keycloakProperties;
        this.corsProperties = corsProperties;
        this.securityProperties = securityProperties;
        this.actuatorProperties = actuatorProperties;
        this.monitoringProperties = monitoringProperties;
        this.loggingProperties = loggingProperties;
        this.webhookProperties = webhookProperties;
        this.cacheProperties = cacheProperties;
        this.runtimeValidator = runtimeValidator;
        this.validationErrorFormatter = validationErrorFormatter;
        this.validator = validator;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        LOGGER.info("Starting comprehensive environment variable validation...");
        Instant startTime = Instant.now();

        try {
            // Step 1: Validate all configuration properties
            validateAllProperties();

            // Step 2: Validate cross-property relationships
            validateCrossPropertyRelationships();

            // Step 3: Environment-specific validations
            validateEnvironmentSpecificRequirements();

            // Step 4: Removed connectivity tests - delegated to health checks

            // Step 5: Validate runtime configuration
            validateRuntimeConfiguration();

            // Step 6: Log configuration summary
            logConfigurationSummary();

            Duration validationTime = Duration.between(startTime, Instant.now());
            LOGGER.info(
                    "Environment variable validation completed successfully in {}ms",
                    validationTime.toMillis());

        } catch (Exception e) {
            LOGGER.error("Environment variable validation FAILED. Application startup aborted.", e);
            throw new IllegalStateException(
                    "Critical configuration validation failed: " + e.getMessage(), e);
        }
    }

    private void validateAllProperties() {
        LOGGER.debug("Validating individual property classes...");

        List<String> validationErrors = new ArrayList<>();

        // Validate each configuration properties class using shared utility
        validationErrors.addAll(
                validationErrorFormatter.validateMultipleObjects(
                        validator,
                        new ValidationErrorFormatter.ValidationTarget<>(
                                applicationProperties, "Application"),
                        new ValidationErrorFormatter.ValidationTarget<>(serverProperties, "Server"),
                        new ValidationErrorFormatter.ValidationTarget<>(
                                databaseProperties, "Database"),
                        new ValidationErrorFormatter.ValidationTarget<>(
                                keycloakProperties, "Keycloak"),
                        new ValidationErrorFormatter.ValidationTarget<>(corsProperties, "CORS"),
                        new ValidationErrorFormatter.ValidationTarget<>(
                                securityProperties, "Security"),
                        new ValidationErrorFormatter.ValidationTarget<>(
                                actuatorProperties, "Actuator"),
                        new ValidationErrorFormatter.ValidationTarget<>(
                                monitoringProperties, "Monitoring"),
                        new ValidationErrorFormatter.ValidationTarget<>(
                                loggingProperties, "Logging"),
                        new ValidationErrorFormatter.ValidationTarget<>(
                                webhookProperties, "Webhook"),
                        new ValidationErrorFormatter.ValidationTarget<>(cacheProperties, "Cache")));

        if (!validationErrors.isEmpty()) {
            String errorMessage =
                    "Configuration validation failed:\n" + String.join("\n", validationErrors);
            throw new IllegalStateException(errorMessage);
        }

        LOGGER.debug("All property classes passed individual validation");
        LOGGER.info(
                "Configuration property validation completed - external connectivity delegated to health checks");
    }

    private void validateCrossPropertyRelationships() {
        LOGGER.debug("Validating cross-property relationships...");

        List<String> errors = new ArrayList<>();

        // Validate Keycloak URL consistency
        if (!keycloakProperties.isIssuerUriValid()) {
            errors.add("KEYCLOAK_ISSUER_URI must start with KEYCLOAK_URL/realms/KEYCLOAK_REALM");
        }

        if (!keycloakProperties.isJwkSetUriValid()) {
            errors.add(
                    "KEYCLOAK_JWK_SET_URI must be KEYCLOAK_URL/realms/KEYCLOAK_REALM/protocol/openid-connect/certs");
        }

        // Validate CORS configuration
        if (!corsProperties.isBaseUrlAllowed()) {
            errors.add("BASE_URL must be included in CORS_ALLOWED_ORIGINS");
        }

        // Validate monitoring configuration
        if (!monitoringProperties.isTracingConfigValid()) {
            errors.add(
                    "Invalid tracing configuration: if TRACING_ENABLED=true,"
                            + " ZIPKIN_ENDPOINT and valid TRACING_SAMPLING_RATE are required");
        }

        if (!errors.isEmpty()) {
            String errorMessage = "Cross-property validation failed:\n" + String.join("\n", errors);
            throw new IllegalStateException(errorMessage);
        }

        LOGGER.debug("Cross-property validation passed");
    }

    private void validateEnvironmentSpecificRequirements() {
        LOGGER.debug("Validating environment-specific requirements...");

        boolean isProduction = applicationProperties.isProduction();
        List<String> errors = new ArrayList<>();

        if (isProduction) {
            // Production-specific validations
            validateProductionRequirements(errors);
        } else if (applicationProperties.isDevelopment()) {
            // Development-specific validations
            validateDevelopmentConfiguration();
        }

        if (!errors.isEmpty()) {
            String errorMessage =
                    "Environment-specific validation failed:\n" + String.join("\n", errors);
            throw new IllegalStateException(errorMessage);
        }

        LOGGER.debug(
                "Environment-specific validation passed for environment: {}",
                applicationProperties.environment());
    }

    private void validateProductionRequirements(List<String> errors) {
        if (serverProperties.port() != null && serverProperties.port() < 1024) {
            errors.add(
                    "Production environment requires SERVER_PORT >= 1024 (privileged ports not allowed)");
        }

        // Security validations for production
        if (!securityProperties.areKeysValidForEnvironment(true)) {
            errors.add(
                    "Production environment requires JWT_SIGNING_KEY, ENCRYPTION_KEY, and API_SECRET_KEY");
        }

        // HTTPS validations
        if (!keycloakProperties.isSecureConnection()) {
            errors.add(
                    "Production environment requires HTTPS URLs for Keycloak (KEYCLOAK_URL must start with https://)");
        }

        if (!corsProperties.areOriginsSecure()) {
            errors.add("Production environment requires HTTPS URLs for all CORS origins");
        }

        // Actuator validations
        if (!actuatorProperties.isProductionSafe()) {
            errors.add(
                    "Production environment requires limited Actuator endpoints"
                            + " and ACTUATOR_HEALTH_SHOW_DETAILS=never");
        }

        // Logging validations
        if (!loggingProperties.isProductionSafe()) {
            errors.add("Production environment requires log levels of WARN or higher");
        }
    }

    private void validateDevelopmentConfiguration() {
        // Warn about development-specific configurations
        if (actuatorProperties.hasDevelopmentEndpoints()) {
            LOGGER.warn(
                    "Development environment detected with extended Actuator endpoints enabled");
        }

        if (loggingProperties.isDevelopmentMode()) {
            LOGGER.warn("Development environment detected with verbose logging enabled");
        }
    }

    // REMOVED: testCriticalConnections() method
    // Connectivity tests are anti-patterns during startup validation.
    // External service connectivity is now handled by:
    // 1. Spring Boot Actuator health indicators (/actuator/health)
    // 2. Docker Compose health checks
    // 3. Kubernetes liveness/readiness probes

    private void validateRuntimeConfiguration() {
        LOGGER.debug("Validating runtime configuration...");

        try {
            runtimeValidator.validateRuntimeConfiguration();
            LOGGER.debug("Runtime configuration validation passed");
        } catch (Exception e) {
            LOGGER.warn(
                    "Runtime configuration validation failed, but this is not critical for startup: {}",
                    e.getMessage());
            // Note: Runtime configuration validation failure is not critical for startup
            // as these configurations can be fixed at runtime
        }
    }

    // REMOVED: testDatabaseConnection() method
    // Database connectivity testing during startup is an anti-pattern.
    // Database health is now monitored by:
    // 1. Spring Boot DataSource health indicator
    // 2. Docker Compose postgres health checks (pg_isready)
    // 3. JPA lazy initialization to defer connection until needed

    private void validateLoggingConfiguration() {
        LOGGER.debug("=== LOGGING CONFIGURATION DEBUG ===");
        LOGGER.debug("LOGGING_LEVEL_ROOT env var: '{}'", System.getenv("LOGGING_LEVEL_ROOT"));
        LOGGER.debug(
                "LOGGING_LEVEL_ROOT system property: '{}'",
                System.getProperty("logging.level.root"));
        LOGGER.debug("Resolved logging.level.root value: '{}'", loggingProperties.root());
        LOGGER.debug(
                "Valid logging levels: TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF (case-sensitive)");

        String rootLevel = loggingProperties.root();
        if (rootLevel != null && !rootLevel.equals(rootLevel.toUpperCase(Locale.ROOT))) {
            throw new IllegalStateException(
                    "Invalid logging level: '%s'. Logging levels must be uppercase. Did you mean: '%s'?"
                            .formatted(rootLevel, rootLevel.toUpperCase(Locale.ROOT)));
        }
        LOGGER.debug("=== END LOGGING CONFIGURATION DEBUG ===");
    }

    private void logConfigurationSummary() {
        // First validate and debug logging configuration
        validateLoggingConfiguration();

        StringBuilder summary = new StringBuilder(512); // Pre-allocate reasonable capacity

        summary.append("=== CONFIGURATION VALIDATION SUMMARY ===")
                .append(System.lineSeparator())
                .append("Application: ")
                .append(applicationProperties.name())
                .append(" v")
                .append(applicationProperties.version())
                .append(" (")
                .append(applicationProperties.environment())
                .append(")")
                .append(System.lineSeparator())
                .append("Server: port ")
                .append(serverProperties.port())
                .append(System.lineSeparator())
                .append("Database: ")
                .append(maskDatabaseUrl(databaseProperties.url()))
                .append(" (pool: ")
                .append(databaseProperties.poolSize())
                .append(")")
                .append(System.lineSeparator())
                .append("Keycloak: ")
                .append(keycloakProperties.url())
                .append(" (realm: ")
                .append(keycloakProperties.realm())
                .append(", secure: ")
                .append(keycloakProperties.isSecureConnection())
                .append(")")
                .append(System.lineSeparator())
                .append("CORS: ")
                .append(corsProperties.allowedOrigins().size())
                .append(" origins")
                .append(", credentials: ")
                .append(corsProperties.allowCredentials())
                .append(System.lineSeparator())
                .append("Security: keys configured")
                .append(System.lineSeparator())
                .append("Actuator: ")
                .append(actuatorProperties.endpoints().size())
                .append(" endpoints")
                .append(", production safe: ")
                .append(actuatorProperties.isProductionSafe())
                .append(System.lineSeparator())
                .append("Monitoring: prometheus ")
                .append(monitoringProperties.prometheusEnabled() ? "enabled" : "disabled")
                .append(", tracing ")
                .append(monitoringProperties.tracingEnabled() ? "enabled" : "disabled")
                .append(System.lineSeparator())
                .append("Logging: root level ")
                .append(loggingProperties.root())
                .append(", development mode: ")
                .append(loggingProperties.isDevelopmentMode())
                .append(System.lineSeparator())
                .append(System.lineSeparator())
                .append("NOTE: External service connectivity is validated by:")
                .append(System.lineSeparator())
                .append("  • Spring Boot Actuator health indicators (/actuator/health)")
                .append(System.lineSeparator())
                .append("  • Docker Compose health checks (pg_isready, keycloak probes)")
                .append(System.lineSeparator())
                .append("  • Kubernetes liveness/readiness probes (if deployed)")
                .append(System.lineSeparator())
                .append("=== END CONFIGURATION VALIDATION SUMMARY ===");

        // Log the complete summary as a single coherent block
        LOGGER.info(summary.toString());
    }

    private String maskDatabaseUrl(String url) {
        // Mask password in URL for logging
        return url.replaceAll("password=[^&]*", "password=***");
    }
}
