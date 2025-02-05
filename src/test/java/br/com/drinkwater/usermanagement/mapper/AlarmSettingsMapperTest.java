package br.com.drinkwater.usermanagement.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.drinkwater.usermanagement.constants.AlarmSettingsTestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public final class AlarmSettingsMapperTest {

    private AlarmSettingsMapper mapper;

    @BeforeEach
    public void setUp() {
        this.mapper = new AlarmSettingsMapper();
    }

    @Test
    public void givenAlarmSettingsDTO_whenConvertingToEntity_thenShouldReturnAlarmSettings() {
        var sut = this.mapper.toEntity(ALARM_SETTINGS_DTO);

        assertThat(sut).isNotNull().isEqualTo(ALARM_SETTINGS);
    }

    @Test
    public void givenNullAlarmSettingsDTO_whenConvertingToEntity_thenShouldReturnNull() {
        var sut = this.mapper.toEntity(null);

        assertThat(sut).isNull();
    }

    @Test
    public void givenAlarmSettings_whenConvertingToDTO_thenShouldReturnAlarmSettingsResponseDTO() {
        var sut = this.mapper.toDto(ALARM_SETTINGS);

        assertThat(sut).isNotNull().isEqualTo(ALARM_SETTINGS_RESPONSE_DTO);
    }

    @Test
    public void givenNullAlarmSettings_whenConvertingToDTO_thenShouldReturnNull() {
        var sut = this.mapper.toDto(null);

        assertThat(sut).isNull();
    }

    @Test
    public void givenNullEntity_whenUpdatingEntity_thenShouldNotThrowException() {
        assertThatCode(() -> this.mapper.updateEntity(null, ALARM_SETTINGS_DTO))
                .doesNotThrowAnyException();
    }

    @Test
    public void givenNullDTO_whenUpdatingEntity_thenShouldNotThrowException() {
        assertThatCode(() -> this.mapper.updateEntity(ALARM_SETTINGS, null))
                .doesNotThrowAnyException();
    }
}