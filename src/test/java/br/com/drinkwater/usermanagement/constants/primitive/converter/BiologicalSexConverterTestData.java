package br.com.drinkwater.usermanagement.constants.primitive.converter;

import br.com.drinkwater.usermanagement.model.BiologicalSex;

public final class BiologicalSexConverterTestData {
    
    private BiologicalSexConverterTestData() {
    }

    // Entity to Database
    public static final BiologicalSex VALID_MALE_ENTITY = BiologicalSex.MALE;
    public static final BiologicalSex VALID_FEMALE_ENTITY = BiologicalSex.FEMALE;
    public static final Integer VALID_MALE_DB = 1;
    public static final Integer VALID_FEMALE_DB = 2;
    
    // Invalid data
    public static final Integer INVALID_DB_CODE = 999;
    public static final BiologicalSex NULL_ENTITY = null;
    public static final Integer NULL_DB = null;
}