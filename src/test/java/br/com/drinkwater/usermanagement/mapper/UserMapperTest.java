package br.com.drinkwater.usermanagement.mapper;

import static br.com.drinkwater.usermanagement.constants.AlarmSettingsTestConstants.ALARM_SETTINGS;
import static br.com.drinkwater.usermanagement.constants.PersonalTestConstants.PERSONAL;
import static br.com.drinkwater.usermanagement.constants.PhysicalTestConstants.PHYSICAL;
import static br.com.drinkwater.usermanagement.constants.UserTestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.drinkwater.usermanagement.model.*;
import java.math.BigDecimal;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

final class UserMapperTest {

    private UserMapper mapper;

    @BeforeEach
    void setUp() {
        PersonalMapper personalMapper = new PersonalMapper();
        PhysicalMapper physicalMapper = new PhysicalMapper();
        AlarmSettingsMapper alarmSettingsMapper = new AlarmSettingsMapper();
        mapper = new UserMapper(personalMapper, physicalMapper, alarmSettingsMapper);
    }

    @Test
    void
            givenValidUserDTO_whenToEntity_thenShouldReturnUserWithGeneratedUUIDAndConvertedSubcomponents() {
        var sut = mapper.toEntity(USER_DTO, USER_UUID);

        assertThat(sut).usingRecursiveComparison().ignoringFields("id").isEqualTo(USER);
    }

    @Test
    void givenNullUserDTO_whenToEntity_thenShouldThrowException() {
        assertThatThrownBy(() -> mapper.toEntity(null, USER_UUID))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("UserDTO cannot be null");
    }

    @Test
    void givenNullPublicId_whenToEntity_thenShouldThrowException() {
        assertThatThrownBy(() -> mapper.toEntity(USER_DTO, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Public ID cannot be null");
    }

    @Test
    void
            givenValidUserEntity_whenToDto_thenShouldReturnUserResponseResponseDTOWithConvertedSubcomponents() {
        var sut = mapper.toDto(USER);

        assertThat(sut.publicId()).isEqualTo(USER_RESPONSE_DTO.publicId());
        assertThat(sut.email()).isEqualTo(USER_RESPONSE_DTO.email());
        assertThat(sut.personal()).isEqualTo(USER_RESPONSE_DTO.personal());
        assertThat(sut.physical()).isEqualTo(USER_RESPONSE_DTO.physical());

        assertThat(sut.settings().goal()).isEqualTo(USER_RESPONSE_DTO.settings().goal());
        assertThat(sut.settings().intervalMinutes())
                .isEqualTo(USER_RESPONSE_DTO.settings().intervalMinutes());
    }

    @Test
    void givenNullUserEntity_whenToDto_thenShouldThrowException() {
        assertThatThrownBy(() -> mapper.toDto(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("User entity cannot be null");
    }

    @Test
    void givenValidUserAndUpdateUserDTO_whenUpdateUser_thenAllFieldsShouldBeUpdated() {
        // Given
        User user =
                new User(USER_UUID, "old.email@example.com", PERSONAL, PHYSICAL, ALARM_SETTINGS);

        // When
        User updatedUser = mapper.updateUser(user, UPDATE_USER_DTO);

        // Then
        assertThat(updatedUser.getEmail()).isEqualTo(UPDATE_EMAIL);

        // Verify personal data
        assertThat(updatedUser.getPersonal().getFirstName()).isEqualTo("John");
        assertThat(updatedUser.getPersonal().getLastName()).isEqualTo("Update");
        assertThat(updatedUser.getPersonal().getBirthDate())
                .isEqualTo(UPDATE_NOW.toLocalDate().minusYears(25));
        assertThat(updatedUser.getPersonal().getBiologicalSex()).isEqualTo(BiologicalSex.MALE);

        // Verify physical data
        assertThat(updatedUser.getPhysical().getWeight()).isEqualTo(BigDecimal.valueOf(70.5));
        assertThat(updatedUser.getPhysical().getWeightUnit()).isEqualTo(WeightUnit.KG);
        assertThat(updatedUser.getPhysical().getHeight()).isEqualTo(BigDecimal.valueOf(175));
        assertThat(updatedUser.getPhysical().getHeightUnit()).isEqualTo(HeightUnit.CM);

        // Verify alarm settings
        assertThat(updatedUser.getSettings().getGoal()).isEqualTo(2000);
        assertThat(updatedUser.getSettings().getIntervalMinutes()).isEqualTo(30);

        // Verificar apenas a hora e minuto, não a data completa
        LocalTime startTime = updatedUser.getSettings().getDailyStartTime();
        LocalTime endTime = updatedUser.getSettings().getDailyEndTime();
        assertThat(startTime.getHour()).isEqualTo(8);
        assertThat(startTime.getMinute()).isEqualTo(0);
        assertThat(endTime.getHour()).isEqualTo(22);
        assertThat(endTime.getMinute()).isEqualTo(0);
    }

    @Test
    void givenUserDTOWithoutSettings_whenUpdateUser_thenShouldThrowException() {
        User user =
                new User(USER_UUID, "old.email@example.com", PERSONAL, PHYSICAL, ALARM_SETTINGS);

        assertThatThrownBy(() -> mapper.updateUser(user, UPDATE_USER_DTO_WITHOUT_SETTINGS))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("AlarmSettingsDTO cannot be null");
    }

    @Test
    void givenUserWithExistingSettings_whenUpdateUserFromDTO_thenShouldUpdateExistingSettings() {
        AlarmSettings existingSettings =
                new AlarmSettings(
                        500,
                        60,
                        ALARM_SETTINGS.getDailyStartTime(),
                        ALARM_SETTINGS.getDailyEndTime());
        User user =
                new User(USER_UUID, "old.email@example.com", PERSONAL, PHYSICAL, existingSettings);

        int initialGoal = existingSettings.getGoal();
        int initialInterval = existingSettings.getIntervalMinutes();

        User updatedUser = mapper.updateUser(user, UPDATE_USER_DTO);

        assertThat(updatedUser.getSettings()).isNotNull();
        assertThat(updatedUser.getSettings().getGoal()).isEqualTo(2000).isNotEqualTo(initialGoal);
        assertThat(updatedUser.getSettings().getIntervalMinutes())
                .isEqualTo(30)
                .isNotEqualTo(initialInterval);

        LocalTime startTime = updatedUser.getSettings().getDailyStartTime();
        LocalTime endTime = updatedUser.getSettings().getDailyEndTime();
        assertThat(startTime.getHour()).isEqualTo(8);
        assertThat(startTime.getMinute()).isEqualTo(0);
        assertThat(endTime.getHour()).isEqualTo(22);
        assertThat(endTime.getMinute()).isEqualTo(0);
    }

    @Test
    void givenNullUserAndNullDto_whenUpdateUser_thenThrowNullPointerException() {
        // When & Then
        assertThatThrownBy(() -> mapper.updateUser(null, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("User entity cannot be null");
    }

    @Test
    void givenNullUser_whenUpdateUser_thenThrowNullPointerException() {
        // When & Then
        assertThatThrownBy(() -> mapper.updateUser(null, UPDATE_USER_DTO))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("User entity cannot be null");
    }

    @Test
    void givenNullDto_whenUpdateUser_thenThrowNullPointerException() {
        // Given
        var user = new User(USER_UUID, "old.email@example.com", PERSONAL, PHYSICAL, ALARM_SETTINGS);

        // When & Then
        assertThatThrownBy(() -> mapper.updateUser(user, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("UserDTO cannot be null");
    }
}
