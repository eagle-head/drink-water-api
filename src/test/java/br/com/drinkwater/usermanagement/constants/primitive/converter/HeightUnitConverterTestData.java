package br.com.drinkwater.usermanagement.constants.primitive.converter;

import br.com.drinkwater.usermanagement.model.HeightUnit;

public final class HeightUnitConverterTestData {
    
    private HeightUnitConverterTestData() {
    }

    // Entity to Database
    public static final HeightUnit VALID_CM_ENTITY = HeightUnit.CM;
    public static final Integer VALID_CM_DB = 1;
    
    // Invalid data
    public static final Integer INVALID_DB_CODE = 999;
    public static final HeightUnit NULL_ENTITY = null;
    public static final Integer NULL_DB = null;
}