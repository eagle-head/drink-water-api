package br.com.drinkwater.hydrationtracking.exception;

import java.util.List;

public class InvalidFilterException extends RuntimeException {
    private final List<String> errors;

    public InvalidFilterException(List<String> errors) {
        super("Invalid filter parameters");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}