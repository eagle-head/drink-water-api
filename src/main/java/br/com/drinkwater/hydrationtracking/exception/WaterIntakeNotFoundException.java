package br.com.drinkwater.hydrationtracking.exception;

/** Thrown when a water intake record cannot be found by its ID for the authenticated user. */
public class WaterIntakeNotFoundException extends RuntimeException {

    public WaterIntakeNotFoundException(String message) {
        super(message);
    }
}
