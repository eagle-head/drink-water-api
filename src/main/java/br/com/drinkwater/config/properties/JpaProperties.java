package br.com.drinkwater.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Immutable configuration properties for JPA/Hibernate settings.
 * All properties are loaded at bootstrap time and cannot be modified at runtime.
 * Maps to spring.jpa.* configuration properties.
 */
@ConfigurationProperties(prefix = "spring.jpa")
@Validated
public record JpaProperties(

        @NotNull(message = "JPA_OPEN_IN_VIEW environment variable is required")
        Boolean openInView,

        @NotNull(message = "JPA_DEFER_DATASOURCE_INITIALIZATION environment variable is required")
        Boolean deferDatasourceInitialization,

        @NotBlank(message = "DATABASE_PLATFORM environment variable is required")
        @Pattern(
                regexp = "^org\\.hibernate\\.dialect\\.PostgreSQLDialect$",
                message = "DATABASE_PLATFORM must be 'org.hibernate.dialect.PostgreSQLDialect'"
        )
        String databasePlatform,

        /**
         * Hibernate DDL auto configuration.
         * Maps to spring.jpa.hibernate.ddl-auto property.
         */
        HibernateProperties hibernate
) {

    /**
     * Constructor with strict validation - no defaults.
     */
    public JpaProperties {
        // All properties are required - no defaults allowed
    }

    /**
     * Checks if the DDL mode is safe for production.
     *
     * @return true if DDL mode is 'validate' or 'none'
     */
    public boolean isProductionSafe() {
        return hibernate != null && hibernate.isProductionSafe();
    }

    /**
     * Nested properties for Hibernate configuration.
     */
    @Validated
    public record HibernateProperties(
            @NotBlank(message = "JPA_HIBERNATE_DDL_AUTO environment variable is required")
            @Pattern(
                    regexp = "^(create|create-drop|update|validate|none)$",
                    message = "JPA_HIBERNATE_DDL_AUTO must be one of: create, create-drop, update, validate, none"
            )
            String ddlAuto
    ) {
        /**
         * Checks if the DDL mode is safe for production.
         *
         * @return true if DDL mode is 'validate' or 'none'
         */
        public boolean isProductionSafe() {
            return "validate".equals(ddlAuto) || "none".equals(ddlAuto);
        }
    }
}