package br.com.drinkwater.config.runtime;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

final class RuntimeActuatorConfigurationTest {

    private RuntimeActuatorConfiguration config;

    @BeforeEach
    void setUp() {
        config = new RuntimeActuatorConfiguration();
    }

    @Test
    void getHealthShowDetails_shouldReturnDefaultValue() {
        assertThat(config.getHealthShowDetails()).isEqualTo("when-authorized");
    }

    @Test
    void setHealthShowDetails_shouldUpdateValue() {
        config.setHealthShowDetails("never");
        assertThat(config.getHealthShowDetails()).isEqualTo("never");
    }

    @Test
    void getHealthShowComponents_shouldReturnDefaultValue() {
        assertThat(config.getHealthShowComponents()).isTrue();
    }

    @Test
    void setHealthShowComponents_shouldUpdateValue() {
        config.setHealthShowComponents(false);
        assertThat(config.getHealthShowComponents()).isFalse();
    }

    @Test
    void isProductionSafe_whenNever_shouldReturnTrue() {
        config.setHealthShowDetails("never");
        assertThat(config.isProductionSafe()).isTrue();
    }

    @Test
    void isProductionSafe_whenAlways_shouldReturnFalse() {
        config.setHealthShowDetails("always");
        assertThat(config.isProductionSafe()).isFalse();
    }

    @Test
    void isProductionSafe_whenWhenAuthorized_shouldReturnFalse() {
        config.setHealthShowDetails("when-authorized");
        assertThat(config.isProductionSafe()).isFalse();
    }
}
