package br.com.drinkwater.usermanagement.mapper;

import static br.com.drinkwater.usermanagement.constants.AlarmSettingsTestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.drinkwater.usermanagement.model.AlarmSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

final class AlarmSettingsMapperTest {

    private AlarmSettingsMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AlarmSettingsMapper();
    }

    @Test
    void givenAlarmSettingsDTO_whenConvertingToEntity_thenShouldReturnAlarmSettings() {
        var sut = mapper.toEntity(ALARM_SETTINGS_DTO);

        assertThat(sut).isNotNull();
        assertThat(sut.getGoal()).isEqualTo(EXPECTED_ALARM_SETTINGS.getGoal());
        assertThat(sut.getIntervalMinutes())
                .isEqualTo(EXPECTED_ALARM_SETTINGS.getIntervalMinutes());
        assertThat(sut.getDailyStartTime()).isEqualTo(EXPECTED_ALARM_SETTINGS.getDailyStartTime());
        assertThat(sut.getDailyEndTime()).isEqualTo(EXPECTED_ALARM_SETTINGS.getDailyEndTime());
    }

    @Test
    void givenNullAlarmSettingsDTO_whenConvertingToEntity_thenShouldThrowException() {
        assertThatThrownBy(() -> mapper.toEntity(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("AlarmSettingsDTO cannot be null");
    }

    @Test
    void givenAlarmSettings_whenConvertingToDTO_thenShouldReturnAlarmSettingsResponseDTO() {
        var sut = mapper.toDto(ALARM_SETTINGS);

        assertThat(sut).isNotNull();
        assertThat(sut.goal()).isEqualTo(ALARM_SETTINGS.getGoal());
        assertThat(sut.intervalMinutes()).isEqualTo(ALARM_SETTINGS.getIntervalMinutes());
        assertThat(sut.dailyStartTime()).isEqualTo(ALARM_SETTINGS.getDailyStartTime());
        assertThat(sut.dailyEndTime()).isEqualTo(ALARM_SETTINGS.getDailyEndTime());
    }

    @Test
    void givenNullAlarmSettings_whenConvertingToDTO_thenShouldThrowException() {
        assertThatThrownBy(() -> mapper.toDto(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("AlarmSettings entity cannot be null");
    }

    @Test
    void givenValidEntityAndDTO_whenUpdatingEntity_thenShouldUpdateEntityFields() {
        // Given
        AlarmSettings entity =
                new AlarmSettings(1000, 60, START_TIME.plusHours(1), END_TIME.minusHours(1));

        // When
        AlarmSettings updated = mapper.updateEntity(entity, ALARM_SETTINGS_DTO);

        // Then
        assertThat(updated.getGoal()).isEqualTo(ALARM_SETTINGS_DTO.goal());
        assertThat(updated.getIntervalMinutes()).isEqualTo(ALARM_SETTINGS_DTO.intervalMinutes());
        assertThat(updated.getDailyStartTime()).isEqualTo(ALARM_SETTINGS_DTO.dailyStartTime());
        assertThat(updated.getDailyEndTime()).isEqualTo(ALARM_SETTINGS_DTO.dailyEndTime());
    }

    @Test
    void givenNullEntity_whenUpdatingEntity_thenShouldThrowException() {
        assertThatThrownBy(() -> mapper.updateEntity(null, ALARM_SETTINGS_DTO))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("AlarmSettings entity cannot be null");
    }

    @Test
    void givenNullDTO_whenUpdatingEntity_thenShouldThrowException() {
        assertThatThrownBy(() -> mapper.updateEntity(ALARM_SETTINGS, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("AlarmSettingsDTO cannot be null");
    }
}
