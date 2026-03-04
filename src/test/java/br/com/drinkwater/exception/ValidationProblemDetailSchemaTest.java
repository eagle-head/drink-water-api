package br.com.drinkwater.exception;

import static org.assertj.core.api.Assertions.assertThat;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

final class ValidationProblemDetailSchemaTest {

    @Test
    void recordShouldHaveSchemaAnnotation() {
        var schema = ValidationProblemDetailSchema.class.getAnnotation(Schema.class);
        assertThat(schema).isNotNull();
        assertThat(schema.description())
                .isEqualTo("RFC 7807 Problem Detail with field-level validation errors");
    }

    @Test
    void allFieldsShouldHaveSchemaOrArraySchemaAnnotation() {
        var fields =
                Arrays.stream(ValidationProblemDetailSchema.class.getDeclaredFields())
                        .filter(f -> !f.isSynthetic())
                        .toList();
        assertThat(fields).hasSize(6);
        var annotatedCount =
                fields.stream()
                        .filter(
                                f ->
                                        f.isAnnotationPresent(Schema.class)
                                                || f.isAnnotationPresent(ArraySchema.class))
                        .count();
        assertThat(annotatedCount).isEqualTo(6);
    }

    @Test
    void shouldCreateInstanceWithErrors() {
        var fieldError =
                new ValidationProblemDetailSchema.FieldErrorSchema("email", "must not be blank");
        var schema =
                new ValidationProblemDetailSchema(
                        URI.create("https://example.com/validation-error"),
                        "Bad Request",
                        400,
                        "Validation failed",
                        URI.create("/api/v1/users"),
                        List.of(fieldError));

        assertThat(schema.type()).hasToString("https://example.com/validation-error");
        assertThat(schema.title()).isEqualTo("Bad Request");
        assertThat(schema.status()).isEqualTo(400);
        assertThat(schema.detail()).isEqualTo("Validation failed");
        assertThat(schema.instance()).hasToString("/api/v1/users");
        assertThat(schema.errors()).hasSize(1);
        assertThat(schema.errors().getFirst().field()).isEqualTo("email");
        assertThat(schema.errors().getFirst().message()).isEqualTo("must not be blank");
    }

    @Test
    void shouldDefaultToEmptyListWhenErrorsIsNull() {
        var schema =
                new ValidationProblemDetailSchema(
                        URI.create("https://example.com/validation-error"),
                        "Bad Request",
                        400,
                        "Validation failed",
                        URI.create("/api/v1/users"),
                        null);

        assertThat(schema.errors()).isNotNull().isEmpty();
    }

    @Test
    void shouldMakeDefensiveCopyOfErrors() {
        var mutableList =
                new java.util.ArrayList<>(
                        List.of(
                                new ValidationProblemDetailSchema.FieldErrorSchema(
                                        "email", "must not be blank")));
        var schema =
                new ValidationProblemDetailSchema(
                        URI.create("https://example.com/validation-error"),
                        "Bad Request",
                        400,
                        "Validation failed",
                        URI.create("/api/v1/users"),
                        mutableList);

        mutableList.clear();
        assertThat(schema.errors()).hasSize(1);
    }

    @Test
    void fieldErrorSchemaShouldHaveSchemaAnnotation() {
        var schema =
                ValidationProblemDetailSchema.FieldErrorSchema.class.getAnnotation(Schema.class);
        assertThat(schema).isNotNull();
        assertThat(schema.description()).isEqualTo("Individual field validation error");
    }

    @Test
    void fieldErrorSchemaShouldHaveAnnotatedFields() {
        var fields =
                Arrays.stream(
                                ValidationProblemDetailSchema.FieldErrorSchema.class
                                        .getDeclaredFields())
                        .filter(f -> !f.isSynthetic())
                        .toList();
        assertThat(fields).hasSize(2);
        assertThat(fields.stream().allMatch(f -> f.isAnnotationPresent(Schema.class))).isTrue();
    }
}
