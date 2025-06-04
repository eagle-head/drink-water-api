package br.com.drinkwater.hydrationtracking.model;

import br.com.drinkwater.usermanagement.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "water_intakes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "date_time_utc"})
})
public class WaterIntake {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_time_utc", nullable = false)
    private Instant dateTimeUTC;

    @Column(nullable = false)
    private int volume;

    @Convert(converter = VolumeUnitConverter.class)
    @Column(name = "volume_unit", nullable = false)
    private VolumeUnit volumeUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    /**
     * Default constructor required by JPA/Hibernate
     */
    protected WaterIntake() {
        // Empty constructor needed for JPA
    }

    /**
     * Constructor with validations to create a valid WaterIntake instance
     *
     * @param dateTimeUTC time when the hydration was recorded (required)
     * @param volume      amount of water consumed (must be positive)
     * @param volumeUnit  measurement unit for volume (required)
     * @param user        user associated with this record (required)
     * @throws IllegalArgumentException if any parameter fails validation
     */
    public WaterIntake(Instant dateTimeUTC, int volume, VolumeUnit volumeUnit, User user) {

        if (dateTimeUTC == null) {
            throw new IllegalArgumentException("Date and time cannot be null");
        }

        if (volume <= 0) {
            throw new IllegalArgumentException("Volume must be greater than zero");
        }

        if (volumeUnit == null) {
            throw new IllegalArgumentException("Volume unit cannot be null");
        }

        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        this.dateTimeUTC = dateTimeUTC;
        this.volume = volume;
        this.volumeUnit = volumeUnit;
        this.user = user;
    }

    /**
     * Constructor for existing entities (usually for updates or test fixtures)
     *
     * @param id          identifier of the existing water intake (must be positive)
     * @param dateTimeUTC time when the hydration was recorded
     * @param volume      amount of water consumed
     * @param volumeUnit  measurement unit for volume
     * @param user        user associated with this record
     * @throws IllegalArgumentException if any parameter fails validation
     */
    public WaterIntake(Long id, Instant dateTimeUTC, int volume, VolumeUnit volumeUnit, User user) {

        this(dateTimeUTC, volume, volumeUnit, user);

        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null for existing entity");
        }

        if (id <= 0) {
            throw new IllegalArgumentException("ID must be greater than zero");
        }

        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDateTimeUTC() {
        return dateTimeUTC;
    }

    public void setDateTimeUTC(Instant dateTimeUTC) {
        this.dateTimeUTC = dateTimeUTC;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public VolumeUnit getVolumeUnit() {
        return volumeUnit;
    }

    public void setVolumeUnit(VolumeUnit volumeUnit) {
        this.volumeUnit = volumeUnit;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WaterIntake that)) return false;

        return volume == that.volume &&
                Objects.equals(id, that.id) &&
                Objects.equals(dateTimeUTC, that.dateTimeUTC) &&
                volumeUnit == that.volumeUnit &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dateTimeUTC, volume, volumeUnit,
                user != null ? user.getId() : null);
    }

    @Override
    public String toString() {
        return "WaterIntake{" +
                "id=" + id +
                ", dateTimeUTC=" + dateTimeUTC +
                ", volume=" + volume +
                ", volumeUnit=" + volumeUnit +
                ", userId=" + (user != null ? user.getId() : "null") +
                '}';
    }
}