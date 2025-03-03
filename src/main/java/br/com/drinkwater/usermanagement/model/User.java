package br.com.drinkwater.usermanagement.model;

import br.com.drinkwater.hydrationtracking.model.WaterIntake;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
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
