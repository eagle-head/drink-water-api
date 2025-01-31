package br.com.drinkwater.usermanagement.constants.dto;

import br.com.drinkwater.usermanagement.constants.primitive.DateTimeTestData;
import br.com.drinkwater.usermanagement.dto.AlarmSettingsResponseDTO;

public final class AlarmSettingsResponseDtoTestData {

    private AlarmSettingsResponseDtoTestData() {
    }

    public static final AlarmSettingsResponseDTO DEFAULT = new AlarmSettingsResponseDTO(
            2000,
            60,
            DateTimeTestData.START_OF_DAY,
            DateTimeTestData.END_OF_DAY
    );

    public static final AlarmSettingsResponseDTO WITH_MAX_VALUES = new AlarmSettingsResponseDTO(
            10000,
            240,
            DateTimeTestData.START_OF_DAY,
            DateTimeTestData.END_OF_DAY
    );

    public static final AlarmSettingsResponseDTO WITH_MIN_VALUES = new AlarmSettingsResponseDTO(
            50,
            15,
            DateTimeTestData.START_OF_DAY,
            DateTimeTestData.END_OF_DAY
    );
}