package br.com.drinkwater.usermanagement.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class HeightUnitConverter implements AttributeConverter<HeightUnit, Integer> {

    @Override
    public Integer convertToDatabaseColumn(HeightUnit attribute) {
        if (attribute == null) {
            return null;
        }

        return attribute.getCode();
    }

    @Override
    public HeightUnit convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }

        return HeightUnit.fromCode(dbData);
    }
}
