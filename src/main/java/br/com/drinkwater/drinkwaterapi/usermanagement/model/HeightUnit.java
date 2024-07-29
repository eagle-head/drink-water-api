package br.com.drinkwater.drinkwaterapi.usermanagement.model;

import lombok.Getter;

@Getter
public enum HeightUnit {

    CM(1);

    private final int code;

    HeightUnit(int code) {
        this.code = code;
    }

    public static HeightUnit fromCode(int code) {
        for (HeightUnit unit : HeightUnit.values()) {
            if (unit.getCode() == code) {
                return unit;
            }
        }

        throw new IllegalArgumentException("Invalid HeightUnit code: " + code);
    }
}
