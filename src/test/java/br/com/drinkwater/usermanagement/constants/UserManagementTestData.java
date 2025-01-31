package br.com.drinkwater.usermanagement.constants;

import br.com.drinkwater.usermanagement.constants.dto.*;
import br.com.drinkwater.usermanagement.constants.model.*;
import br.com.drinkwater.usermanagement.constants.model.embedded.*;
import br.com.drinkwater.usermanagement.constants.primitive.*;
import br.com.drinkwater.usermanagement.constants.primitive.converter.*;
import br.com.drinkwater.usermanagement.constants.primitive.enums.*;
import br.com.drinkwater.usermanagement.dto.*;
import br.com.drinkwater.usermanagement.model.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public final class UserManagementTestData {

    private UserManagementTestData() {
    }

    // Model constants
    public static final User DEFAULT_USER = UserTestData.DEFAULT;
    public static final Personal DEFAULT_PERSONAL = PersonalTestData.DEFAULT;
    public static final Physical DEFAULT_PHYSICAL = PhysicalTestData.DEFAULT;
    public static final AlarmSettings DEFAULT_ALARM_SETTINGS = AlarmSettingsTestData.DEFAULT;

    // DTO constants
    public static final UserDTO DEFAULT_USER_DTO = UserDtoTestData.DEFAULT;
    public static final UserResponseDTO DEFAULT_USER_RESPONSE_DTO = UserResponseDtoTestData.DEFAULT;
    public static final PersonalDTO DEFAULT_PERSONAL_DTO = PersonalDtoTestData.DEFAULT;
    public static final PhysicalDTO DEFAULT_PHYSICAL_DTO = PhysicalDtoTestData.DEFAULT;
    public static final AlarmSettingsDTO DEFAULT_ALARM_SETTINGS_DTO = AlarmSettingsDtoTestData.DEFAULT;
    public static final AlarmSettingsResponseDTO DEFAULT_ALARM_SETTINGS_RESPONSE_DTO = AlarmSettingsResponseDtoTestData.DEFAULT;

    // Common field values
    public static final UUID DEFAULT_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    public static final String DEFAULT_EMAIL = "john.doe@example.com";
    public static final String DEFAULT_FIRST_NAME = "John";
    public static final String DEFAULT_LAST_NAME = "Doe";
    public static final BigDecimal DEFAULT_WEIGHT = BigDecimal.valueOf(70.0);
    public static final BigDecimal DEFAULT_HEIGHT = BigDecimal.valueOf(170.0);
    public static final int DEFAULT_GOAL = 2000;
    public static final int DEFAULT_INTERVAL = 60;

    // Enums
    public static final BiologicalSex DEFAULT_BIOLOGICAL_SEX = BiologicalSexTestData.DEFAULT;
    public static final WeightUnit DEFAULT_WEIGHT_UNIT = WeightUnitTestData.DEFAULT;
    public static final HeightUnit DEFAULT_HEIGHT_UNIT = HeightUnitTestData.DEFAULT;

    // Dates
    public static final OffsetDateTime DEFAULT_DATE = DateTimeTestData.DEFAULT_DATE;
    public static final OffsetDateTime PAST_DATE = DateTimeTestData.PAST_DATE;
    public static final OffsetDateTime FUTURE_DATE = DateTimeTestData.FUTURE_DATE;
    public static final OffsetDateTime START_OF_DAY = DateTimeTestData.START_OF_DAY;
    public static final OffsetDateTime END_OF_DAY = DateTimeTestData.END_OF_DAY;

    // Invalid test data
    public static final UserDTO INVALID_USER_DTO = UserDtoTestData.WITH_INVALID_EMAIL;
    public static final PersonalDTO INVALID_PERSONAL_DTO = PersonalDtoTestData.WITH_NULL_FIRST_NAME;
    public static final PhysicalDTO INVALID_PHYSICAL_DTO = PhysicalDtoTestData.WITH_NULL_WEIGHT;
    public static final AlarmSettingsDTO INVALID_ALARM_SETTINGS_DTO = AlarmSettingsDtoTestData.WITH_NULL_START_TIME;

    // Database converter values
    public static final Integer VALID_MALE_DB = BiologicalSexConverterTestData.VALID_MALE_DB;
    public static final Integer VALID_FEMALE_DB = BiologicalSexConverterTestData.VALID_FEMALE_DB;
    public static final Integer VALID_KG_DB = WeightUnitConverterTestData.VALID_KG_DB;
    public static final Integer VALID_CM_DB = HeightUnitConverterTestData.VALID_CM_DB;
}