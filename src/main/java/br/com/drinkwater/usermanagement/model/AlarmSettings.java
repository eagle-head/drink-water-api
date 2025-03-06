package br.com.drinkwater.usermanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
public class AlarmSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "goal", nullable = false)
    private int goal;

    @Column(name = "interval_minutes", nullable = false)
    private int intervalMinutes;

    @Column(name = "daily_start_time", nullable = false)
    private OffsetDateTime dailyStartTime;

    @Column(name = "daily_end_time", nullable = false)
    private OffsetDateTime dailyEndTime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonIgnore
    private User user;

    /**
     * Default constructor required by JPA/Hibernate
     */
    protected AlarmSettings() {
        // Empty constructor needed for JPA
    }

    /**
     * Constructor with validations to create a valid AlarmSettings instance
     *
     * @param goal            daily water consumption goal (required)
     * @param intervalMinutes interval in minutes between alarms (required)
     * @param dailyStartTime  time to start reminders during the day (required)
     * @param dailyEndTime    time to stop reminders during the day (required)
     * @throws IllegalArgumentException if any parameter fails validation
     */
    public AlarmSettings(int goal, int intervalMinutes, OffsetDateTime dailyStartTime, OffsetDateTime dailyEndTime) {
        if (goal <= 0) {
            throw new IllegalArgumentException("Goal must be greater than zero");
        }

        if (intervalMinutes <= 0) {
            throw new IllegalArgumentException("Interval minutes must be greater than zero");
        }

        if (dailyStartTime == null) {
            throw new IllegalArgumentException("Daily start time cannot be null");
        }

        if (dailyEndTime == null) {
            throw new IllegalArgumentException("Daily end time cannot be null");
        }

        if (dailyEndTime.isBefore(dailyStartTime)) {
            throw new IllegalArgumentException("Daily end time cannot be before daily start time");
        }

        this.goal = goal;
        this.intervalMinutes = intervalMinutes;
        this.dailyStartTime = dailyStartTime;
        this.dailyEndTime = dailyEndTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public int getIntervalMinutes() {
        return intervalMinutes;
    }

    public void setIntervalMinutes(int intervalMinutes) {
        this.intervalMinutes = intervalMinutes;
    }

    public OffsetDateTime getDailyStartTime() {
        return dailyStartTime;
    }

    public void setDailyStartTime(OffsetDateTime dailyStartTime) {
        this.dailyStartTime = dailyStartTime;
    }

    public OffsetDateTime getDailyEndTime() {
        return dailyEndTime;
    }

    public void setDailyEndTime(OffsetDateTime dailyEndTime) {
        this.dailyEndTime = dailyEndTime;
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
        if (!(o instanceof AlarmSettings that)) return false;

        return goal == that.goal &&
                intervalMinutes == that.intervalMinutes &&
                Objects.equals(id, that.id) &&
                Objects.equals(dailyStartTime, that.dailyStartTime) &&
                Objects.equals(dailyEndTime, that.dailyEndTime) &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, goal, intervalMinutes, dailyStartTime, dailyEndTime, user);
    }

    @Override
    public String toString() {
        return "AlarmSettings{" +
                "id=" + id +
                ", goal=" + goal +
                ", intervalMinutes=" + intervalMinutes +
                ", dailyStartTime=" + dailyStartTime +
                ", dailyEndTime=" + dailyEndTime +
                ", user=" + (user != null ? user.getId() : "null") +
                '}';
    }
}