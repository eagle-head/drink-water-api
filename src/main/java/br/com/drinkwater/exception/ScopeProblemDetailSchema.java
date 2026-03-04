package br.com.drinkwater.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import java.net.URI;

/**
 * OpenAPI schema representing an insufficient scope error response (RFC 7807 + required_scope).
 *
 * <p>Extends the standard Problem Detail with a {@code required_scope} field indicating which OAuth
 * scope is needed, as produced by {@link GlobalExceptionHandler} for 403 Forbidden responses when a
 * specific scope is missing.
 */
@Schema(description = "RFC 7807 Problem Detail with required OAuth scope")
public record ScopeProblemDetailSchema(
        @Schema(
                        description = "URI identifying the problem type",
                        example = "https://www.drinkwater.com.br/insufficient-scope")
                URI type,
        @Schema(description = "Short human-readable summary of the problem", example = "Forbidden")
                String title,
        @Schema(description = "HTTP status code", example = "403") int status,
        @Schema(
                        description = "Human-readable explanation specific to this occurrence",
                        example = "Insufficient scope for this resource")
                String detail,
        @Schema(
                        description = "URI identifying the specific occurrence of the problem",
                        example = "/api/v1/users/me")
                URI instance,
        @Schema(
                        name = "required_scope",
                        description = "OAuth scope required to access this resource",
                        example = "drinkwater:v1:user:profile:read")
                String requiredScope) {}
