package br.com.drinkwater.usermanagement.model;

import java.math.BigDecimal;
import java.util.Objects;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Column;

/**
 * Immutable embedded value object representing physical measurements of a user. Stores unit types
 * as integer codes mapped via {@link WeightUnit} and {@link HeightUnit}.
 *
 * <p>The business constructor validates that weight, weight unit, height, and height unit are all
 * non-null.
 */
public final class Physical {

    @Column("weight")
    private final BigDecimal weight;

    @Column("weight_unit")
    private final int weightUnitCode;

    @Column("height")
    private final BigDecimal height;

    @Column("height_unit")
    private final int heightUnitCode;

    public Physical(
            BigDecimal weight, WeightUnit weightUnit, BigDecimal height, HeightUnit heightUnit) {
        Objects.requireNonNull(weight, "Weight cannot be null");
        Objects.requireNonNull(weightUnit, "Weight unit cannot be null");
        Objects.requireNonNull(height, "Height cannot be null");
        Objects.requireNonNull(heightUnit, "Height unit cannot be null");

        this.weight = weight;
        this.weightUnitCode = weightUnit.getCode();
        this.height = height;
        this.heightUnitCode = heightUnit.getCode();
    }

    @PersistenceCreator
    public Physical(BigDecimal weight, int weightUnitCode, BigDecimal height, int heightUnitCode) {
        this.weight = weight;
        this.weightUnitCode = weightUnitCode;
        this.height = height;
        this.heightUnitCode = heightUnitCode;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public WeightUnit getWeightUnit() {
        return WeightUnit.fromCode(weightUnitCode);
    }

    public BigDecimal getHeight() {
        return height;
    }

    public HeightUnit getHeightUnit() {
        return HeightUnit.fromCode(heightUnitCode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Physical physical)) {
            return false;
        }

        return weightUnitCode == physical.weightUnitCode
                && heightUnitCode == physical.heightUnitCode
                && Objects.equals(weight, physical.weight)
                && Objects.equals(height, physical.height);
    }

    @Override
    public int hashCode() {
        return Objects.hash(weight, weightUnitCode, height, heightUnitCode);
    }

    @Override
    public String toString() {
        return "Physical{"
                + "weight="
                + weight
                + ", weightUnit="
                + getWeightUnit()
                + ", height="
                + height
                + ", heightUnit="
                + getHeightUnit()
                + '}';
    }
}
