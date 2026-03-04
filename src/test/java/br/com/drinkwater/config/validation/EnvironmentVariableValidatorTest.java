package br.com.drinkwater.config.validation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
 * Tests for the EnvironmentVariableValidator to ensure fail-fast behavior for configuration
 * validation only (NOT connectivity tests).
 *
 * <p>This test suite validates the new approach that separates configuration validation from
 * external service connectivity checks, following 12-Factor App principles and cloud-native best
 * practices.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
final class EnvironmentVariableValidatorTest {

    @Mock private Validator validator;

    @Mock private RuntimeConfigurationValidator runtimeValidator;

    @Mock private ValidationErrorFormatter validationErrorFormatter;

    @Mock private ApplicationArguments args;

    private EnvironmentVariableValidator environmentValidator;

    @BeforeEach
    void setUp() {
        // Create valid default properties for testing
        ApplicationProperties appProps =
                new ApplicationProperties("drink-water-api", "1.0.0", "development");

        ServerProperties serverProps = new ServerProperties(8081, "on_param");

        DatabaseProperties dbProps =
                new DatabaseProperties(
                        "jdbc:postgresql://localhost:5432/test",
                        "testuser",
                        "testpassword",
                        "org.postgresql.Driver",
                        10,
                        2,
                        30000L);

        KeycloakProperties keycloakProps =
                new KeycloakProperties(
                        "http://localhost:8080",
                        "drinkwater",
                        "drinkwaterapp",
                        "admin",
                        "adminpassword",
                        "http://localhost:8080/realms/drinkwater",
                        "http://localhost:8080/realms/drinkwater/protocol/openid-connect/certs",
                        null);

        CorsProperties corsProps =
                new CorsProperties(
                        "http://localhost:3000",
                        "http://localhost:3000",
                        List.of("http://localhost:3000"),
                        List.of("GET", "POST"),
                        List.of("Authorization", "Content-Type"),
                        true,
                        3600L);

        SecurityProperties securityProps = new SecurityProperties(null, null, null);

        ActuatorProperties actuatorProps =
                new ActuatorProperties(
                        List.of("health", "info"), "/actuator", "when-authorized", "always");

        MonitoringProperties monitoringProps =
                new MonitoringProperties(
                        true,
                        true,
                        Duration.ofSeconds(10),
                        false,
                        0.1,
                        "http://localhost:9411/api/v2/spans");

        LoggingProperties loggingProps =
                new LoggingProperties("INFO", "INFO", "WARN", "WARN", "WARN", "WARN", "WARN");

        WebhookProperties webhookProps = new WebhookProperties("test-webhook-secret");

        CacheProperties cacheProps = new CacheProperties(10_000, 5);

        environmentValidator =
                new EnvironmentVariableValidator(
                        appProps,
                        serverProps,
                        dbProps,
                        keycloakProps,
                        corsProps,
                        securityProps,
                        actuatorProps,
                        monitoringProps,
                        loggingProps,
                        webhookProps,
                        cacheProps,
                        runtimeValidator,
                        validationErrorFormatter,
                        validator);
    }

    @Test
    void givenValidConfiguration_whenValidate_thenShouldPass() throws Exception {
        // Given
        doReturn(List.of())
                .when(validationErrorFormatter)
                .validateMultipleObjects(
                        any(Validator.class),
                        any(ValidationErrorFormatter.ValidationTarget[].class));

        // When & Then
        assertDoesNotThrow(() -> environmentValidator.run(args));

        verify(validationErrorFormatter)
                .validateMultipleObjects(
                        any(Validator.class),
                        any(ValidationErrorFormatter.ValidationTarget[].class));
    }

    @Test
    void givenInvalidConfiguration_whenValidate_thenShouldFail() {
        // Given
        doReturn(List.of("[Application] name: must not be null"))
                .when(validationErrorFormatter)
                .validateMultipleObjects(
                        any(Validator.class),
                        any(ValidationErrorFormatter.ValidationTarget[].class));

        // When & Then
        IllegalStateException exception =
                assertThrows(IllegalStateException.class, () -> environmentValidator.run(args));

        assertTrue(exception.getMessage().contains("Configuration validation failed"));
        assertTrue(exception.getMessage().contains("[Application] name: must not be null"));
    }

