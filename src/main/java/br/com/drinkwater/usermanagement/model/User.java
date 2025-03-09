package br.com.drinkwater.usermanagement.model;

import br.com.drinkwater.hydrationtracking.model.WaterIntake;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users", indexes = {@Index(name = "idx_user_public_id", columnList = "public_id")})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", unique = true, nullable = false, updatable = false)
    private UUID publicId;

    @Column(unique = true, nullable = false)
    private String email;

    @Embedded
    private Personal personal;

    @Embedded
    private Physical physical;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private AlarmSettings settings;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WaterIntake> waterIntakes = new HashSet<>();

    /**
     * Protected default constructor required by JPA/Hibernate.
     * This constructor is used by the JPA provider to instantiate the entity.
     */
    protected User() {
        // Default constructor for JPA
    }

    /**
     * Constructor with validations to create a new User instance.
     *
     * @param publicId the unique public identifier for the user (required)
     * @param email    the user's email address (required, must not be null or blank)
     * @param personal the user's personal data (required)
     * @param physical the user's physical data (required)
     * @param settings the user's alarm settings (required)
     * @throws IllegalArgumentException if any parameter is invalid or null
     */
    public User(UUID publicId, String email, Personal personal, Physical physical, AlarmSettings settings) {
        if (publicId == null) {
            throw new IllegalArgumentException("Public ID cannot be null");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }

        if (personal == null) {
            throw new IllegalArgumentException("Personal information is required");
        }

        if (physical == null) {
            throw new IllegalArgumentException("Physical information is required");
        }

        if (settings == null) {
            throw new IllegalArgumentException("Alarm settings are required");
        }

        this.publicId = publicId;
        this.email = email;
        this.personal = personal;
        this.physical = physical;
        this.settings = settings;

        // Ensure bidirectional association
        settings.setUser(this);

        // Initialize waterIntakes collection to avoid NullPointerException
        this.waterIntakes = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public void setPublicId(UUID publicId) {
        this.publicId = publicId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Personal getPersonal() {
        return personal;
    }

    public void setPersonal(Personal personal) {
        this.personal = personal;
    }

    public Physical getPhysical() {
        return physical;
    }

    public void setPhysical(Physical physical) {
        this.physical = physical;
    }

    public AlarmSettings getSettings() {
        return settings;
    }

    public void setSettings(AlarmSettings settings) {
        this.settings = settings;
    }

    public Set<WaterIntake> getWaterIntakes() {
        return waterIntakes;
    }

    public void setWaterIntakes(Set<WaterIntake> waterIntakes) {
        this.waterIntakes = waterIntakes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User that)) return false;

        return Objects.equals(id, that.id) &&
                Objects.equals(publicId, that.publicId) &&
                Objects.equals(email, that.email) &&
                Objects.equals(personal, that.personal) &&
                Objects.equals(physical, that.physical) &&
                Objects.equals(settings, that.settings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, publicId, email, personal, physical, settings);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", publicId='" + publicId + '\'' +
                ", email='" + email + '\'' +
                ", personal=" + personal +
                ", physical=" + physical +
                ", settings=" + (settings != null ? settings.getId() : "null") +
                '}';
    }
}
