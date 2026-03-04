package br.com.drinkwater.usermanagement.model;

import java.util.Objects;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.Nullable;

/**
 * Immutable aggregate root representing a user in the {@code users} table. Composes {@link
 * Personal}, {@link Physical}, and {@link AlarmSettings} as embedded/mapped values. Equality is
 * based on {@code publicId} (the Keycloak identifier).
 *
 * <p>The business constructor enforces that public ID, email, personal, physical, and settings are
 * all non-null and that email is non-blank.
 */
@Table("users")
public final class User {

    private static final String PUBLIC_ID_REQUIRED = "Public ID cannot be null";
    private static final String EMAIL_REQUIRED = "Email cannot be null or blank";
    private static final String PERSONAL_REQUIRED = "Personal information is required";
    private static final String PHYSICAL_REQUIRED = "Physical information is required";
    private static final String SETTINGS_REQUIRED = "Alarm settings are required";

    @Id
    @Column("id")
    @Nullable
    private final Long id;

    @Column("public_id")
    private final UUID publicId;

    @Column("email")
    private final String email;

    @Embedded.Empty private final Personal personal;

    @Embedded.Empty private final Physical physical;

    @MappedCollection(idColumn = "user_id")
    private final AlarmSettings settings;

    @PersistenceCreator
    public User(
            @Nullable Long id,
            UUID publicId,
            String email,
            Personal personal,
            Physical physical,
            AlarmSettings settings) {
        this.id = id;
        this.publicId = publicId;
        this.email = email;
        this.personal = personal;
        this.physical = physical;
        this.settings = settings;
    }

    public User(
            UUID publicId,
            String email,
            Personal personal,
            Physical physical,
            AlarmSettings settings) {
        Objects.requireNonNull(publicId, PUBLIC_ID_REQUIRED);

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(EMAIL_REQUIRED);
        }

        Objects.requireNonNull(personal, PERSONAL_REQUIRED);
        Objects.requireNonNull(physical, PHYSICAL_REQUIRED);
        Objects.requireNonNull(settings, SETTINGS_REQUIRED);

        this.id = null;
        this.publicId = publicId;
        this.email = email;
        this.personal = personal;
        this.physical = physical;
        this.settings = settings;
    }

    /**
     * Returns a copy of this user with updated email, personal, and physical information while
     * preserving the ID, public ID, and alarm settings.
     *
     * @param email the updated email (must be non-null and non-blank)
     * @param personal the updated personal information
     * @param physical the updated physical information
     * @return a new User instance with the updated fields
     */
    public User withUpdatedFields(String email, Personal personal, Physical physical) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(EMAIL_REQUIRED);
        }

        Objects.requireNonNull(personal, PERSONAL_REQUIRED);
        Objects.requireNonNull(physical, PHYSICAL_REQUIRED);

        return new User(this.id, this.publicId, email, personal, physical, this.settings);
    }

    /**
     * Returns a copy of this user with replaced alarm settings.
     *
     * @param settings the new alarm settings (must not be null)
     * @return a new User instance with the given settings
     */
    public User withSettings(AlarmSettings settings) {
        Objects.requireNonNull(settings, SETTINGS_REQUIRED);
        return new User(this.id, this.publicId, this.email, this.personal, this.physical, settings);
    }

    @Nullable
    public Long getId() {
        return id;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public String getEmail() {
        return email;
    }

    public Personal getPersonal() {
        return Objects.requireNonNull(personal, PERSONAL_REQUIRED);
    }

    public Physical getPhysical() {
        return Objects.requireNonNull(physical, PHYSICAL_REQUIRED);
    }

    public AlarmSettings getSettings() {
        return Objects.requireNonNull(settings, SETTINGS_REQUIRED);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User that)) {
            return false;
        }
        return publicId != null && Objects.equals(publicId, that.publicId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(publicId);
    }

    @Override
    public String toString() {
        return "User{"
                + "id="
                + id
                + ", publicId='"
                + publicId
                + '\''
                + ", email='"
                + email
                + '\''
                + ", personal="
                + personal
                + ", physical="
                + physical
                + ", settings="
                + (settings != null ? settings.getId() : "null")
                + '}';
    }
}
