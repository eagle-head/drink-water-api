package br.com.drinkwater.drinkwaterapi.usermanagement.constants;

import br.com.drinkwater.drinkwaterapi.usermanagement.dto.UserResponseDTO;
import br.com.drinkwater.drinkwaterapi.usermanagement.model.User;
import br.com.drinkwater.drinkwaterapi.usermanagement.model.BiologicalSex;
import br.com.drinkwater.drinkwaterapi.usermanagement.model.WeightUnit;
import br.com.drinkwater.drinkwaterapi.usermanagement.model.HeightUnit;

import java.time.OffsetDateTime;

public final class UserConstants {

    public static final User USER = createUser();
    public static final User USER_WITH_SAME_EMAIL = createUser();
    public static final User USER_WITH_EMPTY_EMAIL = createUserWithEmptyEmail();
    public static final User USER_WITH_NULL_EMAIL = createUserWithNullEmail();
    public static final User USER_WITH_INVALID_EMAIL = createUserWithInvalidEmail();
    public static final User USER_WITH_INVALID_DATA = createUserWithInvalidData();
    public static final UserResponseDTO USER_RESPONSE_DTO = createUserResponseDTO();

    private UserConstants() {
    }

    private static UserResponseDTO createUserResponseDTO() {
        return new UserResponseDTO(
                1L,
                "user@example.com",
                "First",
                "Last",
                OffsetDateTime.parse("1989-12-31T00:00:00Z"),
                BiologicalSex.MALE,
                70.0,
                WeightUnit.KG,
                175.0,
                HeightUnit.CM
        );
    }

    private static User createUser() {
        User user = new User();

        user.setEmail("user@example.com");
        user.setPassword("password");
        user.setFirstName("First");
        user.setLastName("Last");
        user.setBirthDate(OffsetDateTime.parse("1989-12-31T00:00:00Z"));
        user.setBiologicalSex(BiologicalSex.MALE);
        user.setWeight(70.0);
        user.setWeightUnit(WeightUnit.KG);
        user.setHeight(175.0);
        user.setHeightUnit(HeightUnit.CM);
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());

        return user;
    }

    private static User createUserWithEmptyEmail() {
        User user = new User();

        user.setEmail("");
        user.setPassword("password");
        user.setFirstName("First");
        user.setLastName("Last");
        user.setBirthDate(OffsetDateTime.parse("1989-12-31T00:00:00Z"));
        user.setBiologicalSex(BiologicalSex.MALE);
        user.setWeight(70.0);
        user.setWeightUnit(WeightUnit.KG);
        user.setHeight(175.0);
        user.setHeightUnit(HeightUnit.CM);
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());

        return user;
    }

    private static User createUserWithNullEmail() {
        User user = new User();

        user.setEmail(null);
        user.setPassword("password");
        user.setFirstName("First");
        user.setLastName("Last");
        user.setBirthDate(OffsetDateTime.parse("1989-12-31T00:00:00Z"));
        user.setBiologicalSex(BiologicalSex.MALE);
        user.setWeight(70.0);
        user.setWeightUnit(WeightUnit.KG);
        user.setHeight(175.0);
        user.setHeightUnit(HeightUnit.CM);
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());

        return user;
    }

    private static User createUserWithInvalidEmail() {
        User user = new User();

        user.setEmail("invalid-email");
        user.setPassword("password");
        user.setFirstName("First");
        user.setLastName("Last");
        user.setBirthDate(OffsetDateTime.parse("1989-12-31T00:00:00Z"));
        user.setBiologicalSex(BiologicalSex.MALE);
        user.setWeight(70.0);
        user.setWeightUnit(WeightUnit.KG);
        user.setHeight(175.0);
        user.setHeightUnit(HeightUnit.CM);
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());

        return user;
    }

    private static User createUserWithInvalidData() {
        User user = new User();

        user.setEmail("");
        user.setPassword("");
        user.setFirstName("");
        user.setLastName("");
        user.setBirthDate(null);
        user.setBiologicalSex(null);
        user.setWeight(0.0);
        user.setWeightUnit(null);
        user.setHeight(0.0);
        user.setHeightUnit(null);
        user.setCreatedAt(null);
        user.setUpdatedAt(null);

        return user;
    }
}
