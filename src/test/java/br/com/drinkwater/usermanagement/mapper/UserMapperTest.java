package br.com.drinkwater.usermanagement.mapper;

import br.com.drinkwater.usermanagement.exception.AlarmSettingsMapperIllegalArgumentException;
import br.com.drinkwater.usermanagement.exception.UserMapperIllegalArgumentException;
import br.com.drinkwater.usermanagement.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalTime;

import static br.com.drinkwater.usermanagement.constants.AlarmSettingsTestConstants.ALARM_SETTINGS;
import static br.com.drinkwater.usermanagement.constants.PersonalTestConstants.PERSONAL;
import static br.com.drinkwater.usermanagement.constants.PhysicalTestConstants.PHYSICAL;
import static br.com.drinkwater.usermanagement.constants.UserTestConstants.*;
import static br.com.drinkwater.usermanagement.constants.AlarmSettingsTestConstants.EXISTING_ALARM_SETTINGS_FOR_TEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserMapperTest {

    private UserMapper mapper;

    @BeforeEach
    public void setUp() {
        PersonalMapper personalMapper = new PersonalMapper();
        PhysicalMapper physicalMapper = new PhysicalMapper();
        AlarmSettingsMapper alarmSettingsMapper = new AlarmSettingsMapper();
        this.mapper = new UserMapper(personalMapper, physicalMapper, alarmSettingsMapper);
    }

    @Test
    void givenValidUserDTO_whenToEntity_thenShouldReturnUserWithGeneratedUUIDAndConvertedSubcomponents() {
        var sut = this.mapper.toEntity(USER_DTO, USER_UUID);

        assertThat(sut)
                .usingRecursiveComparison()
                .ignoringFields("waterIntakes", "settings.user")
                .isEqualTo(USER);
    }

    @Test
    void givenNullUserDTO_whenToEntity_thenShouldThrowException() {
        assertThatThrownBy(() -> this.mapper.toEntity(null, USER_UUID))
                .isInstanceOf(UserMapperIllegalArgumentException.class)
                .hasMessage("UserDTO cannot be null");
    }

    @Test
    void givenNullPublicId_whenToEntity_thenShouldThrowException() {
        assertThatThrownBy(() -> this.mapper.toEntity(USER_DTO, null))
                .isInstanceOf(UserMapperIllegalArgumentException.class)
                .hasMessage("Public ID cannot be null");
    }

    @Test
    void givenValidUserEntity_whenToDto_thenShouldReturnUserResponseResponseDTOWithConvertedSubcomponents() {
        var sut = this.mapper.toDto(USER);

        assertThat(sut.publicId()).isEqualTo(USER_RESPONSE_DTO.publicId());
        assertThat(sut.email()).isEqualTo(USER_RESPONSE_DTO.email());
        assertThat(sut.personal()).isEqualTo(USER_RESPONSE_DTO.personal());
        assertThat(sut.physical()).isEqualTo(USER_RESPONSE_DTO.physical());

        assertThat(sut.settings().goal()).isEqualTo(USER_RESPONSE_DTO.settings().goal());
        assertThat(sut.settings().intervalMinutes()).isEqualTo(USER_RESPONSE_DTO.settings().intervalMinutes());
    }

    @Test
    void givenNullUserEntity_whenToDto_thenShouldThrowException() {
        assertThatThrownBy(() -> this.mapper.toDto(null))
                .isInstanceOf(UserMapperIllegalArgumentException.class)
                .hasMessage("Entity cannot be null");
    }

    @Test
    void givenValidUserAndUpdateUserDTO_whenUpdateUser_thenAllFieldsShouldBeUpdated() {
        // Arrange
        User user = new User(USER_UUID, "old.email@example.com", PERSONAL, PHYSICAL, ALARM_SETTINGS);

        // Act
        this.mapper.updateUser(user, UPDATE_USER_DTO);

        // Assert
        assertThat(user.getEmail()).isEqualTo(UPDATE_EMAIL);

        // Verify personal data
        assertThat(user.getPersonal().getFirstName()).isEqualTo("John");
        assertThat(user.getPersonal().getLastName()).isEqualTo("Update");
        assertThat(user.getPersonal().getBirthDate()).isEqualTo(UPDATE_NOW.toLocalDate().minusYears(25));
        assertThat(user.getPersonal().getBiologicalSex()).isEqualTo(BiologicalSex.MALE);

        // Verify physical data
        assertThat(user.getPhysical().getWeight()).isEqualTo(BigDecimal.valueOf(70.5));
        assertThat(user.getPhysical().getWeightUnit()).isEqualTo(WeightUnit.KG);
        assertThat(user.getPhysical().getHeight()).isEqualTo(BigDecimal.valueOf(175));
        assertThat(user.getPhysical().getHeightUnit()).isEqualTo(HeightUnit.CM);

        // Verify alarm settings
        assertThat(user.getSettings().getGoal()).isEqualTo(2000);
        assertThat(user.getSettings().getIntervalMinutes()).isEqualTo(30);

        // Verificar apenas a hora e minuto, não a data completa
        LocalTime startTime = user.getSettings().getDailyStartTime();
        LocalTime endTime = user.getSettings().getDailyEndTime();
        assertThat(startTime.getHour()).isEqualTo(8);
        assertThat(startTime.getMinute()).isEqualTo(0);
        assertThat(endTime.getHour()).isEqualTo(22);
        assertThat(endTime.getMinute()).isEqualTo(0);

        assertThat(user.getSettings().getUser()).isEqualTo(user);
    }

    @Test
    void givenUserDTOWithoutSettings_whenUpdateUser_thenShouldThrowException() {
        // Arrange
        User user = new User(USER_UUID, "old.email@example.com", PERSONAL, PHYSICAL, ALARM_SETTINGS);

        // Set existing settings to ensure they are cleared – using the constant.
        AlarmSettings existingSettings = EXISTING_ALARM_SETTINGS_FOR_TEST;
        existingSettings.setUser(user);
        user.setSettings(existingSettings);

        // Act & Assert - alterado para verificar que uma exceção é lançada
        assertThatThrownBy(() -> this.mapper.updateUser(user, UPDATE_USER_DTO_WITHOUT_SETTINGS))
                .isInstanceOf(AlarmSettingsMapperIllegalArgumentException.class)
                .hasMessage("AlarmSettingsDTO cannot be null");
    }

    @Test
    void givenUserWithExistingSettings_whenUpdateUserFromDTO_thenShouldUpdateExistingSettings() {
        // Arrange
        User user = new User(USER_UUID, "old.email@example.com", PERSONAL, PHYSICAL, ALARM_SETTINGS);

        // Set existing alarm settings and capture initial values.
        EXISTING_ALARM_SETTINGS_FOR_TEST.setUser(user);
        user.setSettings(EXISTING_ALARM_SETTINGS_FOR_TEST);
        int initialGoal = EXISTING_ALARM_SETTINGS_FOR_TEST.getGoal();
        int initialInterval = EXISTING_ALARM_SETTINGS_FOR_TEST.getIntervalMinutes();

        // Act
        this.mapper.updateUser(user, UPDATE_USER_DTO);

        // Assert
        assertThat(user.getSettings())
                .isNotNull()
                .isSameAs(EXISTING_ALARM_SETTINGS_FOR_TEST);
        assertThat(user.getSettings().getGoal())
                .isEqualTo(2000)
                .isNotEqualTo(initialGoal);
        assertThat(user.getSettings().getIntervalMinutes())
                .isEqualTo(30)
                .isNotEqualTo(initialInterval);

        // Verificar apenas a hora e minuto, não a data completa
        LocalTime startTime = user.getSettings().getDailyStartTime();
        LocalTime endTime = user.getSettings().getDailyEndTime();
        assertThat(startTime.getHour()).isEqualTo(8);
        assertThat(startTime.getMinute()).isEqualTo(0);
        assertThat(endTime.getHour()).isEqualTo(22);
        assertThat(endTime.getMinute()).isEqualTo(0);

        assertThat(user.getSettings().getUser()).isSameAs(user);
    }

    @Test
    void givenNullParameters_whenUpdateUser_thenShouldThrowException() {
        // Arrange
        User user = new User(USER_UUID, "old.email@example.com", PERSONAL, PHYSICAL, ALARM_SETTINGS);

        // Act & Assert
        assertThatThrownBy(() -> this.mapper.updateUser(null, null))
                .isInstanceOf(UserMapperIllegalArgumentException.class)
                .hasMessage("User entity cannot be null");

        assertThatThrownBy(() -> this.mapper.updateUser(null, UPDATE_USER_DTO))
                .isInstanceOf(UserMapperIllegalArgumentException.class)
                .hasMessage("User entity cannot be null");

        assertThatThrownBy(() -> this.mapper.updateUser(user, null))
                .isInstanceOf(UserMapperIllegalArgumentException.class)
                .hasMessage("UserDTO cannot be null");
    }
}