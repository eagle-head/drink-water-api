package br.com.drinkwater.usermanagement.model;

import java.time.LocalDate;
import java.util.Objects;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Column;

/**
 * Immutable embedded value object representing personal information of a user. Stores biological
 * sex as an integer code mapped via {@link BiologicalSex}.
 *
 * <p>The business constructor validates that first name and last name are non-null and non-empty,
 * and that birth date and biological sex are non-null.
 */
public final class Personal {

    @Column("first_name")
    private final String firstName;

    @Column("last_name")
    private final String lastName;

    @Column("birth_date")
    private final LocalDate birthDate;

    @Column("biological_sex")
    private final int biologicalSexCode;

    public Personal(
            String firstName, String lastName, LocalDate birthDate, BiologicalSex biologicalSex) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }

        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }

        Objects.requireNonNull(birthDate, "Birth date cannot be null");
        Objects.requireNonNull(biologicalSex, "Biological sex cannot be null");

        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.biologicalSexCode = biologicalSex.getCode();
    }

    @PersistenceCreator
    public Personal(String firstName, String lastName, LocalDate birthDate, int biologicalSexCode) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.biologicalSexCode = biologicalSexCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public BiologicalSex getBiologicalSex() {
        return BiologicalSex.fromCode(biologicalSexCode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Personal personal)) {
            return false;
        }

        return biologicalSexCode == personal.biologicalSexCode
                && Objects.equals(firstName, personal.firstName)
                && Objects.equals(lastName, personal.lastName)
                && Objects.equals(birthDate, personal.birthDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, birthDate, biologicalSexCode);
    }

    @Override
    public String toString() {
        return "Personal{"
                + "firstName='"
                + firstName
                + '\''
                + ", lastName='"
                + lastName
                + '\''
                + ", birthDate="
                + birthDate
                + ", biologicalSex="
                + getBiologicalSex()
                + '}';
    }
}
