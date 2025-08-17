package br.com.drinkwater.config.runtime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Runtime-configurable logging properties that can be updated without application restart.
 * Changes take effect immediately when the /actuator/refresh endpoint is called.
 */
@Component
@RefreshScope
@ConfigurationProperties(prefix = "runtime.logging.level")
@Validated
public class RuntimeLoggingConfiguration {

    @NotBlank(message = "Runtime logging root level is required")
    @Pattern(
            regexp = "^(TRACE|DEBUG|INFO|WARN|ERROR|FATAL|OFF)$",
            message = "Runtime logging root level must be one of: TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF"
    )
    private String root = "INFO";

    @NotBlank(message = "Runtime logging app level is required")
    @Pattern(
            regexp = "^(TRACE|DEBUG|INFO|WARN|ERROR|FATAL|OFF)$",
            message = "Runtime logging app level must be one of: TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF"
    )
    private String app = "INFO";

    @NotBlank(message = "Runtime logging security level is required")
    @Pattern(
            regexp = "^(TRACE|DEBUG|INFO|WARN|ERROR|FATAL|OFF)$",
            message = "Runtime logging security level must be one of: TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF"
    )
    private String security = "WARN";

    @NotBlank(message = "Runtime logging oauth2 level is required")
    @Pattern(
            regexp = "^(TRACE|DEBUG|INFO|WARN|ERROR|FATAL|OFF)$",
            message = "Runtime logging oauth2 level must be one of: TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF"
    )
    private String oauth2 = "WARN";

    @NotBlank(message = "Runtime logging sql level is required")
    @Pattern(
            regexp = "^(TRACE|DEBUG|INFO|WARN|ERROR|FATAL|OFF)$",
            message = "Runtime logging sql level must be one of: TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF"
    )
    private String sql = "WARN";

    @NotBlank(message = "Runtime logging sql params level is required")
    @Pattern(
            regexp = "^(TRACE|DEBUG|INFO|WARN|ERROR|FATAL|OFF)$",
            message = "Runtime logging sql params level must be one of: TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF"
    )
    private String sqlParams = "WARN";

    @NotBlank(message = "Runtime logging hibernate level is required")
    @Pattern(
            regexp = "^(TRACE|DEBUG|INFO|WARN|ERROR|FATAL|OFF)$",
            message = "Runtime logging hibernate level must be one of: TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF"
    )
    private String hibernate = "WARN";

    @NotBlank(message = "Runtime logging springframework level is required")
    @Pattern(
            regexp = "^(TRACE|DEBUG|INFO|WARN|ERROR|FATAL|OFF)$",
            message = "Runtime logging springframework level must be one of: TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF"
    )
    private String springframework = "WARN";

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getSecurity() {
        return security;
    }

    public void setSecurity(String security) {
        this.security = security;
    }

    public String getOauth2() {
        return oauth2;
    }

    public void setOauth2(String oauth2) {
        this.oauth2 = oauth2;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getSqlParams() {
        return sqlParams;
    }

    public void setSqlParams(String sqlParams) {
        this.sqlParams = sqlParams;
    }

    public String getHibernate() {
        return hibernate;
    }

    public void setHibernate(String hibernate) {
        this.hibernate = hibernate;
    }

    public String getSpringframework() {
        return springframework;
    }

    public void setSpringframework(String springframework) {
        this.springframework = springframework;
    }

    /**
     * Checks if logging is configured appropriately for production.
     *
     * @return true if log levels are WARN or higher for production
     */
    public boolean isProductionSafe() {
        return isLevelSafeForProduction(root) &&
                isLevelSafeForProduction(security) &&
                isLevelSafeForProduction(oauth2) &&
                isLevelSafeForProduction(sql);
    }

    /**
     * Checks if logging is configured for development (verbose logging).
     *
     * @return true if debug logging is enabled
     */
    public boolean isDevelopmentMode() {
        return "DEBUG".equals(root) || "TRACE".equals(root) ||
                "DEBUG".equals(sql) || "TRACE".equals(sql);
    }

    private boolean isLevelSafeForProduction(String level) {
        return "WARN".equals(level) || "ERROR".equals(level) ||
                "FATAL".equals(level) || "OFF".equals(level);
    }
}