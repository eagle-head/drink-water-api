package br.com.drinkwater.exception;

import static org.assertj.core.api.Assertions.assertThat;

import io.swagger.v3.oas.annotations.media.Schema;
import java.net.URI;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

final class ScopeProblemDetailSchemaTest {

    @Test
    void recordShouldHaveSchemaAnnotation() {
        var schema = ScopeProblemDetailSchema.class.getAnnotation(Schema.class);
        assertThat(schema).isNotNull();
        assertThat(schema.description())
                .isEqualTo("RFC 7807 Problem Detail with required OAuth scope");
    }

    @Test
    void allFieldsShouldHaveSchemaAnnotation() {
        var fields =
                Arrays.stream(ScopeProblemDetailSchema.class.getDeclaredFields())
                        .filter(f -> !f.isSynthetic())
                        .toList();
        assertThat(fields).hasSize(6);
        assertThat(fields.stream().allMatch(f -> f.isAnnotationPresent(Schema.class))).isTrue();
    }

    @Test
    void shouldCreateInstanceWithRequiredScope() {
        var schema =
                new ScopeProblemDetailSchema(
                        URI.create("https://example.com/insufficient-scope"),
                        "Forbidden",
                        403,
                        "Insufficient scope for this resource",
                        URI.create("/api/v1/users/me"),
                        "drinkwater:v1:user:profile:read");

        assertThat(schema.type()).hasToString("https://example.com/insufficient-scope");
        assertThat(schema.title()).isEqualTo("Forbidden");
        assertThat(schema.status()).isEqualTo(403);
        assertThat(schema.detail()).isEqualTo("Insufficient scope for this resource");
        assertThat(schema.instance()).hasToString("/api/v1/users/me");
        assertThat(schema.requiredScope()).isEqualTo("drinkwater:v1:user:profile:read");
    }
}
