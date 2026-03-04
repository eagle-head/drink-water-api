package br.com.drinkwater.config.runtime;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing runtime configuration changes. Provides endpoints to update
 * configuration without application restart.
 *
 * <p>Security: All endpoints require appropriate authentication and authorization.
 */
@RestController
@RequestMapping("/management/runtime-config")
@Validated
public class RuntimeConfigurationController {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(RuntimeConfigurationController.class);

    private final RuntimeConfigurationService configurationService;
    private final RuntimeConfigurationValidator configurationValidator;

    public RuntimeConfigurationController(
            RuntimeConfigurationService configurationService,
            RuntimeConfigurationValidator configurationValidator) {
        this.configurationService = configurationService;
        this.configurationValidator = configurationValidator;
    }

    /**
     * Updates a specific logging level at runtime.
     *
     * @param request the logging level update request
     * @return ResponseEntity indicating success or failure
     */
    @PostMapping("/logging/level")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('SCOPE_drinkwater:v1:admin:config:manage')")
    public ResponseEntity<Map<String, String>> updateLogLevel(
            @Valid @RequestBody LogLevelUpdateRequest request) {
        LOGGER.info(
                "Received request to update log level for '{}' to '{}'",
                request.loggerName(),
                request.level());

        configurationService.updateLogLevel(request.loggerName(), request.level());

        return ResponseEntity.ok(
                Map.of(
                        "status", "success",
                        "message",
                                "Updated log level for '%s' to '%s'"
                                        .formatted(request.loggerName(), request.level()),
                        "logger", request.loggerName(),
                        "level", request.level()));
    }

    /**
     * Triggers a full refresh of all runtime configurations.
     *
     * @return ResponseEntity indicating the refresh was triggered
     */
    @PostMapping("/refresh")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('SCOPE_drinkwater:v1:admin:config:manage')")
    public ResponseEntity<Map<String, String>> refreshConfiguration() {
        LOGGER.info("Received request to refresh runtime configuration");

        configurationService.refreshConfiguration();

        return ResponseEntity.ok(
                Map.of(
                        "status", "success",
                        "message", "Runtime configuration refresh triggered"));
    }

    /**
     * Gets the current runtime configuration summary.
     *
     * @return current runtime configuration values
     */
    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('SCOPE_drinkwater:v1:admin:config:read')")
    public ResponseEntity<Map<String, Object>> getConfigurationSummary() {
        LOGGER.debug("Received request for runtime configuration summary");

        Map<String, Object> summary = configurationService.getConfigurationSummary();
        return ResponseEntity.ok(summary);
    }

    /**
     * Gets the current logging configuration.
     *
     * @return current logging levels
     */
    @GetMapping("/logging")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('SCOPE_drinkwater:v1:admin:config:read')")
    public ResponseEntity<Map<String, Object>> getLoggingConfiguration() {
        LOGGER.debug("Received request for logging configuration");

        Map<String, Object> summary = configurationService.getConfigurationSummary();
        @SuppressWarnings("unchecked")
        Map<String, Object> loggingConfig = (Map<String, Object>) summary.get("logging");

        return ResponseEntity.ok(Map.of("status", "success", "logging", loggingConfig));
    }

    /**
     * Validates the current runtime configuration.
     *
     * @return ResponseEntity with validation results
     */
    @PostMapping("/validate")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('SCOPE_drinkwater:v1:admin:config:manage')")
    public ResponseEntity<Map<String, Object>> validateConfiguration() {
        LOGGER.info("Received request to validate runtime configuration");

        RuntimeConfigurationValidator.ValidationResult result =
                configurationValidator.validateManually();

        if (result.valid()) {
            return ResponseEntity.ok(
                    Map.of(
                            "status",
                            "success",
                            "message",
                            result.message(),
                            "valid",
                            true,
                            "errors",
                            result.errors()));
        }

        return ResponseEntity.badRequest()
                .body(
                        Map.of(
                                "status",
                                "error",
                                "message",
                                result.message(),
                                "valid",
                                false,
                                "errors",
                                result.errors()));
    }

    /** Request object for updating log levels. */
    public record LogLevelUpdateRequest(
            @NotBlank(message = "Logger name is required") String loggerName,
            @NotBlank(message = "Log level is required")
                    @Pattern(
                            regexp = "^(TRACE|DEBUG|INFO|WARN|ERROR|FATAL|OFF)$",
                            message =
                                    "Log level must be one of: TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF")
                    String level) {}
}
