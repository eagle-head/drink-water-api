package br.com.drinkwater.config.security;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Component;

/**
 * Inspects an {@link AuthorizationDeniedException} to determine whether the denial was caused by a
 * missing OAuth scope.
 *
 * <p>Spring Security includes the SpEL expression from {@code @PreAuthorize} in the exception
 * message (e.g. {@code "Access Denied"}). The {@code AuthorizationResult} message typically
 * contains the expression text. This handler parses the message for {@code SCOPE_} patterns.
 */
@Component
public class ScopeAwareAccessDeniedHandler {

    private static final Pattern SCOPE_PATTERN = Pattern.compile("SCOPE_(drinkwater:[^'\"\\s)]+)");

    /**
     * Extracts the missing scope from an authorization denial, if present.
     *
     * @return the required scope if the denial was caused by a missing scope, or empty otherwise
     */
    public Optional<String> extractMissingScope(AuthorizationDeniedException ex) {
        String toSearch = ex.getMessage() + " " + ex.getAuthorizationResult().toString();

        Matcher matcher = SCOPE_PATTERN.matcher(toSearch);
        if (matcher.find()) {
            return Optional.of(matcher.group(1));
        }
        return Optional.empty();
    }
}
