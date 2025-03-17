package br.com.drinkwater.usermanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalTime;
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
    private LocalTime dailyStartTime;

    @Column(name = "daily_end_time", nullable = false)
    private LocalTime dailyEndTime;


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
    public AlarmSettings(int goal, int intervalMinutes, LocalTime dailyStartTime, LocalTime dailyEndTime) {
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

    public LocalTime getDailyStartTime() {
        return dailyStartTime;
    }

    public void setDailyStartTime(LocalTime dailyStartTime) {
        this.dailyStartTime = dailyStartTime;
    }

    public LocalTime getDailyEndTime() {
        return dailyEndTime;
    }

    public void setDailyEndTime(LocalTime dailyEndTime) {
        this.dailyEndTime = dailyEndTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlarmSettings that)) return false;
        if (!Objects.equals(getId(), that.getId())) return false;
        if (!Objects.equals(getGoal(), that.getGoal())) return false;
        if (!Objects.equals(getIntervalMinutes(), that.getIntervalMinutes())) return false;
        if (!Objects.equals(getDailyStartTime(), that.getDailyStartTime())) return false;
        if (!Objects.equals(getDailyEndTime(), that.getDailyEndTime())) return false;

        // User comparison without recursion - using only the publicId
        var thisUser = getUser();
        var thatUser = that.getUser();

        if (thisUser == null && thatUser == null) return true;
        if (thisUser == null || thatUser == null) return false;

        return Objects.equals(thisUser.getPublicId(), thatUser.getPublicId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getId(),
                getGoal(),
                getIntervalMinutes(),
                getDailyStartTime(),
                getDailyEndTime(),
                getUser() != null ? getUser().getPublicId() : null
        );
    }

    @Override
    public String toString() {
        return "AlarmSettings{" +
                "id=" + getId() +
                ", goal=" + getGoal() +
                ", intervalMinutes=" + getIntervalMinutes() +
                ", dailyStartTime=" + getDailyStartTime() +
                ", dailyEndTime=" + getDailyEndTime() +
                ", user=" + (user != null ? user.getId() : "null") +
                '}';
    }
}