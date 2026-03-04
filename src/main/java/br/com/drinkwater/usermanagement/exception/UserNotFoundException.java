package br.com.drinkwater.usermanagement.exception;

/** Thrown when a user cannot be found by their Keycloak public ID. */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("User not found");
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
