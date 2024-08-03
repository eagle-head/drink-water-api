package br.com.drinkwater.drinkwaterapi.exception;

import lombok.Getter;

@Getter
public enum ProblemDetailResponseType {

    VALIDATION_ERROR("Validation error", "/validation-error"),
    CONFLICT("Data conflict occurred.", "/data-conflict");

    private final String title;
    private final String uri;

    ProblemDetailResponseType(String title, String path) {
        this.title = title;
        this.uri = "https://www.drinkwater.com.br" + path;
    }
}
