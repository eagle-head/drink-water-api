package br.com.drinkwater.usermanagement.model;

import static br.com.drinkwater.usermanagement.constants.AlarmSettingsTestConstants.ALARM_SETTINGS;
import static br.com.drinkwater.usermanagement.constants.UserTestConstants.USER;
import static br.com.drinkwater.usermanagement.constants.UserTestConstants.USER_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;

final class UserTest {

    private static final UUID PUBLIC_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final String EMAIL = "john.doe@example.com";
    private static final Personal PERSONAL = USER.getPersonal();
    private static final Physical PHYSICAL = USER.getPhysical();

    @Test
    void givenValidParameters_whenCreatingUser_thenAllFieldsAreSet() {
        var user = new User(PUBLIC_ID, EMAIL, PERSONAL, PHYSICAL, ALARM_SETTINGS);

        assertThat(user.getPublicId()).isEqualTo(PUBLIC_ID);
        assertThat(user.getEmail()).isEqualTo(EMAIL);
        assertThat(user.getPersonal()).isEqualTo(PERSONAL);
        assertThat(user.getPhysical()).isEqualTo(PHYSICAL);
        assertThat(user.getSettings()).isEqualTo(ALARM_SETTINGS);
    }

    @Test
    void givenSameInstance_whenEquals_thenShouldReturnTrue() {
        var user = new User(PUBLIC_ID, EMAIL, PERSONAL, PHYSICAL, ALARM_SETTINGS);

        assertThat(user.equals(user)).isTrue();
    }

    @Test
    void givenTwoUsersWithSamePublicId_whenCompared_thenShouldBeEqual() {
        var user1 = new User(PUBLIC_ID, EMAIL, PERSONAL, PHYSICAL, ALARM_SETTINGS);
        var user2 =
                new User(
                        PUBLIC_ID,
                        "other@email.com",
                        PERSONAL,
                        PHYSICAL,
                        new AlarmSettings(
                                500,
                                15,
                                ALARM_SETTINGS.getDailyStartTime(),
                                ALARM_SETTINGS.getDailyEndTime()));

        assertThat(user1).isEqualTo(user2);
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }

    @Test
    void givenNullUser_whenEquals_thenShouldReturnFalse() {
        var user = new User(PUBLIC_ID, EMAIL, PERSONAL, PHYSICAL, ALARM_SETTINGS);

        assertThat(user.equals(null)).isFalse();
    }

    @Test
    void givenDifferentObjectType_whenEquals_thenShouldReturnFalse() {
        var user = new User(PUBLIC_ID, EMAIL, PERSONAL, PHYSICAL, ALARM_SETTINGS);

        assertThat(user).isNotEqualTo("Not a User instance");
    }

    @Test
    void givenDifferentPublicId_whenEquals_thenShouldReturnFalse() {
        var user1 = new User(PUBLIC_ID, EMAIL, PERSONAL, PHYSICAL, ALARM_SETTINGS);
        var user2 =
                new User(
                        UUID.randomUUID(),
                        EMAIL,
                        PERSONAL,
                        PHYSICAL,
                        new AlarmSettings(
                                ALARM_SETTINGS.getGoal(),
                                ALARM_SETTINGS.getIntervalMinutes(),
                                ALARM_SETTINGS.getDailyStartTime(),
                                ALARM_SETTINGS.getDailyEndTime()));

        assertThat(user1).isNotEqualTo(user2);
    }

    @Test
    void givenUsersWithDifferentPublicIds_whenEquals_thenShouldNotBeEqual() {
        var user1 = new User(UUID.randomUUID(), EMAIL, PERSONAL, PHYSICAL, ALARM_SETTINGS);
        var user2 =
                new User(
                        UUID.randomUUID(),
                        EMAIL,
                        PERSONAL,
                        PHYSICAL,
                        new AlarmSettings(
                                ALARM_SETTINGS.getGoal(),
                                ALARM_SETTINGS.getIntervalMinutes(),
                                ALARM_SETTINGS.getDailyStartTime(),
                                ALARM_SETTINGS.getDailyEndTime()));

        assertThat(user1).isNotEqualTo(user2);
    }

    @Test
    void givenUser_whenToString_thenShouldContainAllFields() {
        var user = new User(PUBLIC_ID, EMAIL, PERSONAL, PHYSICAL, ALARM_SETTINGS);

        var toString = user.toString();

        assertThat(toString)
                .contains(PUBLIC_ID.toString())
                .contains(EMAIL)
                .contains(PERSONAL.toString())
                .contains(PHYSICAL.toString());
    }

    @Test
    void givenValidUpdateData_whenWithUpdatedFields_thenFieldsAreUpdated() {
        var user = new User(PUBLIC_ID, EMAIL, PERSONAL, PHYSICAL, ALARM_SETTINGS);
        var newPersonal =
                new Personal("Jane", "Smith", LocalDate.of(1995, 6, 15), BiologicalSex.FEMALE);
        var newPhysical =
                new Physical(
                        BigDecimal.valueOf(60),
                        WeightUnit.KG,
                        BigDecimal.valueOf(165),
                        HeightUnit.CM);

        var updatedUser = user.withUpdatedFields("new@email.com", newPersonal, newPhysical);

        assertThat(updatedUser.getEmail()).isEqualTo("new@email.com");
        assertThat(updatedUser.getPersonal()).isEqualTo(newPersonal);
        assertThat(updatedUser.getPhysical()).isEqualTo(newPhysical);
        assertThat(updatedUser.getPublicId()).isEqualTo(PUBLIC_ID);
        assertThat(updatedUser.getSettings()).isEqualTo(ALARM_SETTINGS);
    }

