package br.com.drinkwater.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Locale;

/**
 * Immutable configuration properties for locale and internationalization settings.
 * All properties are loaded at bootstrap time and cannot be modified at runtime.
 */
@ConfigurationProperties(prefix = "locale")
@Validated
public record LocaleProperties(

        @NotBlank(message = "DEFAULT_LOCALE environment variable is required")
        String defaultLocale,

        @NotNull(message = "SUPPORTED_LOCALES environment variable is required")
        List<String> supported
) {

    /**
     * Constructor with strict validation - no defaults.
     */
    public LocaleProperties {
        // All properties are required - no defaults allowed
    }

    /**
     * Gets the default locale as a Locale object.
     *
     * @return the default Locale
     */
    public Locale getDefaultLocale() {
        return parseLocale(defaultLocale);
    }

    /**
     * Gets the supported locales as Locale objects.
     *
     * @return list of supported Locale objects
     */
    public List<Locale> getSupportedLocales() {
        return supported.stream()
                .map(this::parseLocale)
                .toList();
    }

    /**
     * Parses a locale string in format "en_US" or "pt_BR" to a Locale object.
     *
     * @param localeString the locale string to parse
     * @return the parsed Locale
     */
    private Locale parseLocale(String localeString) {
        String[] parts = localeString.split("_");
        if (parts.length == 2) {
            return new Locale(parts[0], parts[1]);
        } else if (parts.length == 1) {
            return new Locale(parts[0]);
        } else {
            throw new IllegalArgumentException("Invalid locale format: " + localeString + ". Expected format: 'en_US' or 'en'");
        }
    }

    /**
     * Validates that the default locale is included in supported locales.
     *
     * @return true if default locale is supported
     */
    public boolean isDefaultLocaleSupported() {
        return supported.contains(defaultLocale);
    }
}