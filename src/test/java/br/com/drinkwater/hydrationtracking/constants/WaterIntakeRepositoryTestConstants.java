package br.com.drinkwater.hydrationtracking.constants;

import br.com.drinkwater.hydrationtracking.model.VolumeUnit;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public final class WaterIntakeRepositoryTestConstants {

    private WaterIntakeRepositoryTestConstants() {
    }

    public static final Instant REPOSITORY_WATER_INTAKE_DATE_TIME_UTC = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    public static final int REPOSITORY_WATER_INTAKE_VOLUME = 350;
    public static final int REPOSITORY_DUPLICATE_WATER_INTAKE_VOLUME = 400;
    public static final VolumeUnit REPOSITORY_WATER_INTAKE_VOLUME_UNIT = VolumeUnit.ML;
}