    @Test
    void givenNullEmail_whenWithUpdatedFields_thenThrowsIllegalArgumentException() {
        var user = new User(PUBLIC_ID, EMAIL, PERSONAL, PHYSICAL, ALARM_SETTINGS);

        assertThatThrownBy(() -> user.withUpdatedFields(null, PERSONAL, PHYSICAL))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email cannot be null or blank");
    }

    @Test
    void givenBlankEmail_whenWithUpdatedFields_thenThrowsIllegalArgumentException() {
        var user = new User(PUBLIC_ID, EMAIL, PERSONAL, PHYSICAL, ALARM_SETTINGS);

        assertThatThrownBy(() -> user.withUpdatedFields("", PERSONAL, PHYSICAL))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email cannot be null or blank");
    }

    @Test
    void givenNullPersonal_whenWithUpdatedFields_thenThrowsNullPointerException() {
        var user = new User(PUBLIC_ID, EMAIL, PERSONAL, PHYSICAL, ALARM_SETTINGS);

        assertThatThrownBy(() -> user.withUpdatedFields(EMAIL, null, PHYSICAL))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Personal information is required");
    }

    @Test
    void givenNullPhysical_whenWithUpdatedFields_thenThrowsNullPointerException() {
        var user = new User(PUBLIC_ID, EMAIL, PERSONAL, PHYSICAL, ALARM_SETTINGS);

        assertThatThrownBy(() -> user.withUpdatedFields(EMAIL, PERSONAL, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Physical information is required");
    }

    @Test
    void givenNullPublicId_whenCreatingUser_thenThrowsNullPointerException() {
        assertThatThrownBy(() -> new User(null, EMAIL, PERSONAL, PHYSICAL, ALARM_SETTINGS))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Public ID cannot be null");
    }

    @Test
    void givenNullEmail_whenCreatingUser_thenThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> new User(USER_UUID, null, PERSONAL, PHYSICAL, ALARM_SETTINGS))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email cannot be null or blank");
    }

    @Test
    void givenBlankEmail_whenCreatingUser_thenThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> new User(USER_UUID, "", PERSONAL, PHYSICAL, ALARM_SETTINGS))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email cannot be null or blank");
    }

    @Test
    void givenNullPersonal_whenCreatingUser_thenThrowsNullPointerException() {
        assertThatThrownBy(() -> new User(USER_UUID, EMAIL, null, PHYSICAL, ALARM_SETTINGS))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Personal information is required");
    }

    @Test
    void givenNullPhysical_whenCreatingUser_thenThrowsNullPointerException() {
        assertThatThrownBy(() -> new User(USER_UUID, EMAIL, PERSONAL, null, ALARM_SETTINGS))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Physical information is required");
    }

    @Test
    void givenNullSettings_whenCreatingUser_thenThrowsNullPointerException() {
        assertThatThrownBy(() -> new User(USER_UUID, EMAIL, PERSONAL, PHYSICAL, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Alarm settings are required");
    }

    @Test
    void givenNullSettings_whenWithSettings_thenThrowsNullPointerException() {
        var user = new User(PUBLIC_ID, EMAIL, PERSONAL, PHYSICAL, ALARM_SETTINGS);

        assertThatThrownBy(() -> user.withSettings(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Alarm settings are required");
    }

    @Test
    void givenUserWithSettings_whenWithSettings_thenReturnsNewUserWithUpdatedSettings() {
        var user = new User(PUBLIC_ID, EMAIL, PERSONAL, PHYSICAL, ALARM_SETTINGS);
        var newSettings =
                new AlarmSettings(
                        3000,
                        45,
                        ALARM_SETTINGS.getDailyStartTime(),
                        ALARM_SETTINGS.getDailyEndTime());

        var updated = user.withSettings(newSettings);

        assertThat(updated.getSettings()).isEqualTo(newSettings);
        assertThat(updated.getPublicId()).isEqualTo(PUBLIC_ID);
    }

    @Test
    void givenUserWithNullPublicId_whenEquals_thenShouldReturnFalse() {
        var user1 = new User(1L, null, EMAIL, PERSONAL, PHYSICAL, ALARM_SETTINGS);
        var user2 = new User(PUBLIC_ID, EMAIL, PERSONAL, PHYSICAL, ALARM_SETTINGS);

        assertThat(user1).isNotEqualTo(user2);
    }

    @Test
    void givenUserCreatedViaPersistenceCreatorWithNullSettings_whenToString_thenContainsNull() {
        var user = new User(1L, PUBLIC_ID, EMAIL, PERSONAL, PHYSICAL, null);

        var str = user.toString();

        assertThat(str).contains("null");
    }
}
