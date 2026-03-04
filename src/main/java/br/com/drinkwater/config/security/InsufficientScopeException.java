package br.com.drinkwater.config.security;

/**
 * Thrown when a JWT-authenticated request lacks the required OAuth scope for the target endpoint.
 * Carries the missing scope name so that the error response can include it, enabling clients to
 * trigger incremental consent.
 */
public class InsufficientScopeException extends RuntimeException {

    private final String requiredScope;

    public InsufficientScopeException(String requiredScope) {
        super("Insufficient scope: " + requiredScope);
        this.requiredScope = requiredScope;
    }

    public String getRequiredScope() {
        return requiredScope;
    }
}
