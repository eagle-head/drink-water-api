package br.com.drinkwater.drinkwaterapi.usermanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "users")
public class User {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Email
    @EqualsAndHashCode.Include
    @Column(unique = true, nullable = false)
    private String email;

    @NotEmpty
    @Column(length = 20, nullable = false)
    private String password;

    @NotEmpty
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotEmpty
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotNull
    @Column(name = "birth_date", nullable = false)
    private OffsetDateTime birthDate;

    @NotNull
    @Convert(converter = BiologicalSexConverter.class)
    @Column(name = "biological_sex", nullable = false)
    private BiologicalSex biologicalSex;

    @NotNull
    @Column(nullable = false)
    private double weight;

    @NotNull
    @Convert(converter = WeightUnitConverter.class)
    @Column(name = "weight_unit", nullable = false)
    private WeightUnit weightUnit;

    @NotNull
    @Column(nullable = false)
    private double height;

    @NotNull
    @Convert(converter = HeightUnitConverter.class)
    @Column(name = "height_unit", nullable = false)
    private HeightUnit heightUnit;

    @JsonIgnore
    @CreationTimestamp
    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @JsonIgnore
    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;
}
