package br.com.drinkwater.drinkwaterapi.usermanagement.model;

import lombok.Getter;

@Getter
public enum BiologicalSex {

    MALE(1),
    FEMALE(2);

    private final int code;

    BiologicalSex(int code) {
        this.code = code;
    }

    public static BiologicalSex fromCode(int code) {
        for (BiologicalSex sex : BiologicalSex.values()) {
            if (sex.getCode() == code) {
                return sex;
            }
        }

        throw new IllegalArgumentException("Invalid BiologicalSex code: " + code);
    }
}
