package br.com.drinkwater.usermanagement.constants.model.embedded;

import br.com.drinkwater.usermanagement.constants.primitive.DateTimeTestData;
import br.com.drinkwater.usermanagement.constants.primitive.enums.BiologicalSexTestData;
import br.com.drinkwater.usermanagement.model.Personal;

public final class PersonalTestData {

    private PersonalTestData() {
    }

    // Valid data
    public static final Personal DEFAULT = createDefault();
    public static final Personal WITH_LONG_NAMES = createWithLongNames();
    public static final Personal WITH_SPECIAL_CHARS = createWithSpecialChars();

    // Invalid data
    public static final Personal NULL = null;
    public static final Personal WITH_NULL_FIRST_NAME = createWithNullFirstName();
    public static final Personal WITH_NULL_LAST_NAME = createWithNullLastName();
    public static final Personal WITH_NULL_BIRTH_DATE = createWithNullBirthDate();
    public static final Personal WITH_NULL_BIOLOGICAL_SEX = createWithNullBiologicalSex();

    private static Personal createDefault() {
        Personal personal = new Personal();
        personal.setFirstName("John");
        personal.setLastName("Doe");
        personal.setBirthDate(DateTimeTestData.PAST_DATE);
        personal.setBiologicalSex(BiologicalSexTestData.DEFAULT);
        return personal;
    }

    private static Personal createWithLongNames() {
        Personal personal = new Personal();
        personal.setFirstName("Christopher Alexander");
        personal.setLastName("Winchester Worthington III");
        personal.setBirthDate(DateTimeTestData.PAST_DATE);
        personal.setBiologicalSex(BiologicalSexTestData.DEFAULT);
        return personal;
    }

    private static Personal createWithSpecialChars() {
        Personal personal = new Personal();
        personal.setFirstName("João");
        personal.setLastName("da Silva");
        personal.setBirthDate(DateTimeTestData.PAST_DATE);
        personal.setBiologicalSex(BiologicalSexTestData.DEFAULT);
        return personal;
    }

    private static Personal createWithNullFirstName() {
        Personal personal = new Personal();
        personal.setFirstName(null);
        personal.setLastName("Doe");
        personal.setBirthDate(DateTimeTestData.PAST_DATE);
        personal.setBiologicalSex(BiologicalSexTestData.DEFAULT);
        return personal;
    }

    private static Personal createWithNullLastName() {
        Personal personal = new Personal();
        personal.setFirstName("John");
        personal.setLastName(null);
        personal.setBirthDate(DateTimeTestData.PAST_DATE);
        personal.setBiologicalSex(BiologicalSexTestData.DEFAULT);
        return personal;
    }

    private static Personal createWithNullBirthDate() {
        Personal personal = new Personal();
        personal.setFirstName("John");
        personal.setLastName("Doe");
        personal.setBirthDate(null);
        personal.setBiologicalSex(BiologicalSexTestData.DEFAULT);
        return personal;
    }

    private static Personal createWithNullBiologicalSex() {
        Personal personal = new Personal();
        personal.setFirstName("John");
        personal.setLastName("Doe");
        personal.setBirthDate(DateTimeTestData.PAST_DATE);
        personal.setBiologicalSex(null);
        return personal;
    }
}