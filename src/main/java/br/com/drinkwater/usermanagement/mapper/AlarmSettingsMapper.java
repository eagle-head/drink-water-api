package br.com.drinkwater.usermanagement.mapper;

import br.com.drinkwater.usermanagement.dto.AlarmSettingsDTO;
import br.com.drinkwater.usermanagement.dto.AlarmSettingsResponseDTO;
import br.com.drinkwater.usermanagement.model.AlarmSettings;
import org.springframework.stereotype.Component;

@Component
public final class AlarmSettingsMapper {

    public AlarmSettings toEntity(AlarmSettingsDTO dto) {
        if (dto == null) {
            return null;
        }

        AlarmSettings alarmSettings = new AlarmSettings();
        updateEntity(alarmSettings, dto);
        return alarmSettings;
    }

    public AlarmSettingsResponseDTO toDto(AlarmSettings entity) {
        if (entity == null) {
            return null;
        }

        return new AlarmSettingsResponseDTO(
                entity.getGoal(),
                entity.getIntervalMinutes(),
                entity.getDailyStartTime(),
                entity.getDailyEndTime()
        );
    }

    public void updateEntity(AlarmSettings entity, AlarmSettingsDTO dto) {
        if (entity == null || dto == null) {
            return;
        }
        entity.setGoal(dto.goal());
        entity.setIntervalMinutes(dto.intervalMinutes());
        entity.setDailyStartTime(dto.dailyStartTime());
        entity.setDailyEndTime(dto.dailyEndTime());
    }
}
