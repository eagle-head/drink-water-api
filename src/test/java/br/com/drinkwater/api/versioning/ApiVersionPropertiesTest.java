package br.com.drinkwater.api.versioning;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

final class ApiVersionPropertiesTest {

    private ApiVersionProperties properties;

    @BeforeEach
    void setUp() {
        properties =
                new ApiVersionProperties(
                        "v2",
                        List.of(
                                new ApiVersionProperties.VersionConfig(
                                        "v1", true, "2026-12-31", "v2"),
                                new ApiVersionProperties.VersionConfig("v2", false, "", "")));
    }

    @Test
    void givenDeprecatedVersion_whenIsVersionDeprecated_thenReturnTrue() {
        assertThat(properties.isVersionDeprecated("v1")).isTrue();
    }

    @Test
    void givenActiveVersion_whenIsVersionDeprecated_thenReturnFalse() {
        assertThat(properties.isVersionDeprecated("v2")).isFalse();
    }

    @Test
    void givenUnknownVersion_whenIsVersionDeprecated_thenReturnFalse() {
        assertThat(properties.isVersionDeprecated("v99")).isFalse();
    }

    @Test
    void givenDeprecatedVersion_whenGetSunsetDate_thenReturnDate() {
        assertThat(properties.getSunsetDate("v1")).isEqualTo("2026-12-31");
    }

    @Test
    void givenActiveVersion_whenGetSunsetDate_thenReturnEmpty() {
        assertThat(properties.getSunsetDate("v2")).isEmpty();
    }

    @Test
    void givenUnknownVersion_whenGetSunsetDate_thenReturnEmpty() {
        assertThat(properties.getSunsetDate("v99")).isEmpty();
    }

    @Test
    void givenDeprecatedVersion_whenGetSuccessorVersion_thenReturnSuccessor() {
        assertThat(properties.getSuccessorVersion("v1")).isEqualTo("v2");
    }

    @Test
    void givenActiveVersion_whenGetSuccessorVersion_thenReturnEmpty() {
        assertThat(properties.getSuccessorVersion("v2")).isEmpty();
    }

    @Test
    void givenUnknownVersion_whenGetSuccessorVersion_thenReturnEmpty() {
        assertThat(properties.getSuccessorVersion("v99")).isEmpty();
    }

    @Test
    void givenNullVersionsInCompactConstructor_whenInstantiated_thenVersionsRemainNull() {
        var props = new ApiVersionProperties("v1", null);
        assertThat(props.versions()).isNull();
    }
}
