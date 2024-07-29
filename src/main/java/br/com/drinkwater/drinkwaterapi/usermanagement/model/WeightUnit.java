package br.com.drinkwater.drinkwaterapi.usermanagement.model;

import lombok.Getter;

@Getter
public enum WeightUnit {

    KG(1);

    private final int code;

    WeightUnit(int code) {
        this.code = code;
    }

    public static WeightUnit fromCode(int code) {
        for (WeightUnit unit : WeightUnit.values()) {
            if (unit.getCode() == code) {
                return unit;
            }
        }

        throw new IllegalArgumentException("Invalid WeightUnit code: " + code);
    }
}
