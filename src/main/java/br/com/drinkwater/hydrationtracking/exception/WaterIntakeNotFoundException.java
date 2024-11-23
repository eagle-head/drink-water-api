package br.com.drinkwater.hydrationtracking.exception;

public class WaterIntakeNotFoundException extends RuntimeException {

    public WaterIntakeNotFoundException(String message) {
        super(message);
    }
}
