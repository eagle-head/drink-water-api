package br.com.drinkwater.usermanagement.mapper;

import br.com.drinkwater.usermanagement.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static br.com.drinkwater.usermanagement.constants.UserTestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

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
    void givenNullUserDTO_whenToEntity_thenShouldReturnNull() {
        var sut = this.mapper.toEntity(null, USER_UUID);

        assertThat(sut).isNull();
    }

    @Test
    void givenValidUserEntity_whenToDto_thenShouldReturnUserResponseDTOWithConvertedSubcomponents() {
        var sut = this.mapper.toDto(USER);

        assertThat(sut).isNotNull().isEqualTo(USER_RESPONSE_DTO);
    }

    @Test
    void givenNullUserEntity_whenToDto_thenShouldReturnNull() {
        var sut = this.mapper.toDto(null);

        assertThat(sut).isNull();
    }

    @Test
    void givenValidUserAndUpdateUserDTO_whenUpdateUserFromDTO_thenAllFieldsShouldBeUpdated() {
        // Arrange
        UserMapper userMapper = new UserMapper(
                new PersonalMapper(),
                new PhysicalMapper(),
                new AlarmSettingsMapper()
        );

        User user = new User();
        user.setEmail("old.email@example.com");

        // Act
        userMapper.updateUserFromDTO(user, UPDATE_USER_DTO);

        // Assert
        assertThat(user.getEmail()).isEqualTo(UPDATE_EMAIL);

        // Verify personal data
        assertThat(user.getPersonal().getFirstName()).isEqualTo("John");
        assertThat(user.getPersonal().getLastName()).isEqualTo("Update");
        assertThat(user.getPersonal().getBirthDate()).isEqualTo(UPDATE_NOW.minusYears(25));
        assertThat(user.getPersonal().getBiologicalSex()).isEqualTo(BiologicalSex.MALE);

        // Verify physical data
        assertThat(user.getPhysical().getWeight()).isEqualTo(BigDecimal.valueOf(70.5));
        assertThat(user.getPhysical().getWeightUnit()).isEqualTo(WeightUnit.KG);
        assertThat(user.getPhysical().getHeight()).isEqualTo(BigDecimal.valueOf(175));
        assertThat(user.getPhysical().getHeightUnit()).isEqualTo(HeightUnit.CM);

        // Verify alarm settings
        assertThat(user.getSettings().getGoal()).isEqualTo(2000);
        assertThat(user.getSettings().getIntervalMinutes()).isEqualTo(30);
        assertThat(user.getSettings().getDailyStartTime()).isEqualTo(UPDATE_NOW.withHour(8).withMinute(0));
        assertThat(user.getSettings().getDailyEndTime()).isEqualTo(UPDATE_NOW.withHour(22).withMinute(0));
        assertThat(user.getSettings().getUser()).isEqualTo(user);
    }

    @Test
    void givenUserDTOWithoutSettings_whenUpdateUserFromDTO_thenSettingsShouldBeNull() {
        // Arrange
        UserMapper userMapper = new UserMapper(
                new PersonalMapper(),
                new PhysicalMapper(),
                new AlarmSettingsMapper()
        );

        User user = new User();
        user.setEmail("old.email@example.com");

        // Set existing settings to ensure they are cleared
        AlarmSettings existingSettings = new AlarmSettings();
        existingSettings.setGoal(500);
        user.setSettings(existingSettings);

        // Act
        userMapper.updateUserFromDTO(user, UPDATE_USER_DTO_WITHOUT_SETTINGS);

        // Assert
        assertThat(user.getEmail()).isEqualTo(UPDATE_EMAIL);
        assertThat(user.getPersonal()).isNotNull();
        assertThat(user.getPhysical()).isNotNull();
        assertThat(user.getSettings()).isNull();
    }

    @Test
    void givenUserWithExistingSettings_whenUpdateUserFromDTO_thenShouldUpdateExistingSettings() {
        // Arrange
        UserMapper userMapper = new UserMapper(
                new PersonalMapper(),
                new PhysicalMapper(),
                new AlarmSettingsMapper()
        );

        User user = new User();
        user.setEmail("old.email@example.com");

        // Set existing alarm settings and capture initial values
        EXISTING_ALARM_SETTINGS.setUser(user);
        user.setSettings(EXISTING_ALARM_SETTINGS);
        int initialGoal = EXISTING_ALARM_SETTINGS.getGoal();
        int initialInterval = EXISTING_ALARM_SETTINGS.getIntervalMinutes();

        // Act
        userMapper.updateUserFromDTO(user, UPDATE_USER_DTO);

        // Assert
        assertThat(user.getSettings())
                .isNotNull()
                .isSameAs(EXISTING_ALARM_SETTINGS);
        assertThat(user.getSettings().getGoal())
                .isEqualTo(2000)
                .isNotEqualTo(initialGoal);
        assertThat(user.getSettings().getIntervalMinutes())
                .isEqualTo(30)
                .isNotEqualTo(initialInterval);
        assertThat(user.getSettings().getDailyStartTime())
                .isEqualTo(UPDATE_NOW.withHour(8).withMinute(0));
        assertThat(user.getSettings().getDailyEndTime())
                .isEqualTo(UPDATE_NOW.withHour(22).withMinute(0));
        assertThat(user.getSettings().getUser()).isSameAs(user);
    }

    @Test
    void givenNullParameters_whenUpdateUserFromDTO_thenShouldReturnEarly() {
        // Arrange
        UserMapper userMapper = new UserMapper(
                new PersonalMapper(),
                new PhysicalMapper(),
                new AlarmSettingsMapper()
        );

        User user = new User();

        // Act & Assert - todos os casos devem retornar sem exceção
        userMapper.updateUserFromDTO(null, null);
        userMapper.updateUserFromDTO(null, UPDATE_USER_DTO);
        userMapper.updateUserFromDTO(user, null);
    }
}
