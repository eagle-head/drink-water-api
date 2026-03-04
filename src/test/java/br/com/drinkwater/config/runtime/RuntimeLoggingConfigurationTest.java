package br.com.drinkwater.config.runtime;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link RuntimeLoggingConfiguration}. Tests methods not covered by
 * RuntimeConfigurationTest: isProductionSafe with WARN/ERROR, isDevelopmentMode with TRACE, and
 * getters/setters for sqlParams and springframework.
 */
final class RuntimeLoggingConfigurationTest {

    private RuntimeLoggingConfiguration config;

    @BeforeEach
    void setUp() {
        config = new RuntimeLoggingConfiguration();
        config.setRoot("INFO");
        config.setApp("INFO");
        config.setSecurity("WARN");
        config.setOauth2("WARN");
        config.setSql("WARN");
        config.setSqlParams("WARN");
        config.setSpringframework("WARN");
    }

    @Test
    void isProductionSafe_whenAllLevelsWarnOrError_shouldReturnTrue() {
        // Given
        config.setRoot("WARN");
        config.setSecurity("WARN");
        config.setOauth2("WARN");
        config.setSql("WARN");

        // When
        boolean result = config.isProductionSafe();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void isProductionSafe_whenRootIsInfo_shouldReturnFalse() {
        // Given
        config.setRoot("INFO");
        config.setSecurity("WARN");
        config.setOauth2("WARN");
        config.setSql("WARN");

        // When
        boolean result = config.isProductionSafe();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void isDevelopmentMode_whenRootIsTrace_shouldReturnTrue() {
        // Given
        config.setRoot("TRACE");
        config.setSql("WARN");

        // When
        boolean result = config.isDevelopmentMode();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void isDevelopmentMode_whenSqlIsTrace_shouldReturnTrue() {
        // Given
        config.setRoot("INFO");
        config.setSql("TRACE");

        // When
        boolean result = config.isDevelopmentMode();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void getSecurity_shouldReturnValue() {
        assertThat(config.getSecurity()).isEqualTo("WARN");
    }

    @Test
    void getOauth2_shouldReturnValue() {
        assertThat(config.getOauth2()).isEqualTo("WARN");
    }

    @Test
    void getSql_shouldReturnValue() {
        assertThat(config.getSql()).isEqualTo("WARN");
    }

    @Test
    void getSqlParams_whenSet_shouldReturnValue() {
        // Given
        config.setSqlParams("DEBUG");

        // When
        String result = config.getSqlParams();

        // Then
        assertThat(result).isEqualTo("DEBUG");
    }

    @Test
    void setSqlParams_shouldUpdateValue() {
        // When
        config.setSqlParams("TRACE");

        // Then
        assertThat(config.getSqlParams()).isEqualTo("TRACE");
    }

    @Test
    void getSpringframework_whenSet_shouldReturnValue() {
        // Given
        config.setSpringframework("ERROR");

        // When
        String result = config.getSpringframework();

        // Then
        assertThat(result).isEqualTo("ERROR");
    }

    @Test
    void setSpringframework_shouldUpdateValue() {
        // When
        config.setSpringframework("DEBUG");

        // Then
        assertThat(config.getSpringframework()).isEqualTo("DEBUG");
    }
}
