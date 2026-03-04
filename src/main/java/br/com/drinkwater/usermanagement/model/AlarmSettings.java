package br.com.drinkwater.usermanagement.model;

import java.time.LocalTime;
import java.util.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.Nullable;

/**
 * Immutable entity representing hydration reminder alarm settings in the {@code alarm_settings}
 * table. Associated with a {@link User} via a one-to-one mapped collection.
 *
 * <p>Invariants enforced by the constructor: goal and interval must be positive, start/end times
 * must be non-null, and end time must not be before start time.
 */
@Table("alarm_settings")
public final class AlarmSettings {

    private static final String GOAL_POSITIVE = "Goal must be greater than zero";
    private static final String INTERVAL_POSITIVE = "Interval minutes must be greater than zero";
    private static final String START_TIME_REQUIRED = "Daily start time cannot be null";
    private static final String END_TIME_REQUIRED = "Daily end time cannot be null";
    private static final String END_BEFORE_START =
            "Daily end time cannot be before daily start time";

    @Id
    @Column("id")
    @Nullable
    private final Long id;

    @Column("goal")
    private final int goal;

    @Column("interval_minutes")
    private final int intervalMinutes;

    @Column("daily_start_time")
    private final LocalTime dailyStartTime;

    @Column("daily_end_time")
    private final LocalTime dailyEndTime;

    public AlarmSettings(
            int goal, int intervalMinutes, LocalTime dailyStartTime, LocalTime dailyEndTime) {
        this(null, goal, intervalMinutes, dailyStartTime, dailyEndTime);
    }

    @PersistenceCreator
    public AlarmSettings(
            @Nullable Long id,
            int goal,
            int intervalMinutes,
            LocalTime dailyStartTime,
            LocalTime dailyEndTime) {
        validateFields(goal, intervalMinutes, dailyStartTime, dailyEndTime);
        this.id = id;
        this.goal = goal;
        this.intervalMinutes = intervalMinutes;
        this.dailyStartTime = dailyStartTime;
        this.dailyEndTime = dailyEndTime;
    }

    /**
     * Returns a copy with the given database ID.
     *
     * @param id the database ID, or null for a new entity
     * @return a new AlarmSettings instance with the given ID
     */
    public AlarmSettings withId(@Nullable Long id) {
        return new AlarmSettings(id, goal, intervalMinutes, dailyStartTime, dailyEndTime);
    }

    /**
     * Returns a copy with updated alarm fields, preserving the current ID.
     *
     * @param goal the daily water intake goal in milliliters (must be positive)
     * @param intervalMinutes the reminder interval in minutes (must be positive)
     * @param dailyStartTime the daily reminder start time
     * @param dailyEndTime the daily reminder end time (must not be before start time)
     * @return a new AlarmSettings instance with the updated fields
     */
    public AlarmSettings withUpdatedFields(
            int goal, int intervalMinutes, LocalTime dailyStartTime, LocalTime dailyEndTime) {
        return new AlarmSettings(this.id, goal, intervalMinutes, dailyStartTime, dailyEndTime);
    }

    @Nullable
    public Long getId() {
        return id;
    }

    public int getGoal() {
        return goal;
    }

    public int getIntervalMinutes() {
        return intervalMinutes;
    }

    public LocalTime getDailyStartTime() {
        return dailyStartTime;
    }

    public LocalTime getDailyEndTime() {
        return dailyEndTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AlarmSettings that)) {
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
        return "AlarmSettings{"
                + "id="
                + id
                + ", goal="
                + goal
                + ", intervalMinutes="
                + intervalMinutes
                + ", dailyStartTime="
                + dailyStartTime
                + ", dailyEndTime="
                + dailyEndTime
                + '}';
    }

    private static void validateFields(
            int goal, int intervalMinutes, LocalTime dailyStartTime, LocalTime dailyEndTime) {
        if (goal <= 0) {
            throw new IllegalArgumentException(GOAL_POSITIVE);
        }

        if (intervalMinutes <= 0) {
            throw new IllegalArgumentException(INTERVAL_POSITIVE);
        }

        Objects.requireNonNull(dailyStartTime, START_TIME_REQUIRED);
        Objects.requireNonNull(dailyEndTime, END_TIME_REQUIRED);

        if (dailyEndTime.isBefore(dailyStartTime)) {
            throw new IllegalArgumentException(END_BEFORE_START);
        }
    }
}
