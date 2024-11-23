package br.com.drinkwater.usermanagement.model;

public enum HeightUnit {

    CM(1);

    private final int code;

    HeightUnit(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
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
