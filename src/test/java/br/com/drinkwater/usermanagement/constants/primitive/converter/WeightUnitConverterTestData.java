package br.com.drinkwater.usermanagement.constants.primitive.converter;

import br.com.drinkwater.usermanagement.model.WeightUnit;

public final class WeightUnitConverterTestData {
    
    private WeightUnitConverterTestData() {
    }

    // Entity to Database
    public static final WeightUnit VALID_KG_ENTITY = WeightUnit.KG;
    public static final Integer VALID_KG_DB = 1;
    
    // Invalid data
    public static final Integer INVALID_DB_CODE = 999;
    public static final WeightUnit NULL_ENTITY = null;
    public static final Integer NULL_DB = null;
}