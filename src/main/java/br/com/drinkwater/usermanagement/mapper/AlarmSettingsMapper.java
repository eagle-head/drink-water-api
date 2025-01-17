package br.com.drinkwater.usermanagement.mapper;

import br.com.drinkwater.usermanagement.dto.AlarmSettingsDTO;
import br.com.drinkwater.usermanagement.dto.AlarmSettingsResponseDTO;
import br.com.drinkwater.usermanagement.model.AlarmSettings;
import org.springframework.stereotype.Component;

@Component
public class AlarmSettingsMapper {

    public AlarmSettings toEntity(AlarmSettingsDTO dto) {
        if (dto == null) {
            return null;
        }

        AlarmSettings alarmSettings = new AlarmSettings();
        alarmSettings.setGoal(dto.goal());
        alarmSettings.setIntervalMinutes(dto.intervalMinutes());
        alarmSettings.setDailyStartTime(dto.dailyStartTime());
        alarmSettings.setDailyEndTime(dto.dailyEndTime());

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
}