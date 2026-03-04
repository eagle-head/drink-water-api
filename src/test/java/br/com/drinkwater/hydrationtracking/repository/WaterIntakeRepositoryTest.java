package br.com.drinkwater.hydrationtracking.repository;

import static br.com.drinkwater.hydrationtracking.constants.WaterIntakeRepositoryTestConstants.*;
import static br.com.drinkwater.usermanagement.constants.UserRepositoryTestConstants.createTestUser;
import static org.assertj.core.api.Assertions.assertThat;

import br.com.drinkwater.hydrationtracking.model.WaterIntake;
import br.com.drinkwater.usermanagement.model.User;
import br.com.drinkwater.usermanagement.repository.UserRepository;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class WaterIntakeRepositoryTest {

    @Autowired private WaterIntakeRepository waterIntakeRepository;

    @Autowired private UserRepository userRepository;

    @Test
    void givenValidIdAndUserId_whenFindByIdAndUser_Id_thenReturnWaterIntake() {
        // Given
        User testUser = createTestUser();
        testUser = userRepository.save(testUser);

        WaterIntake testWaterIntake =
                new WaterIntake(
                        REPOSITORY_WATER_INTAKE_DATE_TIME_UTC,
                        REPOSITORY_WATER_INTAKE_VOLUME,
                        REPOSITORY_WATER_INTAKE_VOLUME_UNIT,
                        testUser.getId());
        testWaterIntake = waterIntakeRepository.save(testWaterIntake);

        // When
        Optional<WaterIntake> sut =
                waterIntakeRepository.findByIdAndUserId(testWaterIntake.getId(), testUser.getId());

        // Then
        assertThat(sut).isPresent();
        assertThat(sut.get().getId()).isEqualTo(testWaterIntake.getId());
        assertThat(sut.get().getUserId()).isEqualTo(testUser.getId());
    }

    @Test
    void givenInvalidIdAndValidUserId_whenFindByIdAndUser_Id_thenReturnEmpty() {
        // Given
        User testUser = createTestUser();
        testUser = userRepository.save(testUser);

        WaterIntake testWaterIntake =
                new WaterIntake(
                        REPOSITORY_WATER_INTAKE_DATE_TIME_UTC,
                        REPOSITORY_WATER_INTAKE_VOLUME,
                        REPOSITORY_WATER_INTAKE_VOLUME_UNIT,
                        testUser.getId());
        testWaterIntake = waterIntakeRepository.save(testWaterIntake);

        // When
        Optional<WaterIntake> sut =
                waterIntakeRepository.findByIdAndUserId(
                        testWaterIntake.getId() + 999, testUser.getId());

        // Then
        assertThat(sut).isEmpty();
    }

    @Test
    void givenValidIdAndInvalidUserId_whenFindByIdAndUser_Id_thenReturnEmpty() {
        // Given
        User testUser = createTestUser();
        testUser = userRepository.save(testUser);

        WaterIntake testWaterIntake =
                new WaterIntake(
                        REPOSITORY_WATER_INTAKE_DATE_TIME_UTC,
                        REPOSITORY_WATER_INTAKE_VOLUME,
                        REPOSITORY_WATER_INTAKE_VOLUME_UNIT,
                        testUser.getId());
        testWaterIntake = waterIntakeRepository.save(testWaterIntake);

        // When
        Optional<WaterIntake> sut =
                waterIntakeRepository.findByIdAndUserId(
                        testWaterIntake.getId(), testUser.getId() + 999);

        // Then
        assertThat(sut).isEmpty();
    }

    @Test
    void givenValidIdAndUserId_whenDeleteByIdAndUser_Id_thenSuccess() {
        // Given
        User testUser = createTestUser();
        testUser = userRepository.save(testUser);

        WaterIntake testWaterIntake =
                new WaterIntake(
                        REPOSITORY_WATER_INTAKE_DATE_TIME_UTC,
                        REPOSITORY_WATER_INTAKE_VOLUME,
                        REPOSITORY_WATER_INTAKE_VOLUME_UNIT,
                        testUser.getId());
        testWaterIntake = waterIntakeRepository.save(testWaterIntake);

        Long waterIntakeId = testWaterIntake.getId();
        Long userId = testUser.getId();

        assertThat(waterIntakeRepository.findByIdAndUserId(waterIntakeId, userId)).isPresent();

        // When
        waterIntakeRepository.deleteByIdAndUserId(waterIntakeId, userId);

        // Then
        assertThat(waterIntakeRepository.findByIdAndUserId(waterIntakeId, userId)).isEmpty();
    }

    @Test
    void
            givenValidDateTimeAndUserIdAndDifferentId_whenExistsByDateTimeUTCAndUser_IdAndIdIsNot_thenReturnFalse() {
        // Given
        User testUser = createTestUser();
        testUser = userRepository.save(testUser);

        WaterIntake testWaterIntake =
                new WaterIntake(
                        REPOSITORY_WATER_INTAKE_DATE_TIME_UTC,
                        REPOSITORY_WATER_INTAKE_VOLUME,
                        REPOSITORY_WATER_INTAKE_VOLUME_UNIT,
                        testUser.getId());
        testWaterIntake = waterIntakeRepository.save(testWaterIntake);

        // When
        boolean sut =
                waterIntakeRepository.existsByDateTimeUTCAndUserIdAndIdIsNot(
                        REPOSITORY_WATER_INTAKE_DATE_TIME_UTC,
                        testUser.getId(),
                        testWaterIntake.getId());

        // Then
        assertThat(sut).isFalse();
    }

    @Test
    void
            givenSameDateTimeAndUserIdAndDifferentId_whenExistsByDateTimeUTCAndUser_IdAndIdIsNot_thenReturnTrue() {
        // Given
        User testUser = createTestUser();
        testUser = userRepository.save(testUser);

        // First record
        WaterIntake testWaterIntake =
                new WaterIntake(
                        REPOSITORY_WATER_INTAKE_DATE_TIME_UTC,
                        REPOSITORY_WATER_INTAKE_VOLUME,
                        REPOSITORY_WATER_INTAKE_VOLUME_UNIT,
                        testUser.getId());
        testWaterIntake = waterIntakeRepository.save(testWaterIntake);

        // Second record with different time to avoid uniqueness constraint
        WaterIntake duplicateWaterIntake =
                new WaterIntake(
                        REPOSITORY_WATER_INTAKE_DATE_TIME_UTC.plus(1, ChronoUnit.MINUTES),
                        REPOSITORY_DUPLICATE_WATER_INTAKE_VOLUME,
                        REPOSITORY_WATER_INTAKE_VOLUME_UNIT,
                        testUser.getId());
        duplicateWaterIntake = waterIntakeRepository.save(duplicateWaterIntake);

        // When
        // Note: We can't have two entries with same dateTimeUTC and user_id due to uniqueness
        // constraint
        // We check if the first record exists when querying with its dateTime but excluding the
        // second record's ID
        boolean sut =
                waterIntakeRepository.existsByDateTimeUTCAndUserIdAndIdIsNot(
                        REPOSITORY_WATER_INTAKE_DATE_TIME_UTC,
                        testUser.getId(),
                        duplicateWaterIntake.getId());

        // Then
        assertThat(sut).isTrue();
    }

    @Test
    void givenValidIdAndUserId_whenExistsByIdAndUserId_thenReturnTrue() {
        // Given
        User testUser = createTestUser();
        testUser = userRepository.save(testUser);

        WaterIntake testWaterIntake =
                new WaterIntake(
                        REPOSITORY_WATER_INTAKE_DATE_TIME_UTC,
                        REPOSITORY_WATER_INTAKE_VOLUME,
                        REPOSITORY_WATER_INTAKE_VOLUME_UNIT,
                        testUser.getId());
        testWaterIntake = waterIntakeRepository.save(testWaterIntake);

        // When
        boolean sut =
                waterIntakeRepository.existsByIdAndUserId(
                        testWaterIntake.getId(), testUser.getId());

        // Then
        assertThat(sut).isTrue();
    }

    @Test
    void givenInvalidIdAndValidUserId_whenExistsByIdAndUserId_thenReturnFalse() {
        // Given
        User testUser = createTestUser();
        testUser = userRepository.save(testUser);

        WaterIntake testWaterIntake =
                new WaterIntake(
                        REPOSITORY_WATER_INTAKE_DATE_TIME_UTC,
                        REPOSITORY_WATER_INTAKE_VOLUME,
                        REPOSITORY_WATER_INTAKE_VOLUME_UNIT,
                        testUser.getId());
        waterIntakeRepository.save(testWaterIntake);

        // When
        boolean sut = waterIntakeRepository.existsByIdAndUserId(999999L, testUser.getId());

        // Then
        assertThat(sut).isFalse();
    }
}
