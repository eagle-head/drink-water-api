package br.com.drinkwater.hydrationtracking.service;

import br.com.drinkwater.hydrationtracking.exception.DuplicateDateTimeException;
import br.com.drinkwater.hydrationtracking.exception.WaterIntakeNotFoundException;
import br.com.drinkwater.hydrationtracking.mapper.WaterIntakeMapper;
import br.com.drinkwater.hydrationtracking.model.WaterIntake;
import br.com.drinkwater.hydrationtracking.repository.WaterIntakeRepository;
import br.com.drinkwater.hydrationtracking.validation.WaterIntakeFilterValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static br.com.drinkwater.hydrationtracking.constants.WaterIntakeTestConstants.*;
import static br.com.drinkwater.usermanagement.constants.UserTestConstants.USER;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public final class WaterIntakeServiceTest {

    @Mock
    private WaterIntakeRepository waterIntakeRepository;

    @Mock
    private WaterIntakeMapper waterIntakeMapper;

    @Mock
    private WaterIntakeFilterValidator filterValidator;

    private WaterIntakeService waterIntakeService;

    @BeforeEach
    public void setUp() {
        this.waterIntakeService = new WaterIntakeService(waterIntakeRepository, waterIntakeMapper, filterValidator);
    }

    @Test
    public void givenValidWaterIntakeDataAndUser_whenCreate_thenReturnsResponseWaterIntakeDTO() {
        when(this.waterIntakeMapper.toEntity(WATER_INTAKE_DTO, USER)).thenReturn(WATER_INTAKE);
        when(this.waterIntakeRepository.existsByDateTimeUTCAndUser_IdAndIdIsNot(
                WATER_INTAKE.getDateTimeUTC(),
                WATER_INTAKE.getUser().getId(),
                WATER_INTAKE.getId() == null ? -1L : WATER_INTAKE.getId())).thenReturn(false);
        when(this.waterIntakeRepository.save(WATER_INTAKE)).thenReturn(WATER_INTAKE);
        when(this.waterIntakeMapper.toResponseDTO(WATER_INTAKE)).thenReturn(RESPONSE_WATER_INTAKE_DTO);

        var sut = this.waterIntakeService.create(WATER_INTAKE_DTO, USER);

        assertThat(sut).isEqualTo(RESPONSE_WATER_INTAKE_DTO);
        verify(this.waterIntakeMapper, times(1)).toEntity(WATER_INTAKE_DTO, USER);
        verify(this.waterIntakeRepository, times(1)).existsByDateTimeUTCAndUser_IdAndIdIsNot(
                WATER_INTAKE.getDateTimeUTC(),
                WATER_INTAKE.getUser().getId(),
                WATER_INTAKE.getId() == null ? -1L : WATER_INTAKE.getId()
        );
        verify(this.waterIntakeRepository, times(1)).save(WATER_INTAKE);
        verify(this.waterIntakeMapper, times(1)).toResponseDTO(WATER_INTAKE);
        verifyNoMoreInteractions(this.waterIntakeMapper, this.waterIntakeRepository);
    }

    @Test
    public void givenValidIdWaterIntakeDataAndUser_whenUpdate_thenReturnsUpdatedResponseWaterIntakeDTO() {
        when(this.waterIntakeRepository.findByIdAndUser_Id(WATER_INTAKE_ID, USER.getId()))
                .thenReturn(Optional.of(WATER_INTAKE));
        when(this.waterIntakeMapper.toResponseDTO(WATER_INTAKE)).thenReturn(RESPONSE_WATER_INTAKE_DTO);
        when(this.waterIntakeMapper.toEntity(WATER_INTAKE_DTO, USER, WATER_INTAKE_ID)).thenReturn(WATER_INTAKE);
        when(this.waterIntakeRepository.existsByDateTimeUTCAndUser_IdAndIdIsNot(
                WATER_INTAKE.getDateTimeUTC(),
                USER.getId(),
                WATER_INTAKE_ID)).thenReturn(false);
        when(this.waterIntakeRepository.save(WATER_INTAKE)).thenReturn(WATER_INTAKE);

        var sut = this.waterIntakeService.update(WATER_INTAKE_ID, WATER_INTAKE_DTO, USER);

        assertThat(sut).isEqualTo(RESPONSE_WATER_INTAKE_DTO);
        verify(this.waterIntakeRepository, times(1)).findByIdAndUser_Id(WATER_INTAKE_ID, USER.getId());
        verify(this.waterIntakeMapper, times(1)).toEntity(WATER_INTAKE_DTO, USER, WATER_INTAKE_ID);
        verify(this.waterIntakeRepository, times(1)).existsByDateTimeUTCAndUser_IdAndIdIsNot(
                WATER_INTAKE.getDateTimeUTC(),
                USER.getId(),
                WATER_INTAKE_ID
        );
        verify(this.waterIntakeRepository, times(1)).save(WATER_INTAKE);
        verify(this.waterIntakeMapper, times(2)).toResponseDTO(WATER_INTAKE);
        verifyNoMoreInteractions(this.waterIntakeMapper, this.waterIntakeRepository);
    }

    @Test
    public void givenValidIdAndUserId_whenFindByIdAndUserId_thenReturnsResponseWaterIntakeDTO() {
        when(this.waterIntakeRepository.findByIdAndUser_Id(WATER_INTAKE_ID, USER.getId()))
                .thenReturn(Optional.of(WATER_INTAKE));
        when(this.waterIntakeMapper.toResponseDTO(WATER_INTAKE)).thenReturn(RESPONSE_WATER_INTAKE_DTO);

        var sut = this.waterIntakeService.findByIdAndUserId(WATER_INTAKE_ID, USER.getId());

        assertThat(sut).isEqualTo(RESPONSE_WATER_INTAKE_DTO);
        verify(this.waterIntakeRepository, times(1)).findByIdAndUser_Id(WATER_INTAKE_ID, USER.getId());
        verify(this.waterIntakeMapper, times(1)).toResponseDTO(WATER_INTAKE);
        verifyNoMoreInteractions(this.waterIntakeMapper, this.waterIntakeRepository);
    }

    @Test
    public void givenValidIdAndUserId_whenDeleteByIdAndUserId_thenRecordShouldBeDeleted() {
        assertThatCode(() -> this.waterIntakeService.deleteByIdAndUserId(WATER_INTAKE_ID, USER.getId()))
                .doesNotThrowAnyException();

        verify(this.waterIntakeRepository, times(1))
                .deleteByIdAndUser_Id(WATER_INTAKE_ID, USER.getId());
        verifyNoMoreInteractions(this.waterIntakeRepository);
    }

    @Test
    public void givenValidFilterAndUser_whenSearch_thenReturnsPageResponseOfResponseWaterIntakeDTO() {
        doNothing().when(this.filterValidator).validate(FILTER_DTO);

        PageRequest pageable = PageRequest.of(
                FILTER_DTO.page(),
                FILTER_DTO.size(),
                Sort.Direction.valueOf(FILTER_DTO.sortDirection()),
                FILTER_DTO.sortField()
        );

        Page<WaterIntake> page = new PageImpl<>(List.of(WATER_INTAKE));

        when(this.waterIntakeRepository.findAll(Mockito.<Specification<WaterIntake>>any(), eq(pageable))).thenReturn(page);
        when(this.waterIntakeMapper.toResponseDTO(WATER_INTAKE)).thenReturn(RESPONSE_WATER_INTAKE_DTO);

        var sut = this.waterIntakeService.search(FILTER_DTO, USER);

        assertThat(sut).isNotNull();
        assertThat(sut.content()).contains(RESPONSE_WATER_INTAKE_DTO);

        // Verifica se o validador foi chamado
        verify(this.filterValidator).validate(FILTER_DTO);
        verify(this.waterIntakeRepository).findAll(Mockito.<Specification<WaterIntake>>any(), eq(pageable));
        verify(this.waterIntakeMapper).toResponseDTO(WATER_INTAKE);
    }

    @Test
    public void givenDuplicateDateTime_whenCreate_thenThrowsDuplicateDateTimeException() {
        when(this.waterIntakeMapper.toEntity(WATER_INTAKE_DTO, USER)).thenReturn(WATER_INTAKE);
        when(this.waterIntakeRepository.existsByDateTimeUTCAndUser_IdAndIdIsNot(
                WATER_INTAKE.getDateTimeUTC(),
                WATER_INTAKE.getUser().getId(),
                WATER_INTAKE.getId() == null ? -1L : WATER_INTAKE.getId())).thenReturn(true);

        assertThatThrownBy(() -> this.waterIntakeService.create(WATER_INTAKE_DTO, USER))
                .isInstanceOf(DuplicateDateTimeException.class);

        verify(this.waterIntakeMapper, times(1)).toEntity(WATER_INTAKE_DTO, USER);
        verify(this.waterIntakeRepository, times(1)).existsByDateTimeUTCAndUser_IdAndIdIsNot(
                WATER_INTAKE.getDateTimeUTC(),
                WATER_INTAKE.getUser().getId(),
                WATER_INTAKE.getId() == null ? -1L : WATER_INTAKE.getId()
        );
        verifyNoMoreInteractions(this.waterIntakeMapper, this.waterIntakeRepository);
    }

    @Test
    public void givenInvalidId_whenUpdate_thenThrowsWaterIntakeNotFoundException() {
        when(this.waterIntakeRepository.findByIdAndUser_Id(WATER_INTAKE_ID, USER.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.waterIntakeService.update(WATER_INTAKE_ID, WATER_INTAKE_DTO, USER))
                .isInstanceOf(WaterIntakeNotFoundException.class)
                .hasMessageContaining("Water intake with ID " + WATER_INTAKE_ID + " not found for the user.");

        verify(this.waterIntakeRepository, times(1)).findByIdAndUser_Id(WATER_INTAKE_ID, USER.getId());
        verifyNoMoreInteractions(this.waterIntakeMapper, this.waterIntakeRepository);
    }

    @Test
    public void givenDuplicateDateTime_whenUpdate_thenThrowsDuplicateDateTimeException() {
        when(this.waterIntakeRepository.findByIdAndUser_Id(WATER_INTAKE_ID, USER.getId()))
                .thenReturn(Optional.of(WATER_INTAKE));
        when(this.waterIntakeMapper.toResponseDTO(WATER_INTAKE)).thenReturn(RESPONSE_WATER_INTAKE_DTO);
        when(this.waterIntakeMapper.toEntity(WATER_INTAKE_DTO, USER, WATER_INTAKE_ID)).thenReturn(WATER_INTAKE);
        when(this.waterIntakeRepository.existsByDateTimeUTCAndUser_IdAndIdIsNot(
                WATER_INTAKE.getDateTimeUTC(),
                USER.getId(),
                WATER_INTAKE_ID)).thenReturn(true);

        assertThatThrownBy(() -> this.waterIntakeService.update(WATER_INTAKE_ID, WATER_INTAKE_DTO, USER))
                .isInstanceOf(DuplicateDateTimeException.class);

        verify(this.waterIntakeRepository, times(1)).findByIdAndUser_Id(WATER_INTAKE_ID, USER.getId());
        verify(this.waterIntakeMapper, times(1)).toResponseDTO(WATER_INTAKE);
        verify(this.waterIntakeMapper, times(1)).toEntity(WATER_INTAKE_DTO, USER, WATER_INTAKE_ID);
        verify(this.waterIntakeRepository, times(1)).existsByDateTimeUTCAndUser_IdAndIdIsNot(
                WATER_INTAKE.getDateTimeUTC(),
                USER.getId(),
                WATER_INTAKE_ID
        );
        verifyNoMoreInteractions(this.waterIntakeMapper, this.waterIntakeRepository);
    }

    @Test
    public void givenInvalidIdAndUserId_whenFindByIdAndUserId_thenThrowsWaterIntakeNotFoundException() {
        when(this.waterIntakeRepository.findByIdAndUser_Id(WATER_INTAKE_ID, USER.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.waterIntakeService.findByIdAndUserId(WATER_INTAKE_ID, USER.getId()))
                .isInstanceOf(WaterIntakeNotFoundException.class)
                .hasMessageContaining("Water intake with ID " + WATER_INTAKE_ID + " not found for the user.");

        verify(this.waterIntakeRepository, times(1)).findByIdAndUser_Id(WATER_INTAKE_ID, USER.getId());
        verifyNoMoreInteractions(this.waterIntakeMapper, this.waterIntakeRepository);
    }

    @Test
    public void givenWaterIntakeWithNullId_whenCreate_thenUsesNegativeOneAsId() {
        var waterIntakeWithNullId = new WaterIntake(
                WATER_INTAKE.getDateTimeUTC(),
                WATER_INTAKE.getVolume(),
                WATER_INTAKE.getVolumeUnit(),
                USER
        );

        when(this.waterIntakeMapper.toEntity(WATER_INTAKE_DTO, USER))
                .thenReturn(waterIntakeWithNullId);
        when(this.waterIntakeRepository.existsByDateTimeUTCAndUser_IdAndIdIsNot(
                waterIntakeWithNullId.getDateTimeUTC(),
                USER.getId(),
                -1L)).thenReturn(false);
        when(this.waterIntakeRepository.save(waterIntakeWithNullId))
                .thenReturn(waterIntakeWithNullId);
        when(this.waterIntakeMapper.toResponseDTO(waterIntakeWithNullId))
                .thenReturn(RESPONSE_WATER_INTAKE_DTO);

        this.waterIntakeService.create(WATER_INTAKE_DTO, USER);

        verify(this.waterIntakeRepository).existsByDateTimeUTCAndUser_IdAndIdIsNot(
                waterIntakeWithNullId.getDateTimeUTC(),
                USER.getId(),
                -1L);
    }
}