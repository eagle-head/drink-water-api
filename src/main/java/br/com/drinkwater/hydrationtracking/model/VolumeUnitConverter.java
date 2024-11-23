package br.com.drinkwater.hydrationtracking.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class VolumeUnitConverter implements AttributeConverter<VolumeUnit, Integer> {

    @Override
    public Integer convertToDatabaseColumn(VolumeUnit attribute) {
        if (attribute == null) {
            return null;
        }

        return attribute.getCode();
    }

    @Override
    public VolumeUnit convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }

        return VolumeUnit.fromCode(dbData);
    }
}
