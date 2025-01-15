package br.com.drinkwater.usermanagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;

import java.time.OffsetDateTime;

@Embeddable
public class PersonalData {

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "birth_date", nullable = false)
    private OffsetDateTime birthDate;

    @Convert(converter = BiologicalSexConverter.class)
    @Column(name = "biological_sex", nullable = false)
    private BiologicalSex biologicalSex;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public OffsetDateTime getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(OffsetDateTime birthDate) {
        this.birthDate = birthDate;
    }

    public BiologicalSex getBiologicalSex() {
        return biologicalSex;
    }

    public void setBiologicalSex(BiologicalSex biologicalSex) {
        this.biologicalSex = biologicalSex;
    }
}