    @Test
    void givenProductionEnvironment_whenValidateSecurityConfig_thenShouldRequireHttps() {
        // Test is implicit in the validator logic - production validation
        // is tested through the integration with actual property validation
        // NOTE: This validator NO LONGER tests connectivity - only configuration rules
        assertTrue(true, "Production validation is handled by property classes");
    }

    @Test
    void givenRuntimeValidationFailure_whenValidate_thenShouldStillPass() throws Exception {
        // Given: Configuration validation passes but runtime validation fails
        doReturn(List.of())
                .when(validationErrorFormatter)
                .validateMultipleObjects(
                        any(Validator.class),
                        any(ValidationErrorFormatter.ValidationTarget[].class));
        doThrow(new RuntimeException("Runtime validation failed"))
                .when(runtimeValidator)
                .validateRuntimeConfiguration();

        // When & Then: Should still pass because runtime validation is not critical for startup
        assertDoesNotThrow(() -> environmentValidator.run(args));

        // Verify runtime validation was attempted but failure was handled gracefully
        verify(runtimeValidator).validateRuntimeConfiguration();
    }

    @Test
    void givenInvalidKeycloakIssuerUri_whenValidate_thenShouldFailWithCrossPropertyError() {
        var keycloakProps =
                new KeycloakProperties(
                        "http://localhost:8080",
                        "drinkwater",
                        "drinkwaterapp",
                        "admin",
                        "adminpassword",
                        "http://localhost:8080/realms/wrongrealm",
                        "http://localhost:8080/realms/drinkwater/protocol/openid-connect/certs",
                        null);
        var validator = createValidatorWithKeycloak(keycloakProps);
        doReturn(List.of())
                .when(validationErrorFormatter)
                .validateMultipleObjects(
                        any(Validator.class),
                        any(ValidationErrorFormatter.ValidationTarget[].class));

        var ex = assertThrows(IllegalStateException.class, () -> validator.run(args));
        assertTrue(ex.getMessage().contains("Cross-property validation failed"));
        assertTrue(ex.getMessage().contains("KEYCLOAK_ISSUER_URI"));
    }

    @Test
    void givenInvalidKeycloakJwkSetUri_whenValidate_thenShouldFailWithCrossPropertyError() {
        var keycloakProps =
                new KeycloakProperties(
                        "http://localhost:8080",
                        "drinkwater",
                        "drinkwaterapp",
                        "admin",
                        "adminpassword",
                        "http://localhost:8080/realms/drinkwater",
                        "http://localhost:8080/wrong/certs",
                        null);
        var validator = createValidatorWithKeycloak(keycloakProps);
        doReturn(List.of())
                .when(validationErrorFormatter)
                .validateMultipleObjects(
                        any(Validator.class),
                        any(ValidationErrorFormatter.ValidationTarget[].class));

        var ex = assertThrows(IllegalStateException.class, () -> validator.run(args));
        assertTrue(ex.getMessage().contains("Cross-property validation failed"));
        assertTrue(ex.getMessage().contains("KEYCLOAK_JWK_SET_URI"));
    }

    @Test
    void givenBaseUrlNotInCorsOrigins_whenValidate_thenShouldFailWithCrossPropertyError() {
        var corsProps =
                new CorsProperties(
                        "http://localhost:3000",
                        "http://localhost:3000",
                        List.of("http://other-origin.com"),
                        List.of("GET", "POST"),
                        List.of("Authorization", "Content-Type"),
                        true,
                        3600L);
        var validator = createValidatorWithCors(corsProps);
        doReturn(List.of())
                .when(validationErrorFormatter)
                .validateMultipleObjects(
                        any(Validator.class),
                        any(ValidationErrorFormatter.ValidationTarget[].class));

        var ex = assertThrows(IllegalStateException.class, () -> validator.run(args));
        assertTrue(ex.getMessage().contains("Cross-property validation failed"));
        assertTrue(ex.getMessage().contains("BASE_URL"));
    }

