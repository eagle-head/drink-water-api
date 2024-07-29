package br.com.drinkwater.drinkwaterapi.usermanagement.exception;

public class EmailAlreadyUsedException extends RuntimeException {

    public EmailAlreadyUsedException(String message) {
        super(message);
    }
}
