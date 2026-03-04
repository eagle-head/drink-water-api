package br.com.drinkwater.config.properties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Immutable configuration properties for the Caffeine cache that maps user publicId (UUID) to
 * userId (Long). All properties are loaded at bootstrap time and cannot be modified at runtime.
 */
@ConfigurationProperties(prefix = "cache.user-id-by-public-id")
@Validated
public record CacheProperties(
        @NotNull(message = "CACHE_USER_ID_MAX_SIZE environment variable is required")
                @Min(value = 100, message = "CACHE_USER_ID_MAX_SIZE must be at least 100")
                @Max(value = 1_000_000, message = "CACHE_USER_ID_MAX_SIZE cannot exceed 1000000")
                Integer maxSize,
        @NotNull(message = "CACHE_USER_ID_EXPIRE_MINUTES environment variable is required")
                @Min(value = 1, message = "CACHE_USER_ID_EXPIRE_MINUTES must be at least 1 minute")
                @Max(
                        value = 1440,
                        message = "CACHE_USER_ID_EXPIRE_MINUTES cannot exceed 1440 (24 hours)")
                Integer expireAfterWriteMinutes) {}
