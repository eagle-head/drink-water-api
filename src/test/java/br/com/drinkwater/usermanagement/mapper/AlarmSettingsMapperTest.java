package br.com.drinkwater.usermanagement.mapper;

import br.com.drinkwater.usermanagement.exception.AlarmSettingsMapperIllegalArgumentException;
import br.com.drinkwater.usermanagement.model.AlarmSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.drinkwater.usermanagement.constants.AlarmSettingsTestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public final class AlarmSettingsMapperTest {

    private AlarmSettingsMapper mapper;

    @BeforeEach
    public void setUp() {
        this.mapper = new AlarmSettingsMapper();
    }

    @Test
    public void givenAlarmSettingsDTO_whenConvertingToEntity_thenShouldReturnAlarmSettings() {
        var sut = this.mapper.toEntity(ALARM_SETTINGS_DTO);

        assertThat(sut).isNotNull();
        assertThat(sut.getGoal()).isEqualTo(EXPECTED_ALARM_SETTINGS.getGoal());
        assertThat(sut.getIntervalMinutes()).isEqualTo(EXPECTED_ALARM_SETTINGS.getIntervalMinutes());
        assertThat(sut.getDailyStartTime()).isEqualTo(EXPECTED_ALARM_SETTINGS.getDailyStartTime());
        assertThat(sut.getDailyEndTime()).isEqualTo(EXPECTED_ALARM_SETTINGS.getDailyEndTime());
    }

    @Test
    public void givenNullAlarmSettingsDTO_whenConvertingToEntity_thenShouldThrowException() {
        assertThatThrownBy(() -> this.mapper.toEntity(null))
                .isInstanceOf(AlarmSettingsMapperIllegalArgumentException.class)
                .hasMessage("AlarmSettingsDTO cannot be null");
    }

    @Test
    public void givenAlarmSettings_whenConvertingToDTO_thenShouldReturnAlarmSettingsResponseDTO() {
        var sut = this.mapper.toDto(ALARM_SETTINGS);

        assertThat(sut).isNotNull();
        assertThat(sut.goal()).isEqualTo(ALARM_SETTINGS.getGoal());
        assertThat(sut.intervalMinutes()).isEqualTo(ALARM_SETTINGS.getIntervalMinutes());
        assertThat(sut.dailyStartTime()).isEqualTo(ALARM_SETTINGS.getDailyStartTime());
        assertThat(sut.dailyEndTime()).isEqualTo(ALARM_SETTINGS.getDailyEndTime());
    }

    @Test
    public void givenNullAlarmSettings_whenConvertingToDTO_thenShouldThrowException() {
        assertThatThrownBy(() -> this.mapper.toDto(null))
                .isInstanceOf(AlarmSettingsMapperIllegalArgumentException.class)
                .hasMessage("AlarmSettings entity cannot be null");
    }

    @Test
    public void givenValidEntityAndDTO_whenUpdatingEntity_thenShouldUpdateEntityFields() {
        // Arrange
        AlarmSettings entity = new AlarmSettings(
                1000,
                60,
                START_TIME.plusHours(1),
                END_TIME.minusHours(1)
        );

        // Act
        this.mapper.updateEntity(entity, ALARM_SETTINGS_DTO);

        // Assert
        assertThat(entity.getGoal()).isEqualTo(ALARM_SETTINGS_DTO.goal());
        assertThat(entity.getIntervalMinutes()).isEqualTo(ALARM_SETTINGS_DTO.intervalMinutes());
        assertThat(entity.getDailyStartTime()).isEqualTo(ALARM_SETTINGS_DTO.dailyStartTime());
        assertThat(entity.getDailyEndTime()).isEqualTo(ALARM_SETTINGS_DTO.dailyEndTime());
    }

    @Test
    public void givenNullEntity_whenUpdatingEntity_thenShouldThrowException() {
        assertThatThrownBy(() -> this.mapper.updateEntity(null, ALARM_SETTINGS_DTO))
                .isInstanceOf(AlarmSettingsMapperIllegalArgumentException.class)
                .hasMessage("AlarmSettings entity cannot be null");
    }

    @Test
    public void givenNullDTO_whenUpdatingEntity_thenShouldThrowException() {
        assertThatThrownBy(() -> this.mapper.updateEntity(ALARM_SETTINGS, null))
                .isInstanceOf(AlarmSettingsMapperIllegalArgumentException.class)
                .hasMessage("AlarmSettingsDTO cannot be null");
    }
}