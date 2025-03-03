package br.com.drinkwater.usermanagement.constants;

import br.com.drinkwater.usermanagement.model.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public final class RepositoryTestConstants {

    private RepositoryTestConstants() {}

    // UUID fixo para garantir consistência nos testes
    public static final UUID REPOSITORY_USER_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    // Método que cria uma nova instância de User para cada teste
    public static User createTestUser() {
        // Criando objetos necessários
        Personal personal = new Personal(
                "John",
                "Doe",
                OffsetDateTime.of(1990, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
                BiologicalSex.MALE
        );

        Physical physical = new Physical();
        physical.setWeight(BigDecimal.valueOf(70.5));
        physical.setWeightUnit(WeightUnit.KG);
        physical.setHeight(BigDecimal.valueOf(175));
        physical.setHeightUnit(HeightUnit.CM);

        AlarmSettings settings = new AlarmSettings();
        settings.setGoal(2000);
        settings.setIntervalMinutes(30);
        settings.setDailyStartTime(OffsetDateTime.now().withHour(8).withMinute(0).withSecond(0).withNano(0));
        settings.setDailyEndTime(OffsetDateTime.now().withHour(22).withMinute(0).withSecond(0).withNano(0));

        // Criando e configurando o usuário
        User user = new User();
        user.setPublicId(REPOSITORY_USER_UUID);
        user.setEmail("john.repository@example.com");
        user.setPersonal(personal);
        user.setPhysical(physical);

        // Configurando a relação bidirecional
        settings.setUser(user);
        user.setSettings(settings);

        return user;
    }
}