package br.com.drinkwater.core.validation;


public final class ValidationResult<T> {

    private final boolean valid;
    private final String errorMessage;
    private final T value;

    private ValidationResult(boolean valid, String errorMessage, T value) {
        this.valid = valid;
        this.errorMessage = errorMessage;
        this.value = value;
    }

    public boolean isValid() { 
        return valid; 
    }

    public String getErrorMessage() { 
        return errorMessage; 
    }

    public T getValue() { 
        return value; 
    }

    public static <T> ValidationResult<T> valid(T value) {
        return new ValidationResult<>(true, null, value);
    }

    public static <T> ValidationResult<T> invalid(String message) {
        return new ValidationResult<>(false, message, null);
    }
}