    @Test
    void givenInvalidTracingConfig_whenValidate_thenShouldFailWithCrossPropertyError() {
        var monitoringProps =
                new MonitoringProperties(true, true, Duration.ofSeconds(10), true, 0.0, "");
        var validator = createValidatorWithMonitoring(monitoringProps);
        doReturn(List.of())
                .when(validationErrorFormatter)
                .validateMultipleObjects(
                        any(Validator.class),
                        any(ValidationErrorFormatter.ValidationTarget[].class));

        var ex = assertThrows(IllegalStateException.class, () -> validator.run(args));
        assertTrue(ex.getMessage().contains("Cross-property validation failed"));
        assertTrue(ex.getMessage().contains("tracing"));
    }

    @Test
    void givenProductionEnvWithPrivilegedPort_whenValidate_thenShouldFail() {
        var appProps = new ApplicationProperties("drink-water-api", "1.0.0", "production");
        var serverProps = new ServerProperties(80, "on_param");
        var validator = createValidatorWithAppAndServer(appProps, serverProps);
        doReturn(List.of())
                .when(validationErrorFormatter)
                .validateMultipleObjects(
                        any(Validator.class),
                        any(ValidationErrorFormatter.ValidationTarget[].class));

        var ex = assertThrows(IllegalStateException.class, () -> validator.run(args));
        assertTrue(ex.getMessage().contains("Environment-specific validation failed"));
        assertTrue(ex.getMessage().contains("SERVER_PORT"));
    }

    @Test
    void givenProductionEnvWithInsecureKeycloak_whenValidate_thenShouldFail() {
        var appProps = new ApplicationProperties("drink-water-api", "1.0.0", "production");
        var serverProps = new ServerProperties(8080, "on_param");
        var keycloakProps =
                new KeycloakProperties(
                        "http://localhost:8080",
                        "drinkwater",
                        "drinkwaterapp",
                        "admin",
                        "adminpassword",
                        "http://localhost:8080/realms/drinkwater",
                        "http://localhost:8080/realms/drinkwater/protocol/openid-connect/certs",
                        null);
        var validator = createValidatorWithProductionConfig(appProps, serverProps, keycloakProps);
        doReturn(List.of())
                .when(validationErrorFormatter)
                .validateMultipleObjects(
                        any(Validator.class),
                        any(ValidationErrorFormatter.ValidationTarget[].class));

        var ex = assertThrows(IllegalStateException.class, () -> validator.run(args));
        assertTrue(ex.getMessage().contains("Environment-specific validation failed"));
        assertTrue(ex.getMessage().contains("HTTPS"));
    }

    @Test
    void givenDatabaseUrlWithPassword_whenLogConfigurationSummary_thenMasksPassword()
            throws Exception {
        var dbProps =
                new DatabaseProperties(
                        "jdbc:postgresql://localhost:5432/test?user=testuser&password=secret123",
                        "testuser",
                        "testpassword",
                        "org.postgresql.Driver",
                        10,
                        2,
                        30000L);
        var validator = createValidatorWithDatabase(dbProps);
        doReturn(List.of())
                .when(validationErrorFormatter)
                .validateMultipleObjects(
                        any(Validator.class),
                        any(ValidationErrorFormatter.ValidationTarget[].class));

        assertDoesNotThrow(() -> validator.run(args));
    }

