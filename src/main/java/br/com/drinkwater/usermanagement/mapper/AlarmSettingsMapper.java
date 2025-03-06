package br.com.drinkwater.usermanagement.mapper;

import br.com.drinkwater.usermanagement.dto.AlarmSettingsDTO;
import br.com.drinkwater.usermanagement.dto.AlarmSettingsResponseDTO;
import br.com.drinkwater.usermanagement.exception.AlarmSettingsMapperIllegalArgumentException;
import br.com.drinkwater.usermanagement.model.AlarmSettings;
import org.springframework.stereotype.Component;

@Component
public final class AlarmSettingsMapper {

    public AlarmSettings toEntity(AlarmSettingsDTO dto) {
        if (dto == null) {
            throw new AlarmSettingsMapperIllegalArgumentException("AlarmSettingsDTO cannot be null");
        }

        return new AlarmSettings(
                dto.goal(),
                dto.intervalMinutes(),
                dto.dailyStartTime(),
                dto.dailyEndTime()
        );
    }

    public AlarmSettingsResponseDTO toDto(AlarmSettings entity) {
        if (entity == null) {
            throw new AlarmSettingsMapperIllegalArgumentException("AlarmSettings entity cannot be null");
        }

        return new AlarmSettingsResponseDTO(
                entity.getGoal(),
                entity.getIntervalMinutes(),
                entity.getDailyStartTime(),
                entity.getDailyEndTime()
        );
    }

    public void updateEntity(AlarmSettings entity, AlarmSettingsDTO dto) {
        if (entity == null) {
            throw new AlarmSettingsMapperIllegalArgumentException("AlarmSettings entity cannot be null");
        }

        if (dto == null) {
            throw new AlarmSettingsMapperIllegalArgumentException("AlarmSettingsDTO cannot be null");
        }

        entity.setGoal(dto.goal());
        entity.setIntervalMinutes(dto.intervalMinutes());
        entity.setDailyStartTime(dto.dailyStartTime());
        entity.setDailyEndTime(dto.dailyEndTime());
    }
}