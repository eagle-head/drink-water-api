package br.com.drinkwater.hydrationtracking.service;

import br.com.drinkwater.hydrationtracking.exception.DuplicateDateTimeException;
import br.com.drinkwater.hydrationtracking.exception.WaterIntakeNotFoundException;
import br.com.drinkwater.hydrationtracking.model.WaterIntake;
import br.com.drinkwater.hydrationtracking.repository.WaterIntakeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WaterIntakeService {

    private final WaterIntakeRepository waterIntakeRepository;

    public WaterIntakeService(WaterIntakeRepository waterIntakeRepository) {
        this.waterIntakeRepository = waterIntakeRepository;
    }

    @Transactional
    public WaterIntake save(WaterIntake waterIntake) {
        var exists = this.waterIntakeRepository.existsByDateTimeUTCAndUser_IdAndIdNot(
                waterIntake.getDateTimeUTC(),
                waterIntake.getUser().getId(),
                waterIntake.getId()
        );

        if (exists) {
            throw new DuplicateDateTimeException();
        }

        return this.waterIntakeRepository.save(waterIntake);
    }

    @Transactional(readOnly = true)
    public WaterIntake findByIdAndUserId(Long requestedId, Long userId) {
        return this.waterIntakeRepository.findByIdAndUser_Id(requestedId, userId)
            .orElseThrow(() -> new WaterIntakeNotFoundException("Water intake with ID " + requestedId
                + " not found for the user."));
    }

    // TODO: apply pagination
    @Transactional(readOnly = true)
    public List<WaterIntake> findAllByUserId(Long userId) {
        return this.waterIntakeRepository.findAllByUser_Id(userId);
    }

    @Transactional
    public void deleteByIdAndUserId(Long id, Long userId) {
        this.waterIntakeRepository.deleteByIdAndUser_Id(id, userId);
    }
}
