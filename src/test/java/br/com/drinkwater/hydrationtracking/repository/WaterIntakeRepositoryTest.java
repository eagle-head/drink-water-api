package br.com.drinkwater.hydrationtracking.repository;

import br.com.drinkwater.hydrationtracking.model.WaterIntake;
import br.com.drinkwater.usermanagement.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static br.com.drinkwater.hydrationtracking.constants.WaterIntakeRepositoryTestConstants.*;
import static br.com.drinkwater.usermanagement.constants.UserRepositoryTestConstants.createTestUser;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.sql.init.mode=never"
})
public class WaterIntakeRepositoryTest {

    @Autowired
    private WaterIntakeRepository waterIntakeRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void givenValidIdAndUserId_whenFindByIdAndUser_Id_thenReturnWaterIntake() {
        // Arrange
        User testUser = createTestUser();
        testUser = this.testEntityManager.persistAndFlush(testUser);

        WaterIntake testWaterIntake = new WaterIntake(
                REPOSITORY_WATER_INTAKE_DATE_TIME_UTC,
                REPOSITORY_WATER_INTAKE_VOLUME,
                REPOSITORY_WATER_INTAKE_VOLUME_UNIT,
                testUser
        );
        testWaterIntake = this.testEntityManager.persistAndFlush(testWaterIntake);
        this.testEntityManager.clear();

        // Act
        Optional<WaterIntake> sut = this.waterIntakeRepository.findByIdAndUser_Id(
                testWaterIntake.getId(),
                testUser.getId());

        // Assert
        assertThat(sut).isPresent();
        assertThat(sut.get().getId()).isEqualTo(testWaterIntake.getId());
        assertThat(sut.get().getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    public void givenInvalidIdAndValidUserId_whenFindByIdAndUser_Id_thenReturnEmpty() {
        // Arrange
        User testUser = createTestUser();
        testUser = this.testEntityManager.persistAndFlush(testUser);

        WaterIntake testWaterIntake = new WaterIntake(
                REPOSITORY_WATER_INTAKE_DATE_TIME_UTC,
                REPOSITORY_WATER_INTAKE_VOLUME,
                REPOSITORY_WATER_INTAKE_VOLUME_UNIT,
                testUser
        );
        testWaterIntake = this.testEntityManager.persistAndFlush(testWaterIntake);
        this.testEntityManager.clear();

        // Act
        Optional<WaterIntake> sut = this.waterIntakeRepository.findByIdAndUser_Id(
                testWaterIntake.getId() + 999,
                testUser.getId());

        // Assert
        assertThat(sut).isEmpty();
    }

    @Test
    public void givenValidIdAndInvalidUserId_whenFindByIdAndUser_Id_thenReturnEmpty() {
        // Arrange
        User testUser = createTestUser();
        testUser = this.testEntityManager.persistAndFlush(testUser);

        WaterIntake testWaterIntake = new WaterIntake(
                REPOSITORY_WATER_INTAKE_DATE_TIME_UTC,
                REPOSITORY_WATER_INTAKE_VOLUME,
                REPOSITORY_WATER_INTAKE_VOLUME_UNIT,
                testUser
        );
        testWaterIntake = this.testEntityManager.persistAndFlush(testWaterIntake);
        this.testEntityManager.clear();

        // Act
        Optional<WaterIntake> sut = this.waterIntakeRepository.findByIdAndUser_Id(
                testWaterIntake.getId(),
                testUser.getId() + 999);

        // Assert
        assertThat(sut).isEmpty();
    }

    @Test
    public void givenValidIdAndUserId_whenDeleteByIdAndUser_Id_thenSuccess() {
        // Arrange
        User testUser = createTestUser();
        testUser = this.testEntityManager.persistAndFlush(testUser);

        WaterIntake testWaterIntake = new WaterIntake(
                REPOSITORY_WATER_INTAKE_DATE_TIME_UTC,
                REPOSITORY_WATER_INTAKE_VOLUME,
                REPOSITORY_WATER_INTAKE_VOLUME_UNIT,
                testUser
        );
        testWaterIntake = this.testEntityManager.persistAndFlush(testWaterIntake);
        this.testEntityManager.clear();

        Long waterIntakeId = testWaterIntake.getId();
        Long userId = testUser.getId();

        assertThat(this.waterIntakeRepository.findByIdAndUser_Id(waterIntakeId, userId)).isPresent();

        // Act
        this.waterIntakeRepository.deleteByIdAndUser_Id(waterIntakeId, userId);
        this.testEntityManager.flush();
        this.testEntityManager.clear();

        // Assert
        assertThat(this.waterIntakeRepository.findByIdAndUser_Id(waterIntakeId, userId)).isEmpty();
    }

    @Test
    public void givenValidDateTimeAndUserIdAndDifferentId_whenExistsByDateTimeUTCAndUser_IdAndIdIsNot_thenReturnFalse() {
        // Arrange
        User testUser = createTestUser();
        testUser = this.testEntityManager.persistAndFlush(testUser);

        WaterIntake testWaterIntake = new WaterIntake(
                REPOSITORY_WATER_INTAKE_DATE_TIME_UTC,
                REPOSITORY_WATER_INTAKE_VOLUME,
                REPOSITORY_WATER_INTAKE_VOLUME_UNIT,
                testUser
        );
        testWaterIntake = this.testEntityManager.persistAndFlush(testWaterIntake);
        this.testEntityManager.clear();

        // Act
        boolean sut = this.waterIntakeRepository.existsByDateTimeUTCAndUser_IdAndIdIsNot(
                REPOSITORY_WATER_INTAKE_DATE_TIME_UTC,
                testUser.getId(),
                testWaterIntake.getId());

        // Assert
        assertThat(sut).isFalse();
    }

    @Test
    public void givenSameDateTimeAndUserIdAndDifferentId_whenExistsByDateTimeUTCAndUser_IdAndIdIsNot_thenReturnTrue() {
        // Arrange
        User testUser = createTestUser();
        testUser = this.testEntityManager.persistAndFlush(testUser);

        // First record
        WaterIntake testWaterIntake = new WaterIntake(
                REPOSITORY_WATER_INTAKE_DATE_TIME_UTC,
                REPOSITORY_WATER_INTAKE_VOLUME,
                REPOSITORY_WATER_INTAKE_VOLUME_UNIT,
                testUser
        );
        testWaterIntake = this.testEntityManager.persistAndFlush(testWaterIntake);

        // Second record with different time to avoid uniqueness constraint
        WaterIntake duplicateWaterIntake = new WaterIntake(
                REPOSITORY_WATER_INTAKE_DATE_TIME_UTC.plusMinutes(1),
                REPOSITORY_DUPLICATE_WATER_INTAKE_VOLUME,
                REPOSITORY_WATER_INTAKE_VOLUME_UNIT,
                testUser
        );
        duplicateWaterIntake = this.testEntityManager.persistAndFlush(duplicateWaterIntake);
        this.testEntityManager.clear();

        // Act
        // Note: We can't have two entries with same dateTimeUTC and user_id due to uniqueness constraint
        // We check if the first record exists when querying with its dateTime but excluding the second record's ID
        boolean sut = this.waterIntakeRepository.existsByDateTimeUTCAndUser_IdAndIdIsNot(
                REPOSITORY_WATER_INTAKE_DATE_TIME_UTC,
                testUser.getId(),
                duplicateWaterIntake.getId());

        // Assert
        assertThat(sut).isTrue();
    }
}