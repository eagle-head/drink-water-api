package br.com.drinkwater.hydrationtracking.service;

import br.com.drinkwater.hydrationtracking.exception.DuplicateDateTimeException;
import br.com.drinkwater.hydrationtracking.exception.WaterIntakeNotFoundException;
import br.com.drinkwater.hydrationtracking.model.VolumeUnit;
import br.com.drinkwater.hydrationtracking.model.WaterIntake;
import br.com.drinkwater.hydrationtracking.repository.WaterIntakeRepository;
import br.com.drinkwater.hydrationtracking.specification.WaterIntakeSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class WaterIntakeService {

    private final WaterIntakeRepository waterIntakeRepository;

    public WaterIntakeService(WaterIntakeRepository waterIntakeRepository) {
        this.waterIntakeRepository = waterIntakeRepository;
    }

    @PreAuthorize("hasAuthority('SCOPE_free:create')")
    @Transactional
    public WaterIntake create(WaterIntake waterIntake) {
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

    @PreAuthorize("hasAuthority('SCOPE_free:update')")
    @Transactional
    public WaterIntake update(WaterIntake waterIntake) {
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

    @PreAuthorize("hasAuthority('SCOPE_free:read')")
    @Transactional(readOnly = true)
    public WaterIntake findByIdAndUserId(Long requestedId, Long userId) {
        return this.waterIntakeRepository.findByIdAndUser_Id(requestedId, userId)
                .orElseThrow(() -> new WaterIntakeNotFoundException("Water intake with ID " + requestedId
                        + " not found for the user."));
    }

    @PreAuthorize("hasAuthority('SCOPE_free:read')")
    @Transactional(readOnly = true)
    public Page<WaterIntake> findAllByUserIdWithFilters(Long userId,
                                                        OffsetDateTime startDate,
                                                        OffsetDateTime endDate,
                                                        Integer minVolume,
                                                        Integer maxVolume,
                                                        VolumeUnit volumeUnit,
                                                        Pageable pageable) {
        Specification<WaterIntake> spec = Specification
                .where(WaterIntakeSpecifications.userIdEqual(userId))
                .and(WaterIntakeSpecifications.dateTimeBetween(startDate, endDate))
                .and(WaterIntakeSpecifications.volumeBetween(minVolume, maxVolume))
                .and(WaterIntakeSpecifications.volumeUnitEqual(volumeUnit));

        return this.waterIntakeRepository.findAll(spec, pageable);
    }

    @PreAuthorize("hasAuthority('SCOPE_free:delete')")
    @Transactional
    public void deleteByIdAndUserId(Long id, Long userId) {
        this.waterIntakeRepository.deleteByIdAndUser_Id(id, userId);
    }
}
