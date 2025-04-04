package br.com.drinkwater.usermanagement.constants;

import br.com.drinkwater.usermanagement.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public final class UserRepositoryTestConstants {

    private UserRepositoryTestConstants() {
    }

    public static final UUID REPOSITORY_USER_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    public static User createTestUser() {
        Personal personal = new Personal(
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                BiologicalSex.MALE
        );

        Physical physical = new Physical(
                BigDecimal.valueOf(70.5),
                WeightUnit.KG,
                BigDecimal.valueOf(175),
                HeightUnit.CM
        );

        AlarmSettings settings = new AlarmSettings(
                2000, // goal
                30,   // intervalMinutes
                LocalTime.of(8, 0, 0),
                LocalTime.of(22, 0, 0)
        );

        return new User(
                REPOSITORY_USER_UUID,
                "john.repository@example.com",
                personal,
                physical,
                settings
        );
    }
}