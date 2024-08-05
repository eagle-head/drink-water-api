package br.com.drinkwater.drinkwaterapi.usermanagement.constants;

import br.com.drinkwater.drinkwaterapi.usermanagement.dto.UserCreateDTO;
import br.com.drinkwater.drinkwaterapi.usermanagement.dto.UserResponseDTO;
import br.com.drinkwater.drinkwaterapi.usermanagement.dto.UserUpdateDTO;
import br.com.drinkwater.drinkwaterapi.usermanagement.model.User;
import br.com.drinkwater.drinkwaterapi.usermanagement.model.BiologicalSex;
import br.com.drinkwater.drinkwaterapi.usermanagement.model.WeightUnit;
import br.com.drinkwater.drinkwaterapi.usermanagement.model.HeightUnit;

import java.time.OffsetDateTime;

public final class UserConstants {

    public static final User USER = createUser();
    public static final User USER_WITH_SAME_EMAIL = createUser();
    public static final User USER_WITH_INVALID_DATA = createUserWithInvalidData();
    public static final UserResponseDTO USER_RESPONSE_DTO = createUserResponseDTO();
    public static final UserCreateDTO USER_CREATE_DTO = createUserCreateDTO();
    public static final UserCreateDTO USER_CREATE_DTO_WITH_EMPTY_EMAIL = createUserCreateDTOWithEmptyEmail();
    public static final UserCreateDTO USER_CREATE_DTO_WITH_NULL_EMAIL = createUserCreateDTOWithNullEmail();
    public static final UserCreateDTO USER_CREATE_DTO_WITH_INVALID_EMAIL = createUserCreateDTOWithInvalidEmail();
    public static final UserCreateDTO USER_CREATE_DTO_WITH_INVALID_DATA = createUserCreateDTOWithInvalidData();
    public static final User UPDATED_USER = createUpdatedUser();
    public static final UserUpdateDTO USER_UPDATE_DTO = createUserUpdateDTO();


    private UserConstants() {
    }

    private static UserCreateDTO createUserCreateDTO() {
        return new UserCreateDTO(
                "user@example.com",
                "password",
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

    private static UserCreateDTO createUserCreateDTOWithEmptyEmail() {
        return new UserCreateDTO(
                "",
                "password",
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

    private static UserCreateDTO createUserCreateDTOWithNullEmail() {
        return new UserCreateDTO(
                null,
                "password",
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

    private static UserCreateDTO createUserCreateDTOWithInvalidEmail() {
        return new UserCreateDTO(
                "invalid-email",
                "password",
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

    private static UserCreateDTO createUserCreateDTOWithInvalidData() {
        return new UserCreateDTO(
                "",
                "",
                "",
                "",
                null,
                null,
                0.0,
                null,
                0.0,
                null
        );
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

    private static User createUpdatedUser() {
        User user = createUser();
        user.setEmail("updated@example.com");
        return user;
    }

    private static UserUpdateDTO createUserUpdateDTO() {
        return new UserUpdateDTO(
                "updated@example.com",
                "password",
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
}
