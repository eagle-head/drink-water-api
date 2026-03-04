package br.com.drinkwater.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import java.net.URI;

/**
 * OpenAPI schema representing an RFC 7807 Problem Detail response.
 *
 * <p>This record exists solely for accurate OpenAPI documentation. The actual responses are
 * produced by Spring's {@link org.springframework.http.ProblemDetail} and serialized via Jackson's
 * {@code ProblemDetailJacksonMixin}, which flattens the internal {@code properties} map into
 * top-level JSON fields. Using {@code ProblemDetail.class} directly in
 * {@code @Schema(implementation = ...)} causes springdoc to expose the raw {@code properties} map
 * as a visible field, which does not match the real JSON output.
 */
@Schema(description = "RFC 7807 Problem Detail response")
public record ProblemDetailSchema(
        @Schema(
                        description = "URI identifying the problem type",
                        example = "https://www.drinkwater.com.br/user-not-found")
                URI type,
        @Schema(description = "Short human-readable summary of the problem", example = "Not Found")
                String title,
        @Schema(description = "HTTP status code", example = "404") int status,
        @Schema(
                        description = "Human-readable explanation specific to this occurrence",
                        example = "User not found")
                String detail,
        @Schema(
                        description = "URI identifying the specific occurrence of the problem",
                        example = "/api/v1/users/me")
                URI instance) {}