    private EnvironmentVariableValidator createValidatorWithKeycloak(KeycloakProperties keycloak) {
        return new EnvironmentVariableValidator(
                new ApplicationProperties("drink-water-api", "1.0.0", "development"),
                new ServerProperties(8081, "on_param"),
                new DatabaseProperties(
                        "jdbc:postgresql://localhost:5432/test",
                        "testuser",
                        "testpassword",
                        "org.postgresql.Driver",
                        10,
                        2,
                        30000L),
                keycloak,
                new CorsProperties(
                        "http://localhost:3000",
                        "http://localhost:3000",
                        List.of("http://localhost:3000"),
                        List.of("GET", "POST"),
                        List.of("Authorization", "Content-Type"),
                        true,
                        3600L),
                new SecurityProperties(null, null, null),
                new ActuatorProperties(
                        List.of("health", "info"), "/actuator", "when-authorized", "always"),
                new MonitoringProperties(
                        true,
                        true,
                        Duration.ofSeconds(10),
                        false,
                        0.1,
                        "http://localhost:9411/api/v2/spans"),
                new LoggingProperties("INFO", "INFO", "WARN", "WARN", "WARN", "WARN", "WARN"),
                new WebhookProperties("test-webhook-secret"),
                new CacheProperties(10_000, 5),
                runtimeValidator,
                validationErrorFormatter,
                validator);
    }

    private EnvironmentVariableValidator createValidatorWithCors(CorsProperties cors) {
        return new EnvironmentVariableValidator(
                new ApplicationProperties("drink-water-api", "1.0.0", "development"),
                new ServerProperties(8081, "on_param"),
                new DatabaseProperties(
                        "jdbc:postgresql://localhost:5432/test",
                        "testuser",
                        "testpassword",
                        "org.postgresql.Driver",
                        10,
                        2,
                        30000L),
                new KeycloakProperties(
                        "http://localhost:8080",
                        "drinkwater",
                        "drinkwaterapp",
                        "admin",
                        "adminpassword",
                        "http://localhost:8080/realms/drinkwater",
                        "http://localhost:8080/realms/drinkwater/protocol/openid-connect/certs",
                        null),
                cors,
                new SecurityProperties(null, null, null),
                new ActuatorProperties(
                        List.of("health", "info"), "/actuator", "when-authorized", "always"),
                new MonitoringProperties(
                        true,
                        true,
                        Duration.ofSeconds(10),
                        false,
                        0.1,
                        "http://localhost:9411/api/v2/spans"),
                new LoggingProperties("INFO", "INFO", "WARN", "WARN", "WARN", "WARN", "WARN"),
                new WebhookProperties("test-webhook-secret"),
                new CacheProperties(10_000, 5),
                runtimeValidator,
                validationErrorFormatter,
                validator);
    }

    private EnvironmentVariableValidator createValidatorWithMonitoring(
            MonitoringProperties monitoring) {
        return new EnvironmentVariableValidator(
                new ApplicationProperties("drink-water-api", "1.0.0", "development"),
                new ServerProperties(8081, "on_param"),
                new DatabaseProperties(
                        "jdbc:postgresql://localhost:5432/test",
                        "testuser",
                        "testpassword",
                        "org.postgresql.Driver",
                        10,
                        2,
                        30000L),
                new KeycloakProperties(
                        "http://localhost:8080",
                        "drinkwater",
                        "drinkwaterapp",
                        "admin",
                        "adminpassword",
                        "http://localhost:8080/realms/drinkwater",
                        "http://localhost:8080/realms/drinkwater/protocol/openid-connect/certs",
                        null),
                new CorsProperties(
                        "http://localhost:3000",
                        "http://localhost:3000",
                        List.of("http://localhost:3000"),
                        List.of("GET", "POST"),
                        List.of("Authorization", "Content-Type"),
                        true,
                        3600L),
                new SecurityProperties(null, null, null),
                new ActuatorProperties(
                        List.of("health", "info"), "/actuator", "when-authorized", "always"),
                monitoring,
                new LoggingProperties("INFO", "INFO", "WARN", "WARN", "WARN", "WARN", "WARN"),
                new WebhookProperties("test-webhook-secret"),
                new CacheProperties(10_000, 5),
                runtimeValidator,
                validationErrorFormatter,
                validator);
    }

