package br.com.drinkwater.usermanagement.constants.model;

import br.com.drinkwater.usermanagement.constants.model.embedded.PersonalTestData;
import br.com.drinkwater.usermanagement.constants.model.embedded.PhysicalTestData;
import br.com.drinkwater.usermanagement.model.User;

import java.util.UUID;

public final class UserTestData {

    private UserTestData() {
    }

    // Valid data
    public static final User DEFAULT = createDefault();
    public static final User WITH_ALL_RELATIONSHIPS = createWithAllRelationships();

    // Invalid data
    public static final User NULL = null;
    public static final User WITH_NULL_PUBLIC_ID = createWithNullPublicId();
    public static final User WITH_NULL_EMAIL = createWithNullEmail();
    public static final User WITH_NULL_PERSONAL = createWithNullPersonal();
    public static final User WITH_NULL_PHYSICAL = createWithNullPhysical();
    public static final User WITH_INVALID_EMAIL = createWithInvalidEmail();

    private static User createDefault() {
        User user = new User();
        user.setPublicId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
        user.setEmail("john.doe@example.com");
        user.setPersonal(PersonalTestData.DEFAULT);
        user.setPhysical(PhysicalTestData.DEFAULT);
        return user;
    }

    private static User createWithAllRelationships() {
        User user = createDefault();
        user.setSettings(AlarmSettingsTestData.DEFAULT);
        user.getSettings().setUser(user);
        return user;
    }

    private static User createWithNullPublicId() {
        User user = createDefault();
        user.setPublicId(null);
        return user;
    }

    private static User createWithNullEmail() {
        User user = createDefault();
        user.setEmail(null);
        return user;
    }

    private static User createWithNullPersonal() {
        User user = createDefault();
        user.setPersonal(null);
        return user;
    }

    private static User createWithNullPhysical() {
        User user = createDefault();
        user.setPhysical(null);
        return user;
    }

    private static User createWithInvalidEmail() {
        User user = createDefault();
        user.setEmail("invalid-email");
        return user;
    }
}