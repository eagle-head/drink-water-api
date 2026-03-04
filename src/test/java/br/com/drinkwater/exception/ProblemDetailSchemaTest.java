package br.com.drinkwater.exception;

import static org.assertj.core.api.Assertions.assertThat;

import io.swagger.v3.oas.annotations.media.Schema;
import java.net.URI;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

final class ProblemDetailSchemaTest {

    @Test
    void recordShouldHaveSchemaAnnotation() {
        var schema = ProblemDetailSchema.class.getAnnotation(Schema.class);
        assertThat(schema).isNotNull();
        assertThat(schema.description()).isEqualTo("RFC 7807 Problem Detail response");
    }

    @Test
    void allFieldsShouldHaveSchemaAnnotation() {
        var fields =
                Arrays.stream(ProblemDetailSchema.class.getDeclaredFields())
                        .filter(f -> !f.isSynthetic())
                        .toList();
        assertThat(fields).hasSize(5);
        assertThat(fields.stream().allMatch(f -> f.isAnnotationPresent(Schema.class))).isTrue();
    }

    @Test
    void shouldCreateInstanceWithAllFields() {
        var schema =
                new ProblemDetailSchema(
                        URI.create("https://example.com/not-found"),
                        "Not Found",
                        404,
                        "User not found",
                        URI.create("/api/v1/users/me"));

        assertThat(schema.type()).hasToString("https://example.com/not-found");
        assertThat(schema.title()).isEqualTo("Not Found");
        assertThat(schema.status()).isEqualTo(404);
        assertThat(schema.detail()).isEqualTo("User not found");
        assertThat(schema.instance()).hasToString("/api/v1/users/me");
    }
}