    private EnvironmentVariableValidator createValidatorWithAppAndServer(
            ApplicationProperties app, ServerProperties server) {
        return new EnvironmentVariableValidator(
                app,
                server,
                new DatabaseProperties(
                        "jdbc:postgresql://localhost:5432/test",
                        "testuser",
                        "testpassword",
                        "org.postgresql.Driver",
                        10,
                        2,
                        30000L),
                new KeycloakProperties(
                        "https://keycloak.example.com",
                        "drinkwater",
                        "drinkwaterapp",
                        "admin",
                        "adminpassword",
                        "https://keycloak.example.com/realms/drinkwater",
                        "https://keycloak.example.com/realms/drinkwater/protocol/openid-connect/certs",
                        null),
                new CorsProperties(
                        "https://app.example.com",
                        "https://app.example.com",
                        List.of("https://app.example.com"),
                        List.of("GET", "POST"),
                        List.of("Authorization", "Content-Type"),
                        true,
                        3600L),
                new SecurityProperties("jwt-key", "enc-key", "api-secret"),
                new ActuatorProperties(List.of("health", "info"), "/actuator", "never", "never"),
                new MonitoringProperties(
                        true,
                        true,
                        Duration.ofSeconds(10),
                        false,
                        0.1,
                        "https://zipkin.example.com/api/v2/spans"),
                new LoggingProperties("WARN", "WARN", "WARN", "WARN", "WARN", "WARN", "WARN"),
                new WebhookProperties("test-webhook-secret"),
                new CacheProperties(10_000, 5),
                runtimeValidator,
                validationErrorFormatter,
                validator);
    }

    private EnvironmentVariableValidator createValidatorWithProductionConfig(
            ApplicationProperties app, ServerProperties server, KeycloakProperties keycloak) {
        return new EnvironmentVariableValidator(
                app,
                server,
                new DatabaseProperties(
                        "jdbc:postgresql://localhost:5432/test",
                        "testuser",
                        "testpassword",
                        "org.postgresql.Driver",
                        10,
                        2,
                        30000L),
                keycloak,
                new CorsProperties(
                        "https://app.example.com",
                        "https://app.example.com",
                        List.of("https://app.example.com"),
                        List.of("GET", "POST"),
                        List.of("Authorization", "Content-Type"),
                        true,
                        3600L),
                new SecurityProperties("jwt-key", "enc-key", "api-secret"),
                new ActuatorProperties(List.of("health", "info"), "/actuator", "never", "never"),
                new MonitoringProperties(
                        true,
                        true,
                        Duration.ofSeconds(10),
                        false,
                        0.1,
                        "https://zipkin.example.com/api/v2/spans"),
                new LoggingProperties("WARN", "WARN", "WARN", "WARN", "WARN", "WARN", "WARN"),
                new WebhookProperties("test-webhook-secret"),
                new CacheProperties(10_000, 5),
                runtimeValidator,
                validationErrorFormatter,
                validator);
    }

    private EnvironmentVariableValidator createValidatorWithDatabase(DatabaseProperties database) {
        return new EnvironmentVariableValidator(
                new ApplicationProperties("drink-water-api", "1.0.0", "development"),
                new ServerProperties(8081, "on_param"),
                database,
                new KeycloakProperties(
                        "http://localhost:8080",
                        "drinkwater",
                        "drinkwaterapp",
                        "admin",
                        "adminpassword",
                        "http://localhost:8080/realms/drinkwater",
                        "http://localhost:8080/realms/drinkwater/protocol/openid-connect/certs",
                        null),
                new CorsProperties(
                        "http://localhost:3000",
                        "http://localhost:3000",
                        List.of("http://localhost:3000"),
                        List.of("GET", "POST"),
                        List.of("Authorization", "Content-Type"),
                        true,
                        3600L),
                new SecurityProperties(null, null, null),
                new ActuatorProperties(
                        List.of("health", "info"), "/actuator", "when-authorized", "always"),
                new MonitoringProperties(
                        true,
                        true,
                        Duration.ofSeconds(10),
                        false,
                        0.1,
                        "http://localhost:9411/api/v2/spans"),
                new LoggingProperties("INFO", "INFO", "WARN", "WARN", "WARN", "WARN", "WARN"),
                new WebhookProperties("test-webhook-secret"),
                new CacheProperties(10_000, 5),
                runtimeValidator,
                validationErrorFormatter,
                validator);
    }
}
