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