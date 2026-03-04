package br.com.drinkwater.api.versioning;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "api.versioning")
@Validated
public record ApiVersionProperties(
        @NotBlank(message = "api.versioning.current-version is required") String currentVersion,
        @NotNull(message = "api.versioning.versions is required")
                List<@Valid VersionConfig> versions) {

    public ApiVersionProperties {
        versions = versions != null ? List.copyOf(versions) : null;
    }

    public record VersionConfig(
            @NotBlank(message = "version identifier is required") String version,
            boolean deprecated,
            String sunset,
            String successorVersion) {}

    public boolean isVersionDeprecated(String version) {
        return versions.stream()
                .filter(v -> v.version().equals(version))
                .findFirst()
                .map(VersionConfig::deprecated)
                .orElse(false);
    }

    public String getSunsetDate(String version) {
        return versions.stream()
                .filter(v -> v.version().equals(version))
                .findFirst()
                .map(VersionConfig::sunset)
                .orElse("");
    }

    public String getSuccessorVersion(String version) {
        return versions.stream()
                .filter(v -> v.version().equals(version))
                .findFirst()
                .map(VersionConfig::successorVersion)
                .orElse("");
    }
}
