package br.com.drinkwater.hydrationtracking.model;

import java.time.Instant;
import java.util.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.Nullable;

/**
 * Immutable entity representing a single water intake record in the {@code water_intakes} table.
 * Stores the volume unit as an integer code mapped via {@link VolumeUnit}. Equality is based on the
 * database ID.
 *
 * <p>Business constructors enforce that date/time is non-null, volume is positive, volume unit is
 * non-null, and user ID is non-null. The constructor for existing entities also requires a positive
 * ID.
 */
@Table("water_intakes")
public final class WaterIntake {

    private static final String DATETIME_REQUIRED = "Date and time cannot be null";
    private static final String VOLUME_POSITIVE = "Volume must be greater than zero";
    private static final String VOLUME_UNIT_REQUIRED = "Volume unit cannot be null";
    private static final String USER_ID_REQUIRED = "User ID cannot be null";
    private static final String ID_REQUIRED = "ID cannot be null for existing entity";
    private static final String ID_POSITIVE = "ID must be greater than zero";

    @Id
    @Column("id")
    @Nullable
    private final Long id;

    @Column("date_time_utc")
    private final Instant dateTimeUTC;

    @Column("volume")
    private final int volume;

    @Column("volume_unit")
    private final int volumeUnitCode;

    @Column("user_id")
    private final Long userId;

    @PersistenceCreator
    public WaterIntake(
            @Nullable Long id, Instant dateTimeUTC, int volume, int volumeUnitCode, Long userId) {
        this.id = id;
        this.dateTimeUTC = dateTimeUTC;
        this.volume = volume;
        this.volumeUnitCode = volumeUnitCode;
        this.userId = userId;
    }

    public WaterIntake(Instant dateTimeUTC, int volume, VolumeUnit volumeUnit, Long userId) {
        Objects.requireNonNull(dateTimeUTC, DATETIME_REQUIRED);
        if (volume <= 0) {
            throw new IllegalArgumentException(VOLUME_POSITIVE);
        }
        Objects.requireNonNull(volumeUnit, VOLUME_UNIT_REQUIRED);
        Objects.requireNonNull(userId, USER_ID_REQUIRED);

        this.id = null;
        this.dateTimeUTC = dateTimeUTC;
        this.volume = volume;
        this.volumeUnitCode = volumeUnit.getCode();
        this.userId = userId;
    }

    public WaterIntake(
            Long id, Instant dateTimeUTC, int volume, VolumeUnit volumeUnit, Long userId) {
        Objects.requireNonNull(id, ID_REQUIRED);
        if (id <= 0) {
            throw new IllegalArgumentException(ID_POSITIVE);
        }
        Objects.requireNonNull(dateTimeUTC, DATETIME_REQUIRED);
        if (volume <= 0) {
            throw new IllegalArgumentException(VOLUME_POSITIVE);
        }
        Objects.requireNonNull(volumeUnit, VOLUME_UNIT_REQUIRED);
        Objects.requireNonNull(userId, USER_ID_REQUIRED);

        this.id = id;
        this.dateTimeUTC = dateTimeUTC;
        this.volume = volume;
        this.volumeUnitCode = volumeUnit.getCode();
        this.userId = userId;
    }

    @Nullable
    public Long getId() {
        return id;
    }

    public Instant getDateTimeUTC() {
        return dateTimeUTC;
    }

    public int getVolume() {
        return volume;
    }

    public VolumeUnit getVolumeUnit() {
        return VolumeUnit.fromCode(volumeUnitCode);
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WaterIntake that)) {
            return false;
        }
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "WaterIntake{"
                + "id="
                + id
                + ", dateTimeUTC="
                + dateTimeUTC
                + ", volume="
                + volume
                + ", volumeUnit="
                + getVolumeUnit()
                + ", userId="
                + userId
                + '}';
    }
}
