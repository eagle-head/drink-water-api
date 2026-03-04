package br.com.drinkwater.config.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import br.com.drinkwater.config.properties.*;
import br.com.drinkwater.config.runtime.RuntimeConfigurationValidator;
import jakarta.validation.Validator;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.ApplicationArguments;

/**
 * Production-environment tests for EnvironmentVariableValidator. Covers production requirements,
 * development configuration warnings, and logging configuration validation.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
final class EnvironmentVariableValidatorProductionTest {

    @Mock private Validator validator;

    @Mock private RuntimeConfigurationValidator runtimeValidator;

    @Mock private ValidationErrorFormatter validationErrorFormatter;

    @Mock private ApplicationArguments args;

    @BeforeEach
    void setUp() {
        doReturn(List.of())
                .when(validationErrorFormatter)
                .validateMultipleObjects(
                        any(Validator.class),
                        any(ValidationErrorFormatter.ValidationTarget[].class));
    }

    @Test
    void givenValidProductionConfig_whenRun_thenPasses() throws Exception {
        var validator =
                createProductionValidator(
                        new ApplicationProperties("drink-water-api", "1.0.0", "production"),
                        new ServerProperties(8080, "on_param"),
                        new DatabaseProperties(
                                "jdbc:postgresql://localhost:5432/prod",
                                "user",
                                "password",
                                "org.postgresql.Driver",
                                10,
                                2,
                                30000L),
                        new KeycloakProperties(
                                "https://keycloak.example.com",
                                "realm",
                                "client",
                                "admin",
                                "adminpass123",
                                "https://keycloak.example.com/realms/realm",
                                "https://keycloak.example.com/realms/realm/protocol/openid-connect/certs",
                                null),
                        new CorsProperties(
                                "https://app.example.com",
                                "https://app.example.com",
                                List.of("https://app.example.com"),
                                List.of("GET", "POST"),
                                List.of("Authorization"),
                                true,
                                3600L),
                        new SecurityProperties("jwt-key", "enc-key", "api-secret"),
                        new ActuatorProperties(
                                List.of("health", "info"), "/actuator", "never", "never"),
                        new MonitoringProperties(
                                true,
                                true,
                                Duration.ofSeconds(10),
                                false,
                                0.1,
                                "https://zipkin.example.com/api/v2/spans"),
                        new LoggingProperties(
                                "WARN", "WARN", "WARN", "WARN", "WARN", "WARN", "WARN"),
                        new WebhookProperties("webhook-secret"),
                        new CacheProperties(10_000, 5));

        assertDoesNotThrow(() -> validator.run(args));
    }

    @Test
    void givenProductionWithPrivilegedPort_whenRun_thenFails() {
        var validator =
                createProductionValidator(
                        new ApplicationProperties("drink-water-api", "1.0.0", "production"),
                        new ServerProperties(80, "on_param"),
                        defaultDatabase(),
                        defaultKeycloakHttps(),
                        defaultCorsHttps(),
                        new SecurityProperties("jwt-key", "enc-key", "api-secret"),
                        defaultActuatorProductionSafe(),
                        defaultMonitoring(),
                        defaultLoggingProductionSafe(),
                        new WebhookProperties("secret"),
                        new CacheProperties(10_000, 5));

        var ex = assertThrows(IllegalStateException.class, () -> validator.run(args));
        assertTrue(ex.getMessage().contains("Environment-specific validation failed"));
        assertTrue(ex.getMessage().contains("SERVER_PORT"));
    }

    @Test
    void givenProductionWithInvalidSecurityKeys_whenRun_thenFails() {
        var validator =
                createProductionValidator(
                        new ApplicationProperties("drink-water-api", "1.0.0", "production"),
                        new ServerProperties(8080, "on_param"),
                        defaultDatabase(),
                        defaultKeycloakHttps(),
                        defaultCorsHttps(),
                        new SecurityProperties(null, null, null),
                        defaultActuatorProductionSafe(),
                        defaultMonitoring(),
                        defaultLoggingProductionSafe(),
                        new WebhookProperties("secret"),
                        new CacheProperties(10_000, 5));

        var ex = assertThrows(IllegalStateException.class, () -> validator.run(args));
        assertTrue(ex.getMessage().contains("Environment-specific validation failed"));
        assertTrue(ex.getMessage().contains("JWT_SIGNING_KEY"));
    }

    @Test
    void givenProductionWithHttpKeycloak_whenRun_thenFails() {
        var keycloak =
                new KeycloakProperties(
                        "http://keycloak.example.com",
                        "realm",
                        "client",
                        "admin",
                        "adminpass123",
                        "http://keycloak.example.com/realms/realm",
                        "http://keycloak.example.com/realms/realm/protocol/openid-connect/certs",
                        null);
        var validator =
                createProductionValidator(
                        new ApplicationProperties("drink-water-api", "1.0.0", "production"),
                        new ServerProperties(8080, "on_param"),
                        defaultDatabase(),
                        keycloak,
                        defaultCorsHttps(),
                        new SecurityProperties("jwt-key", "enc-key", "api-secret"),
                        defaultActuatorProductionSafe(),
                        defaultMonitoring(),
                        defaultLoggingProductionSafe(),
                        new WebhookProperties("secret"),
                        new CacheProperties(10_000, 5));

        var ex = assertThrows(IllegalStateException.class, () -> validator.run(args));
        assertTrue(ex.getMessage().contains("Environment-specific validation failed"));
        assertTrue(ex.getMessage().contains("HTTPS"));
    }

    @Test
    void givenProductionWithInsecureCorsOrigins_whenRun_thenFails() {
        var cors =
                new CorsProperties(
                        "https://app.example.com",
                        "https://app.example.com",
                        List.of("https://app.example.com", "http://insecure.example.com"),
                        List.of("GET", "POST"),
                        List.of("Authorization"),
                        true,
                        3600L);
        var validator =
                createProductionValidator(
                        new ApplicationProperties("drink-water-api", "1.0.0", "production"),
                        new ServerProperties(8080, "on_param"),
                        defaultDatabase(),
                        defaultKeycloakHttps(),
                        cors,
                        new SecurityProperties("jwt-key", "enc-key", "api-secret"),
                        defaultActuatorProductionSafe(),
                        defaultMonitoring(),
                        defaultLoggingProductionSafe(),
                        new WebhookProperties("secret"),
                        new CacheProperties(10_000, 5));

        var ex = assertThrows(IllegalStateException.class, () -> validator.run(args));
        assertTrue(ex.getMessage().contains("Environment-specific validation failed"));
        assertTrue(ex.getMessage().contains("CORS"));
    }

    @Test
    void givenProductionWithDevelopmentActuatorEndpoints_whenRun_thenFails() {
        var actuator =
                new ActuatorProperties(
                        List.of("health", "info", "env", "loggers"), "/actuator", "never", "never");
        var validator =
                createProductionValidator(
                        new ApplicationProperties("drink-water-api", "1.0.0", "production"),
                        new ServerProperties(8080, "on_param"),
                        defaultDatabase(),
                        defaultKeycloakHttps(),
                        defaultCorsHttps(),
                        new SecurityProperties("jwt-key", "enc-key", "api-secret"),
                        actuator,
                        defaultMonitoring(),
                        defaultLoggingProductionSafe(),
                        new WebhookProperties("secret"),
                        new CacheProperties(10_000, 5));

        var ex = assertThrows(IllegalStateException.class, () -> validator.run(args));
        assertTrue(ex.getMessage().contains("Environment-specific validation failed"));
        assertTrue(ex.getMessage().contains("Actuator"));
    }

    @Test
    void givenProductionWithVerboseLogging_whenRun_thenFails() {
        var logging = new LoggingProperties("INFO", "INFO", "WARN", "WARN", "WARN", "WARN", "WARN");
        var validator =
                createProductionValidator(
                        new ApplicationProperties("drink-water-api", "1.0.0", "production"),
                        new ServerProperties(8080, "on_param"),
                        defaultDatabase(),
                        defaultKeycloakHttps(),
                        defaultCorsHttps(),
                        new SecurityProperties("jwt-key", "enc-key", "api-secret"),
                        defaultActuatorProductionSafe(),
                        defaultMonitoring(),
                        logging,
                        new WebhookProperties("secret"),
                        new CacheProperties(10_000, 5));

        var ex = assertThrows(IllegalStateException.class, () -> validator.run(args));
        assertTrue(ex.getMessage().contains("Environment-specific validation failed"));
        assertTrue(ex.getMessage().contains("log levels"));
    }

    @Test
    void givenDevelopmentEnv_whenRun_thenPassesWithWarnings() throws Exception {
        var validator =
                createProductionValidator(
                        new ApplicationProperties("drink-water-api", "1.0.0", "development"),
                        new ServerProperties(8081, "on_param"),
                        defaultDatabase(),
                        new KeycloakProperties(
                                "http://localhost:8080",
                                "realm",
                                "client",
                                "admin",
                                "adminpass123",
                                "http://localhost:8080/realms/realm",
                                "http://localhost:8080/realms/realm/protocol/openid-connect/certs",
                                null),
                        new CorsProperties(
                                "http://localhost:3000",
                                "http://localhost:3000",
                                List.of("http://localhost:3000"),
                                List.of("GET", "POST"),
                                List.of("Authorization"),
                                true,
                                3600L),
                        new SecurityProperties(null, null, null),
                        new ActuatorProperties(
                                List.of("health", "info", "loggers"),
                                "/actuator",
                                "always",
                                "always"),
                        defaultMonitoring(),
                        new LoggingProperties(
                                "DEBUG", "DEBUG", "WARN", "WARN", "WARN", "WARN", "WARN"),
                        new WebhookProperties("secret"),
                        new CacheProperties(10_000, 5));

        assertDoesNotThrow(() -> validator.run(args));
    }

    @Test
    void givenStagingEnvironment_whenRun_thenPassesWithoutEnvironmentSpecificValidation()
            throws Exception {
        var validator =
                createProductionValidator(
                        new ApplicationProperties("drink-water-api", "1.0.0", "staging"),
                        new ServerProperties(8080, "on_param"),
                        defaultDatabase(),
                        defaultKeycloakHttps(),
                        defaultCorsHttps(),
                        new SecurityProperties("jwt-key", "enc-key", "api-secret"),
                        defaultActuatorProductionSafe(),
                        defaultMonitoring(),
                        defaultLoggingProductionSafe(),
                        new WebhookProperties("webhook-secret"),
                        new CacheProperties(10_000, 5));

        assertDoesNotThrow(() -> validator.run(args));
    }

    @Test
    void givenProductionWithNullPort_whenRun_thenPassesPortCheck() throws Exception {
        var validator =
                createProductionValidator(
                        new ApplicationProperties("drink-water-api", "1.0.0", "production"),
                        new ServerProperties(null, "on_param"),
                        defaultDatabase(),
                        defaultKeycloakHttps(),
                        defaultCorsHttps(),
                        new SecurityProperties("jwt-key", "enc-key", "api-secret"),
                        defaultActuatorProductionSafe(),
                        defaultMonitoring(),
                        defaultLoggingProductionSafe(),
                        new WebhookProperties("webhook-secret"),
                        new CacheProperties(10_000, 5));

        assertDoesNotThrow(() -> validator.run(args));
    }

    @Test
    void givenNullLoggingRoot_whenRun_thenPassesLoggingValidation() throws Exception {
        var logging = new LoggingProperties(null, "INFO", "WARN", "WARN", "WARN", "WARN", "WARN");
        var validator =
                createProductionValidator(
                        new ApplicationProperties("drink-water-api", "1.0.0", "development"),
                        new ServerProperties(8081, "on_param"),
                        defaultDatabase(),
                        new KeycloakProperties(
                                "http://localhost:8080",
                                "realm",
                                "client",
                                "admin",
                                "adminpass123",
                                "http://localhost:8080/realms/realm",
                                "http://localhost:8080/realms/realm/protocol/openid-connect/certs",
                                null),
                        new CorsProperties(
                                "http://localhost:3000",
                                "http://localhost:3000",
                                List.of("http://localhost:3000"),
                                List.of("GET", "POST"),
                                List.of("Authorization"),
                                true,
                                3600L),
                        new SecurityProperties(null, null, null),
                        new ActuatorProperties(
                                List.of("health", "info"), "/actuator", "always", "always"),
                        defaultMonitoring(),
                        logging,
                        new WebhookProperties("secret"),
                        new CacheProperties(10_000, 5));

        assertDoesNotThrow(() -> validator.run(args));
    }

    @Test
    void givenMonitoringDisabled_whenRun_thenSummaryCoversDisabledBranches() throws Exception {
        var monitoring =
                new MonitoringProperties(
                        false,
                        false,
                        Duration.ofSeconds(10),
                        false,
                        0.1,
                        "https://zipkin.example.com/api/v2/spans");
        var validator =
                createProductionValidator(
                        new ApplicationProperties("drink-water-api", "1.0.0", "development"),
                        new ServerProperties(8081, "on_param"),
                        defaultDatabase(),
                        new KeycloakProperties(
                                "http://localhost:8080",
                                "realm",
                                "client",
                                "admin",
                                "adminpass123",
                                "http://localhost:8080/realms/realm",
                                "http://localhost:8080/realms/realm/protocol/openid-connect/certs",
                                null),
                        new CorsProperties(
                                "http://localhost:3000",
                                "http://localhost:3000",
                                List.of("http://localhost:3000"),
                                List.of("GET", "POST"),
                                List.of("Authorization"),
                                true,
                                3600L),
                        new SecurityProperties(null, null, null),
                        new ActuatorProperties(
                                List.of("health", "info"), "/actuator", "always", "always"),
                        monitoring,
                        new LoggingProperties(
                                "INFO", "INFO", "WARN", "WARN", "WARN", "WARN", "WARN"),
                        new WebhookProperties("secret"),
                        new CacheProperties(10_000, 5));

        assertDoesNotThrow(() -> validator.run(args));
    }

    @Test
    void givenTracingEnabled_whenRun_thenSummaryCoversEnabledBranch() throws Exception {
        var monitoring =
                new MonitoringProperties(
                        true,
                        true,
                        Duration.ofSeconds(10),
                        true,
                        0.5,
                        "https://zipkin.example.com/api/v2/spans");
        var validator =
                createProductionValidator(
                        new ApplicationProperties("drink-water-api", "1.0.0", "development"),
                        new ServerProperties(8081, "on_param"),
                        defaultDatabase(),
                        new KeycloakProperties(
                                "http://localhost:8080",
                                "realm",
                                "client",
                                "admin",
                                "adminpass123",
                                "http://localhost:8080/realms/realm",
                                "http://localhost:8080/realms/realm/protocol/openid-connect/certs",
                                null),
                        new CorsProperties(
                                "http://localhost:3000",
                                "http://localhost:3000",
                                List.of("http://localhost:3000"),
                                List.of("GET", "POST"),
                                List.of("Authorization"),
                                true,
                                3600L),
                        new SecurityProperties(null, null, null),
                        new ActuatorProperties(
                                List.of("health", "info"), "/actuator", "always", "always"),
                        monitoring,
                        new LoggingProperties(
                                "INFO", "INFO", "WARN", "WARN", "WARN", "WARN", "WARN"),
                        new WebhookProperties("secret"),
                        new CacheProperties(10_000, 5));

        assertDoesNotThrow(() -> validator.run(args));
    }

    @Test
    void givenLoggingRootLowercase_whenRun_thenThrows() {
        var logging = new LoggingProperties("info", "INFO", "WARN", "WARN", "WARN", "WARN", "WARN");
        var validator =
                createProductionValidator(
                        new ApplicationProperties("drink-water-api", "1.0.0", "development"),
                        new ServerProperties(8081, "on_param"),
                        defaultDatabase(),
                        new KeycloakProperties(
                                "http://localhost:8080",
                                "realm",
                                "client",
                                "admin",
                                "adminpass123",
                                "http://localhost:8080/realms/realm",
                                "http://localhost:8080/realms/realm/protocol/openid-connect/certs",
                                null),
                        new CorsProperties(
                                "http://localhost:3000",
                                "http://localhost:3000",
                                List.of("http://localhost:3000"),
                                List.of("GET", "POST"),
                                List.of("Authorization"),
                                true,
                                3600L),
                        new SecurityProperties(null, null, null),
                        new ActuatorProperties(
                                List.of("health", "info"), "/actuator", "always", "always"),
                        defaultMonitoring(),
                        logging,
                        new WebhookProperties("secret"),
                        new CacheProperties(10_000, 5));

        var ex = assertThrows(IllegalStateException.class, () -> validator.run(args));
        assertTrue(ex.getMessage().contains("Invalid logging level"));
        assertTrue(ex.getMessage().contains("uppercase"));
    }

    private EnvironmentVariableValidator createProductionValidator(
            ApplicationProperties app,
            ServerProperties server,
            DatabaseProperties database,
            KeycloakProperties keycloak,
            CorsProperties cors,
            SecurityProperties security,
            ActuatorProperties actuator,
            MonitoringProperties monitoring,
            LoggingProperties logging,
            WebhookProperties webhook,
            CacheProperties cache) {
        return new EnvironmentVariableValidator(
                app,
                server,
                database,
                keycloak,
                cors,
                security,
                actuator,
                monitoring,
                logging,
                webhook,
                cache,
                runtimeValidator,
                validationErrorFormatter,
                validator);
    }

    private static DatabaseProperties defaultDatabase() {
        return new DatabaseProperties(
                "jdbc:postgresql://localhost:5432/prod",
                "user",
                "password",
                "org.postgresql.Driver",
                10,
                2,
                30000L);
    }

    private static KeycloakProperties defaultKeycloakHttps() {
        return new KeycloakProperties(
                "https://keycloak.example.com",
                "realm",
                "client",
                "admin",
                "adminpass123",
                "https://keycloak.example.com/realms/realm",
                "https://keycloak.example.com/realms/realm/protocol/openid-connect/certs",
                null);
    }

    private static CorsProperties defaultCorsHttps() {
        return new CorsProperties(
                "https://app.example.com",
                "https://app.example.com",
                List.of("https://app.example.com"),
                List.of("GET", "POST"),
                List.of("Authorization"),
                true,
                3600L);
    }

    private static ActuatorProperties defaultActuatorProductionSafe() {
        return new ActuatorProperties(List.of("health", "info"), "/actuator", "never", "never");
    }

    private static MonitoringProperties defaultMonitoring() {
        return new MonitoringProperties(
                true,
                true,
                Duration.ofSeconds(10),
                false,
                0.1,
                "https://zipkin.example.com/api/v2/spans");
    }

    private static LoggingProperties defaultLoggingProductionSafe() {
        return new LoggingProperties("WARN", "WARN", "WARN", "WARN", "WARN", "WARN", "WARN");
    }
}
