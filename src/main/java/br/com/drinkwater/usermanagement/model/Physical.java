package br.com.drinkwater.usermanagement.model;

import br.com.drinkwater.usermanagement.converter.HeightUnitConverter;
import br.com.drinkwater.usermanagement.converter.WeightUnitConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.util.Objects;

@Embeddable
public class Physical {

    @Column(nullable = false)
    private BigDecimal weight;

    @Convert(converter = WeightUnitConverter.class)
    @Column(name = "weight_unit", nullable = false)
    private WeightUnit weightUnit;

    @Column(nullable = false)
    private BigDecimal height;

    @Convert(converter = HeightUnitConverter.class)
    @Column(name = "height_unit", nullable = false)
    private HeightUnit heightUnit;

    /**
     * Default constructor required by JPA/Hibernate
     */
    protected Physical() {
        // Empty constructor needed for JPA
    }

    /**
     * Constructor with validations to create a valid Physical instance
     *
     * @param weight     weight of the person (required)
     * @param weightUnit weight unit of measurement (required)
     * @param height     height of the person (required)
     * @param heightUnit height unit of measurement (required)
     * @throws IllegalArgumentException if any parameter fails validation
     */
    public Physical(BigDecimal weight, WeightUnit weightUnit, BigDecimal height, HeightUnit heightUnit) {
        if (weight == null) {
            throw new IllegalArgumentException("Weight cannot be null");
        }

        if (weightUnit == null) {
            throw new IllegalArgumentException("Weight unit cannot be null");
        }

        if (height == null) {
            throw new IllegalArgumentException("Height cannot be null");
        }

        if (heightUnit == null) {
            throw new IllegalArgumentException("Height unit cannot be null");
        }

        this.weight = weight;
        this.weightUnit = weightUnit;
        this.height = height;
        this.heightUnit = heightUnit;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public WeightUnit getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(WeightUnit weightUnit) {
        this.weightUnit = weightUnit;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public HeightUnit getHeightUnit() {
        return heightUnit;
    }

    public void setHeightUnit(HeightUnit heightUnit) {
        this.heightUnit = heightUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Physical physical)) return false;

        return Objects.equals(weight, physical.weight) &&
                weightUnit == physical.weightUnit &&
                Objects.equals(height, physical.height) &&
                heightUnit == physical.heightUnit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(weight, weightUnit, height, heightUnit);
    }

    @Override
    public String toString() {
        return "Physical{" +
                "weight=" + weight +
                ", weightUnit=" + weightUnit +
                ", height=" + height +
                ", heightUnit=" + heightUnit +
                '}';
    }
}