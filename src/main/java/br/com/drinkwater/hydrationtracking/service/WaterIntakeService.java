package br.com.drinkwater.hydrationtracking.service;

import br.com.drinkwater.core.PageResponse;
import br.com.drinkwater.hydrationtracking.dto.ResponseWaterIntakeDTO;
import br.com.drinkwater.hydrationtracking.dto.WaterIntakeDTO;
import br.com.drinkwater.hydrationtracking.dto.WaterIntakeFilterDTO;
import br.com.drinkwater.hydrationtracking.exception.DuplicateDateTimeException;
import br.com.drinkwater.hydrationtracking.exception.WaterIntakeNotFoundException;
import br.com.drinkwater.hydrationtracking.mapper.WaterIntakeMapper;
import br.com.drinkwater.hydrationtracking.model.WaterIntake;
import br.com.drinkwater.hydrationtracking.repository.WaterIntakeRepository;
import br.com.drinkwater.hydrationtracking.specification.WaterIntakeSpecification;
import br.com.drinkwater.usermanagement.model.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WaterIntakeService {

    private final WaterIntakeRepository waterIntakeRepository;
    private final WaterIntakeMapper waterIntakeMapper;

    public WaterIntakeService(WaterIntakeRepository waterIntakeRepository, WaterIntakeMapper waterIntakeMapper) {
        this.waterIntakeRepository = waterIntakeRepository;
        this.waterIntakeMapper = waterIntakeMapper;
    }

    @Transactional
    public ResponseWaterIntakeDTO create(WaterIntakeDTO dto, User user) {
        var waterIntake = waterIntakeMapper.toEntity(dto, user);
        validateDuplicateDateTime(waterIntake);
        var savedWaterIntake = waterIntakeRepository.save(waterIntake);

        return waterIntakeMapper.toResponseDTO(savedWaterIntake);
    }

    @Transactional
    public ResponseWaterIntakeDTO update(Long id, WaterIntakeDTO dto, User user) {
        findByIdAndUserId(id, user.getId());
        var waterIntake = waterIntakeMapper.toEntity(dto, user);
        waterIntake.setId(id);
        validateDuplicateDateTime(waterIntake);
        var savedWaterIntake = waterIntakeRepository.save(waterIntake);

        return waterIntakeMapper.toResponseDTO(savedWaterIntake);
    }

    @Transactional(readOnly = true)
    public ResponseWaterIntakeDTO findByIdAndUserId(Long requestedId, Long userId) {
        return waterIntakeRepository.findByIdAndUser_Id(requestedId, userId)
                .map(waterIntakeMapper::toResponseDTO)
                .orElseThrow(() -> new WaterIntakeNotFoundException("Water intake with ID " + requestedId
                        + " not found for the user."));
    }

    @Transactional
    public void deleteByIdAndUserId(Long id, Long userId) {
        this.waterIntakeRepository.deleteByIdAndUser_Id(id, userId);
    }

    private void validateDuplicateDateTime(WaterIntake waterIntake) {
        boolean exists = waterIntakeRepository.existsByDateTimeUTCAndUser_IdAndIdIsNot(
                waterIntake.getDateTimeUTC(),
                waterIntake.getUser().getId(),
                waterIntake.getId() == null ? -1L : waterIntake.getId()
        );

        if (exists) {
            throw new DuplicateDateTimeException();
        }
    }

    @Transactional(readOnly = true)
    public PageResponse<ResponseWaterIntakeDTO> search(WaterIntakeFilterDTO filter, User user) {

        var pageable = PageRequest.of(
                filter.page(),
                filter.size(),
                Sort.Direction.valueOf(filter.sortDirection()),
                filter.sortField()
        );

        var specification = WaterIntakeSpecification.withFilters(filter, user.getId());
        var result = waterIntakeRepository.findAll(specification, pageable)
                .map(waterIntakeMapper::toResponseDTO);

        return PageResponse.of(result);
    }
}
