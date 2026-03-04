package br.com.drinkwater.usermanagement.exception;

/**
 * Thrown when attempting to create a user profile with a Keycloak public ID that already exists.
 */
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException() {
        super("User already exists");
    }

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
