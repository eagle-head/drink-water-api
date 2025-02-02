package br.com.drinkwater.usermanagement.converter;


import br.com.drinkwater.usermanagement.model.BiologicalSex;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class BiologicalSexConverter implements AttributeConverter<BiologicalSex, Integer> {

    @Override
    public Integer convertToDatabaseColumn(BiologicalSex attribute) {
        if (attribute == null) {
            return null;
        }

        return attribute.getCode();
    }

    @Override
    public BiologicalSex convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }

        return BiologicalSex.fromCode(dbData);
    }
}
