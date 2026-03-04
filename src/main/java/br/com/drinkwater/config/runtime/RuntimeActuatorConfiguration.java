package br.com.drinkwater.config.runtime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Runtime-configurable actuator properties that can be updated without application restart.
 *
 * <p><strong>Important:</strong> These properties are <em>informational only</em>. While values are
 * refreshed in this bean when {@code /actuator/refresh} is called, the actual Spring Boot Actuator
 * health endpoint configuration (e.g. {@code show-details}, {@code show-components}) is not
 * dynamically re-applied. A full application restart is required for actuator behavior changes to
 * take effect. This bean is useful for querying the <em>desired</em> runtime configuration and for
 * validation purposes.
 */
@Component
@RefreshScope
@ConfigurationProperties(prefix = "runtime.actuator")
@Validated
public class RuntimeActuatorConfiguration {

    @NotBlank(message = "Runtime actuator health show details is required")
    @Pattern(
            regexp = "^(never|when-authorized|always)$",
            message =
                    "Runtime actuator health show details must be one of: never, when-authorized, always")
    private String healthShowDetails = "when-authorized";

    private Boolean healthShowComponents = true;

    // Getters and setters
    public String getHealthShowDetails() {
        return healthShowDetails;
    }

    public void setHealthShowDetails(String healthShowDetails) {
        this.healthShowDetails = healthShowDetails;
    }

    public Boolean getHealthShowComponents() {
        return healthShowComponents;
    }

    public void setHealthShowComponents(Boolean healthShowComponents) {
        this.healthShowComponents = healthShowComponents;
    }

    /**
     * Checks if actuator is configured safely for production.
     *
     * @return true if actuator settings are production-safe
     */
    public boolean isProductionSafe() {
        return "never".equals(healthShowDetails);
    }
}
