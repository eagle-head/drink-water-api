package br.com.drinkwater.support;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Mock authentication provider for integration tests when Keycloak is not available.
 * Returns predefined token strings that are recognized by the MockJwtDecoder.
 */
@Component
@Profile("it-no-containers")
public class MockTestAuthProvider {

    private static final String JOHN_DOE_TOKEN = "mock-jwt-token-john-doe";
    private static final String NEW_USER_TOKEN = "mock-jwt-token-new-user";

    /**
     * Returns a mock JWT token for John Doe user.
     * This token is recognized by the MockJwtDecoder and returns the appropriate JWT claims.
     *
     * @return Mock JWT token string for John Doe
     */
    public String getJohnDoeToken() {
        return JOHN_DOE_TOKEN;
    }

    /**
     * Returns a mock JWT token for a new user.
     * This token is recognized by the MockJwtDecoder and returns the appropriate JWT claims.
     *
     * @return Mock JWT token string for new user
     */
    public String getNewUserToken() {
        return NEW_USER_TOKEN;
    }
}