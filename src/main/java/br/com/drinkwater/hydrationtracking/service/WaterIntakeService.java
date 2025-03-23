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
import br.com.drinkwater.hydrationtracking.validation.WaterIntakeFilterValidator;
import br.com.drinkwater.usermanagement.model.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WaterIntakeService {

    private final WaterIntakeRepository waterIntakeRepository;
    private final WaterIntakeMapper waterIntakeMapper;
    private final WaterIntakeFilterValidator filterValidator;

    public WaterIntakeService(WaterIntakeRepository waterIntakeRepository, WaterIntakeMapper waterIntakeMapper,
                              WaterIntakeFilterValidator filterValidator) {
        this.waterIntakeRepository = waterIntakeRepository;
        this.waterIntakeMapper = waterIntakeMapper;
        this.filterValidator = filterValidator;
    }

    @Transactional
    public ResponseWaterIntakeDTO create(WaterIntakeDTO dto, User user) {
        var waterIntake = this.waterIntakeMapper.toEntity(dto, user);
        this.validateDuplicateDateTime(waterIntake);
        var savedWaterIntake = this.waterIntakeRepository.save(waterIntake);

        return this.waterIntakeMapper.toResponseDTO(savedWaterIntake);
    }

    @Transactional
    public ResponseWaterIntakeDTO update(Long waterIntakeId, WaterIntakeDTO dto, User user) {
        this.findByIdAndUserId(waterIntakeId, user.getId());
        var waterIntake = this.waterIntakeMapper.toEntity(dto, user, waterIntakeId);
        this.validateDuplicateDateTime(waterIntake);
        var savedWaterIntake = this.waterIntakeRepository.save(waterIntake);

        return this.waterIntakeMapper.toResponseDTO(savedWaterIntake);
    }

    @Transactional(readOnly = true)
    public ResponseWaterIntakeDTO findByIdAndUserId(Long requestedId, Long userId) {
        return this.waterIntakeRepository.findByIdAndUser_Id(requestedId, userId)
                .map(waterIntakeMapper::toResponseDTO)
                .orElseThrow(() -> new WaterIntakeNotFoundException("Water intake with ID " + requestedId
                        + " not found for the user."));
    }

    @Transactional
    public void deleteByIdAndUserId(Long id, Long userId) {
        this.waterIntakeRepository.deleteByIdAndUser_Id(id, userId);
    }

    @Transactional(readOnly = true)
    public PageResponse<ResponseWaterIntakeDTO> search(WaterIntakeFilterDTO filter, User user) {
        // Validate the filter parameters
        filterValidator.validate(filter);

        // Create page request with sorting
        PageRequest pageRequest = PageRequest.of(
                filter.page(),
                filter.size(),
                Sort.by(Sort.Direction.valueOf(filter.sortDirection()), filter.sortField())
        );

        // Build specification and execute query
        Specification<WaterIntake> spec = WaterIntakeSpecification.buildSpecification(filter, user);
        var page = waterIntakeRepository.findAll(spec, pageRequest);

        // Map to DTOs and return paginated response
        return PageResponse.of(page.map(waterIntakeMapper::toResponseDTO));
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
}