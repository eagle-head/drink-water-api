package br.com.drinkwater.hydrationtracking.model;


public enum VolumeUnit {

    ML(1);

    private final int code;

    VolumeUnit(int code) {
        this.code = code;
    }

    public static VolumeUnit fromCode(int code) {
        for (VolumeUnit unit : VolumeUnit.values()) {
            if (unit.getCode() == code) {
                return unit;
            }
        }

        throw new IllegalArgumentException("Invalid VolumeUnit code: " + code);
    }

    public int getCode() {
        return this.code;
    }
}
