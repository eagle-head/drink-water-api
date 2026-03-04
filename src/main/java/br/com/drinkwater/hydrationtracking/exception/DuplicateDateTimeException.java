package br.com.drinkwater.hydrationtracking.exception;

/**
 * Thrown when a water intake record is created or updated with a date/time that already exists for
 * the same user.
 */
public class DuplicateDateTimeException extends RuntimeException {

    public DuplicateDateTimeException(String message) {
        super(message);
    }
}
