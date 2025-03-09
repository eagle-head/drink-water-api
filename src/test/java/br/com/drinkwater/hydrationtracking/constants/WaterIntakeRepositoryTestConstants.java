package br.com.drinkwater.hydrationtracking.constants;

import br.com.drinkwater.hydrationtracking.model.VolumeUnit;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class WaterIntakeRepositoryTestConstants {

    private WaterIntakeRepositoryTestConstants() {
    }

    public static final Long REPOSITORY_WATER_INTAKE_ID = 100L;
    public static final Long REPOSITORY_DUPLICATE_WATER_INTAKE_ID = 101L;
    public static final OffsetDateTime REPOSITORY_WATER_INTAKE_DATE_TIME_UTC = OffsetDateTime.now()
            .withOffsetSameInstant(ZoneOffset.UTC)
            .withSecond(0)
            .withNano(0);
    public static final int REPOSITORY_WATER_INTAKE_VOLUME = 350;
    public static final int REPOSITORY_DUPLICATE_WATER_INTAKE_VOLUME = 400;
    public static final VolumeUnit REPOSITORY_WATER_INTAKE_VOLUME_UNIT = VolumeUnit.ML;
}