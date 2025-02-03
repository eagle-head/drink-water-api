package br.com.drinkwater.usermanagement.model;

import static br.com.drinkwater.usermanagement.constants.UserTestConstants.USER;
import static org.assertj.core.api.Assertions.assertThat;

import br.com.drinkwater.hydrationtracking.model.WaterIntake;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

public final class UserTest {

    private static final Long ID = 1L;
    private static final UUID PUBLIC_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final String EMAIL = "john.doe@example.com";
    private static final Personal PERSONAL = USER.getPersonal();
    private static final Physical PHYSICAL = USER.getPhysical();
    private static final AlarmSettings SETTINGS = USER.getSettings();
    private static final Set<WaterIntake> WATER_INTAKES = new HashSet<>();

    @ParameterizedTest(name = "Testing setter/getter with value: {2}")
    @MethodSource("provideSetterGetterPairs")
    public <T> void testGetterSetter(final BiConsumer<User, T> setter,
                                     final Function<User, T> getter,
                                     final T expectedValue) {
        var user = new User();
        setter.accept(user, expectedValue);

        assertThat(getter.apply(user)).isEqualTo(expectedValue);
    }

    public static Stream<Arguments> provideSetterGetterPairs() {
        return Stream.of(
                Arguments.of(
                        (BiConsumer<User, Long>) User::setId,
                        (Function<User, Long>) User::getId,
                        ID),
                Arguments.of(
                        (BiConsumer<User, UUID>) User::setPublicId,
                        (Function<User, UUID>) User::getPublicId,
                        PUBLIC_ID),
                Arguments.of(
                        (BiConsumer<User, String>) User::setEmail,
                        (Function<User, String>) User::getEmail,
                        EMAIL),
                Arguments.of(
                        (BiConsumer<User, Personal>) User::setPersonal,
                        (Function<User, Personal>) User::getPersonal,
                        PERSONAL),
                Arguments.of(
                        (BiConsumer<User, Physical>) User::setPhysical,
                        (Function<User, Physical>) User::getPhysical,
                        PHYSICAL),
                Arguments.of(
                        (BiConsumer<User, AlarmSettings>) User::setSettings,
                        (Function<User, AlarmSettings>) User::getSettings,
                        SETTINGS),
                Arguments.of(
                        (BiConsumer<User, Set<WaterIntake>>) User::setWaterIntakes,
                        (Function<User, Set<WaterIntake>>) User::getWaterIntakes,
                        WATER_INTAKES)
        );
    }

    @Test
    public void givenNewUser_whenGetWaterIntakes_thenReturnNonNullSet() {
        var user = new User();

        assertThat(user.getWaterIntakes())
                .isNotNull()
                .isInstanceOf(HashSet.class)
                .isEmpty();
    }

    @Test
    public void givenNullSettings_whenToString_thenShouldHandleNull() {
        var user = new User();
        user.setId(ID);
        user.setSettings(null);
        String toString = user.toString();

        assertThat(toString)
                .contains("settings=null")
                .doesNotContain("settings.getId()");
    }

    @Test
    public void givenSameInstance_whenEquals_thenShouldReturnTrue() {
        var user = new User();
        assertThat(user.equals(user)).isTrue();
    }

    @Test
    public void givenTwoEqualUsers_whenCompared_thenShouldBeEqual() {
        var user1 = createDefaultUser();
        var user2 = createDefaultUser();

        assertThat(user1).isEqualTo(user2);
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }

    @Test
    public void givenNullUser_whenEquals_thenShouldReturnFalse() {
        var user = createDefaultUser();
        assertThat(user.equals(null)).isFalse();
    }

    @Test
    public void givenDifferentObjectType_whenEquals_thenShouldReturnFalse() {
        var user = createDefaultUser();
        String differentType = "Not a User instance";

        assertThat(user.equals(differentType)).isFalse();
    }

    @Test
    public void givenDifferentId_whenEquals_thenShouldReturnFalse() {
        var user1 = createDefaultUser();
        var user2 = createDefaultUser();
        user2.setId(999L);

        assertThat(user1.equals(user2)).isFalse();
    }

    @Test
    public void givenDifferentEmail_whenEquals_thenShouldReturnFalse() {
        var user1 = createDefaultUser();
        var user2 = createDefaultUser();
        user2.setEmail("different@email.com");

        assertThat(user1.equals(user2)).isFalse();
    }

    @Test
    public void givenDifferentPublicId_whenEquals_thenShouldReturnFalse() {
        var user1 = createDefaultUser();
        var user2 = createDefaultUser();
        user2.setPublicId(UUID.randomUUID());

        assertThat(user1.equals(user2)).isFalse();
    }

    @Test
    public void givenUser_whenToString_thenShouldContainAllFields() {
        var user = createDefaultUser();
        var toString = user.toString();

        assertThat(toString)
                .contains(String.valueOf(ID))
                .contains(PUBLIC_ID.toString())
                .contains(EMAIL)
                .contains(PERSONAL.toString())
                .contains(PHYSICAL.toString())
                .contains(String.valueOf(SETTINGS.getId()));
    }

    @Test
    public void givenSettingsWithId_whenToString_thenShouldShowSettingsId() {
        var user = new User();
        var settings = new AlarmSettings();
        settings.setId(123L);
        user.setSettings(settings);

        assertThat(user.toString())
                .contains("settings=123")
                .doesNotContain("settings=null");
    }

    @Test
    public void givenDifferentPersonal_whenEquals_thenShouldReturnFalse() {
        var user1 = createDefaultUser();
        var user2 = createDefaultUser();

        Personal differentPersonal = new Personal();
        differentPersonal.setFirstName("Different");
        user2.setPersonal(differentPersonal);

        assertThat(user1.equals(user2)).isFalse();
    }

    @Test
    public void givenDifferentPhysical_whenEquals_thenShouldReturnFalse() {
        var user1 = createDefaultUser();
        var user2 = createDefaultUser();

        Physical differentPhysical = new Physical();
        differentPhysical.setHeight(USER.getPhysical().getHeight().add(BigDecimal.TEN));
        user2.setPhysical(differentPhysical);

        assertThat(user1.equals(user2)).isFalse();
    }

    @Test
    public void givenDifferentSettings_whenEquals_thenShouldReturnFalse() {
        var user1 = createDefaultUser();
        var user2 = createDefaultUser();

        AlarmSettings differentSettings = new AlarmSettings();
        differentSettings.setId(999L);
        user2.setSettings(differentSettings);

        assertThat(user1.equals(user2)).isFalse();
    }

    @Test
    public void givenDifferentFieldValues_whenHashCode_thenShouldReturnDifferentValues() {
        var user1 = createDefaultUser();
        var user2 = createDefaultUser();
        user2.setEmail("different@email.com");

        assertThat(user1.hashCode()).isNotEqualTo(user2.hashCode());
    }

    private User createDefaultUser() {
        var user = new User();
        user.setId(ID);
        user.setPublicId(PUBLIC_ID);
        user.setEmail(EMAIL);
        user.setPersonal(PERSONAL);
        user.setPhysical(PHYSICAL);
        user.setSettings(SETTINGS);
        user.setWaterIntakes(WATER_INTAKES);
        return user;
    }
}