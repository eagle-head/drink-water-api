package br.com.drinkwater.usermanagement.constants;

import br.com.drinkwater.usermanagement.model.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public final class UserConstants {

    private UserConstants() {
        throw new UnsupportedOperationException("UserConstants is a utility class and cannot be instantiated.");
    }

    public static final User JOHN_DOE = createJohnDoe();

    private static User createJohnDoe() {

        var user = new User();
        user.setId(1L);
        user.setPublicId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        user.setEmail("johndoe@test.com");

        // Definition of personal data
        Personal personal = new Personal();
        personal.setFirstName("John");
        personal.setLastName("Doe");
        personal.setBirthDate(OffsetDateTime.parse("1990-01-01T00:00:00Z"));
        personal.setBiologicalSex(BiologicalSex.MALE);
        user.setPersonal(personal);

        // Definition of physical data
        Physical physical = new Physical();
        physical.setWeight(BigDecimal.valueOf(75.0));
        physical.setWeightUnit(WeightUnit.KG);
        physical.setHeight(BigDecimal.valueOf(180));
        physical.setHeightUnit(HeightUnit.CM);
        user.setPhysical(physical);

        return user;
    }
}
