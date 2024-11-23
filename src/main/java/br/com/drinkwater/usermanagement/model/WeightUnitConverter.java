package br.com.drinkwater.usermanagement.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class WeightUnitConverter implements AttributeConverter<WeightUnit, Integer> {

    @Override
    public Integer convertToDatabaseColumn(WeightUnit attribute) {
        if (attribute == null) {
            return null;
        }

        return attribute.getCode();
    }

    @Override
    public WeightUnit convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }

        return WeightUnit.fromCode(dbData);
    }
}
