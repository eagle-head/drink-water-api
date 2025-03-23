package br.com.drinkwater.hydrationtracking.constants;

import br.com.drinkwater.hydrationtracking.dto.ResponseWaterIntakeDTO;
import br.com.drinkwater.hydrationtracking.dto.WaterIntakeDTO;
import br.com.drinkwater.hydrationtracking.dto.WaterIntakeFilterDTO;
import br.com.drinkwater.hydrationtracking.model.VolumeUnit;
import br.com.drinkwater.hydrationtracking.model.WaterIntake;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import static br.com.drinkwater.usermanagement.constants.UserTestConstants.USER;

public final class WaterIntakeTestConstants {

    private WaterIntakeTestConstants() {
    }

    public static final Long WATER_INTAKE_ID = 5L;

    public static final Instant DATE_TIME_UTC = Instant.now()
            .truncatedTo(ChronoUnit.MINUTES)
            .plusSeconds(10);

    public static final int VOLUME = 250;

    public static final VolumeUnit VOLUME_UNIT = VolumeUnit.ML;

    public static final WaterIntakeDTO WATER_INTAKE_DTO = new WaterIntakeDTO(
            DATE_TIME_UTC,
            VOLUME,
            VOLUME_UNIT
    );

    public static final ResponseWaterIntakeDTO RESPONSE_WATER_INTAKE_DTO = new ResponseWaterIntakeDTO(
            WATER_INTAKE_ID,
            DATE_TIME_UTC,
            VOLUME,
            VOLUME_UNIT
    );

    public static final WaterIntakeFilterDTO FILTER_DTO = new WaterIntakeFilterDTO(
            Instant.now().minus(7, ChronoUnit.DAYS),
            Instant.now(),
            100,
            2000,
            0,
            10,
            "dateTimeUTC",
            "DESC"
    );

    public static final WaterIntake WATER_INTAKE;

    static {
        WATER_INTAKE = new WaterIntake(WATER_INTAKE_ID, DATE_TIME_UTC, VOLUME, VOLUME_UNIT, USER);
    }
}