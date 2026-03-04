package br.com.drinkwater.exception;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.net.URI;
import java.util.List;

/**
 * OpenAPI schema representing a validation error response (RFC 7807 + field-level errors).
 *
 * <p>Extends the standard Problem Detail with an {@code errors} array containing per-field
 * validation messages, as produced by {@link GlobalExceptionHandler} for 400 Bad Request responses.
 */
@Schema(description = "RFC 7807 Problem Detail with field-level validation errors")
public record ValidationProblemDetailSchema(
        @Schema(
                        description = "URI identifying the problem type",
                        example = "https://www.drinkwater.com.br/validation-error")
                URI type,
        @Schema(
                        description = "Short human-readable summary of the problem",
                        example = "Bad Request")
                String title,
        @Schema(description = "HTTP status code", example = "400") int status,
        @Schema(
                        description = "Human-readable explanation specific to this occurrence",
                        example = "Validation failed")
                String detail,
        @Schema(
                        description = "URI identifying the specific occurrence of the problem",
                        example = "/api/v1/users")
                URI instance,
        @ArraySchema(schema = @Schema(implementation = FieldErrorSchema.class), minItems = 1)
                List<FieldErrorSchema> errors) {

    public ValidationProblemDetailSchema {
        errors = errors == null ? List.of() : List.copyOf(errors);
    }

    @Schema(description = "Individual field validation error")
    public record FieldErrorSchema(
            @Schema(description = "Name of the field that failed validation", example = "email")
                    String field,
            @Schema(
                            description = "Human-readable error message for this field",
                            example = "must not be blank")
                    String message) {}
}
