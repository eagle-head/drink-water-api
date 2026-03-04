package br.com.drinkwater.hydrationtracking.service;

import static br.com.drinkwater.hydrationtracking.constants.WaterIntakeTestConstants.*;
import static br.com.drinkwater.usermanagement.constants.UserTestConstants.USER_ID;
import static br.com.drinkwater.usermanagement.constants.UserTestConstants.USER_UUID;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.com.drinkwater.core.MessageResolver;
import br.com.drinkwater.hydrationtracking.dto.WaterIntakeFilterDTO;
import br.com.drinkwater.hydrationtracking.dto.WaterIntakeResponseDTO;
import br.com.drinkwater.hydrationtracking.exception.DuplicateDateTimeException;
import br.com.drinkwater.hydrationtracking.exception.WaterIntakeNotFoundException;
import br.com.drinkwater.hydrationtracking.mapper.WaterIntakeMapper;
import br.com.drinkwater.hydrationtracking.model.VolumeUnit;
import br.com.drinkwater.hydrationtracking.model.WaterIntake;
import br.com.drinkwater.hydrationtracking.repository.WaterIntakeRepository;
import br.com.drinkwater.hydrationtracking.repository.WaterIntakeSearchRepository;
import br.com.drinkwater.usermanagement.exception.UserNotFoundException;
import br.com.drinkwater.usermanagement.service.UserService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
final class WaterIntakeServiceTest {

    @Mock private WaterIntakeRepository waterIntakeRepository;

    @Mock private WaterIntakeSearchRepository waterIntakeSearchRepository;

    @Mock private WaterIntakeMapper waterIntakeMapper;

    @Mock private MessageResolver messageResolver;

    @Mock private UserService userService;

    private final MeterRegistry meterRegistry = new SimpleMeterRegistry();

    private WaterIntakeService waterIntakeService;

    @BeforeEach
    void setUp() {
        lenient()
                .when(messageResolver.resolve(anyString(), any(Object[].class)))
                .thenReturn("test message");
        lenient().when(userService.resolveUserIdByPublicId(USER_UUID)).thenReturn(USER_ID);
        waterIntakeService =
                new WaterIntakeService(
                        waterIntakeRepository,
                        waterIntakeSearchRepository,
                        waterIntakeMapper,
                        messageResolver,
                        userService,
                        meterRegistry);
    }

    @Test
    void givenValidWaterIntakeDataAndUser_whenCreate_thenReturnsWaterIntakeResponseDTO() {
        // Given
        when(waterIntakeMapper.toEntity(WATER_INTAKE_DTO, USER_ID)).thenReturn(WATER_INTAKE);
        when(waterIntakeRepository.existsByDateTimeUTCAndUserIdAndIdIsNot(
                        WATER_INTAKE.getDateTimeUTC(),
                        WATER_INTAKE.getUserId(),
                        WATER_INTAKE.getId()))
                .thenReturn(false);
        when(waterIntakeRepository.save(WATER_INTAKE)).thenReturn(WATER_INTAKE);
        when(waterIntakeMapper.toDto(WATER_INTAKE)).thenReturn(RESPONSE_WATER_INTAKE_DTO);

        // When
        var sut = waterIntakeService.create(WATER_INTAKE_DTO, USER_UUID);

        // Then
        assertThat(sut).isEqualTo(RESPONSE_WATER_INTAKE_DTO);
        verify(userService, times(1)).resolveUserIdByPublicId(USER_UUID);
        verify(waterIntakeMapper, times(1)).toEntity(WATER_INTAKE_DTO, USER_ID);
        verify(waterIntakeRepository, times(1))
                .existsByDateTimeUTCAndUserIdAndIdIsNot(
                        WATER_INTAKE.getDateTimeUTC(),
                        WATER_INTAKE.getUserId(),
                        WATER_INTAKE.getId());
        verify(waterIntakeRepository, times(1)).save(WATER_INTAKE);
        verify(waterIntakeMapper, times(1)).toDto(WATER_INTAKE);
        verifyNoMoreInteractions(waterIntakeMapper, waterIntakeRepository);
    }

    @Test
    void givenValidIdWaterIntakeDataAndUser_whenUpdate_thenReturnsUpdatedWaterIntakeResponseDTO() {
        // Given
        when(waterIntakeRepository.existsByIdAndUserId(WATER_INTAKE_ID, USER_ID)).thenReturn(true);
        when(waterIntakeMapper.toEntity(WATER_INTAKE_DTO, USER_ID, WATER_INTAKE_ID))
                .thenReturn(WATER_INTAKE);
        when(waterIntakeRepository.existsByDateTimeUTCAndUserIdAndIdIsNot(
                        WATER_INTAKE.getDateTimeUTC(), USER_ID, WATER_INTAKE_ID))
                .thenReturn(false);
        when(waterIntakeRepository.save(WATER_INTAKE)).thenReturn(WATER_INTAKE);
        when(waterIntakeMapper.toDto(WATER_INTAKE)).thenReturn(RESPONSE_WATER_INTAKE_DTO);

        // When
        var sut = waterIntakeService.update(WATER_INTAKE_ID, WATER_INTAKE_DTO, USER_UUID);

        // Then
        assertThat(sut).isEqualTo(RESPONSE_WATER_INTAKE_DTO);
        verify(userService, times(1)).resolveUserIdByPublicId(USER_UUID);
        verify(waterIntakeRepository, times(1)).existsByIdAndUserId(WATER_INTAKE_ID, USER_ID);
        verify(waterIntakeMapper, times(1)).toEntity(WATER_INTAKE_DTO, USER_ID, WATER_INTAKE_ID);
        verify(waterIntakeRepository, times(1))
                .existsByDateTimeUTCAndUserIdAndIdIsNot(
                        WATER_INTAKE.getDateTimeUTC(), USER_ID, WATER_INTAKE_ID);
        verify(waterIntakeRepository, times(1)).save(WATER_INTAKE);
        verify(waterIntakeMapper, times(1)).toDto(WATER_INTAKE);
        verifyNoMoreInteractions(waterIntakeMapper, waterIntakeRepository);
    }

    @Test
    void givenValidIdAndUserId_whenFindByIdAndUserId_thenReturnsWaterIntakeResponseDTO() {
        // Given
        when(waterIntakeRepository.findByIdAndUserId(WATER_INTAKE_ID, USER_ID))
                .thenReturn(Optional.of(WATER_INTAKE));
        when(waterIntakeMapper.toDto(WATER_INTAKE)).thenReturn(RESPONSE_WATER_INTAKE_DTO);

        // When
        var sut = waterIntakeService.findByIdAndUserId(WATER_INTAKE_ID, USER_UUID);

        // Then
        assertThat(sut).isEqualTo(RESPONSE_WATER_INTAKE_DTO);
        verify(userService, times(1)).resolveUserIdByPublicId(USER_UUID);
        verify(waterIntakeRepository, times(1)).findByIdAndUserId(WATER_INTAKE_ID, USER_ID);
        verify(waterIntakeMapper, times(1)).toDto(WATER_INTAKE);
        verifyNoMoreInteractions(waterIntakeMapper, waterIntakeRepository);
    }

    @Test
    void givenValidIdAndUserId_whenDeleteByIdAndUserId_thenRecordShouldBeDeleted() {
        // When & Then
        assertThatCode(() -> waterIntakeService.deleteByIdAndUserId(WATER_INTAKE_ID, USER_UUID))
                .doesNotThrowAnyException();

        verify(userService, times(1)).resolveUserIdByPublicId(USER_UUID);
        verify(waterIntakeRepository, times(1)).deleteByIdAndUserId(WATER_INTAKE_ID, USER_ID);
        verifyNoMoreInteractions(waterIntakeRepository);
    }

    @Test
    void givenValidFilterAndUser_whenSearch_thenReturnsCursorPageResponse() {
        // Given
        when(waterIntakeSearchRepository.search(
                        USER_ID,
                        FILTER_DTO.startDate(),
                        FILTER_DTO.endDate(),
                        FILTER_DTO.minVolume(),
                        FILTER_DTO.maxVolume(),
                        FILTER_DTO.size() + 1,
                        null,
                        FILTER_DTO.sortField(),
                        FILTER_DTO.sortDirection()))
                .thenReturn(List.of(WATER_INTAKE));
        when(waterIntakeMapper.toDto(WATER_INTAKE)).thenReturn(RESPONSE_WATER_INTAKE_DTO);

        // When
        var sut = waterIntakeService.search(FILTER_DTO, USER_UUID);

        // Then
        assertThat(sut).isNotNull();
        assertThat(sut.content()).containsExactly(RESPONSE_WATER_INTAKE_DTO);
        assertThat(sut.hasNext()).isFalse();
        assertThat(sut.nextCursor()).isNull();
        assertThat(sut.pageSize()).isEqualTo(FILTER_DTO.size());

        verify(userService, times(1)).resolveUserIdByPublicId(USER_UUID);
        verify(waterIntakeSearchRepository)
                .search(
                        USER_ID,
                        FILTER_DTO.startDate(),
                        FILTER_DTO.endDate(),
                        FILTER_DTO.minVolume(),
                        FILTER_DTO.maxVolume(),
                        FILTER_DTO.size() + 1,
                        null,
                        FILTER_DTO.sortField(),
                        FILTER_DTO.sortDirection());
        verify(waterIntakeMapper).toDto(WATER_INTAKE);
    }

    @Test
    void givenDuplicateDateTime_whenCreate_thenThrowsDuplicateDateTimeException() {
        // Given
        when(waterIntakeMapper.toEntity(WATER_INTAKE_DTO, USER_ID)).thenReturn(WATER_INTAKE);
        when(waterIntakeRepository.existsByDateTimeUTCAndUserIdAndIdIsNot(
                        WATER_INTAKE.getDateTimeUTC(),
                        WATER_INTAKE.getUserId(),
                        WATER_INTAKE.getId()))
                .thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> waterIntakeService.create(WATER_INTAKE_DTO, USER_UUID))
                .isInstanceOf(DuplicateDateTimeException.class);

        verify(userService, times(1)).resolveUserIdByPublicId(USER_UUID);
        verify(waterIntakeMapper, times(1)).toEntity(WATER_INTAKE_DTO, USER_ID);
        verify(waterIntakeRepository, times(1))
                .existsByDateTimeUTCAndUserIdAndIdIsNot(
                        WATER_INTAKE.getDateTimeUTC(),
                        WATER_INTAKE.getUserId(),
                        WATER_INTAKE.getId());
        verifyNoMoreInteractions(waterIntakeMapper, waterIntakeRepository);
    }

    @Test
    void givenInvalidId_whenUpdate_thenThrowsWaterIntakeNotFoundException() {
        // Given
        when(waterIntakeRepository.existsByIdAndUserId(WATER_INTAKE_ID, USER_ID)).thenReturn(false);

        // When & Then
        assertThatThrownBy(
                        () ->
                                waterIntakeService.update(
                                        WATER_INTAKE_ID, WATER_INTAKE_DTO, USER_UUID))
                .isInstanceOf(WaterIntakeNotFoundException.class);

        verify(userService, times(1)).resolveUserIdByPublicId(USER_UUID);
        verify(waterIntakeRepository, times(1)).existsByIdAndUserId(WATER_INTAKE_ID, USER_ID);
        verifyNoMoreInteractions(waterIntakeMapper, waterIntakeRepository);
    }

    @Test
    void givenDuplicateDateTime_whenUpdate_thenThrowsDuplicateDateTimeException() {
        // Given
        when(waterIntakeRepository.existsByIdAndUserId(WATER_INTAKE_ID, USER_ID)).thenReturn(true);
        when(waterIntakeMapper.toEntity(WATER_INTAKE_DTO, USER_ID, WATER_INTAKE_ID))
                .thenReturn(WATER_INTAKE);
        when(waterIntakeRepository.existsByDateTimeUTCAndUserIdAndIdIsNot(
                        WATER_INTAKE.getDateTimeUTC(), USER_ID, WATER_INTAKE_ID))
                .thenReturn(true);

        // When & Then
        assertThatThrownBy(
                        () ->
                                waterIntakeService.update(
                                        WATER_INTAKE_ID, WATER_INTAKE_DTO, USER_UUID))
                .isInstanceOf(DuplicateDateTimeException.class);

        verify(userService, times(1)).resolveUserIdByPublicId(USER_UUID);
        verify(waterIntakeRepository, times(1)).existsByIdAndUserId(WATER_INTAKE_ID, USER_ID);
        verify(waterIntakeMapper, times(1)).toEntity(WATER_INTAKE_DTO, USER_ID, WATER_INTAKE_ID);
        verify(waterIntakeRepository, times(1))
                .existsByDateTimeUTCAndUserIdAndIdIsNot(
                        WATER_INTAKE.getDateTimeUTC(), USER_ID, WATER_INTAKE_ID);
        verifyNoMoreInteractions(waterIntakeMapper, waterIntakeRepository);
    }

    @Test
    void givenInvalidIdAndUserId_whenFindByIdAndUserId_thenThrowsWaterIntakeNotFoundException() {
        // Given
        when(waterIntakeRepository.findByIdAndUserId(WATER_INTAKE_ID, USER_ID))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> waterIntakeService.findByIdAndUserId(WATER_INTAKE_ID, USER_UUID))
                .isInstanceOf(WaterIntakeNotFoundException.class);

        verify(userService, times(1)).resolveUserIdByPublicId(USER_UUID);
        verify(waterIntakeRepository, times(1)).findByIdAndUserId(WATER_INTAKE_ID, USER_ID);
        verifyNoMoreInteractions(waterIntakeMapper, waterIntakeRepository);
    }

    @Test
    void givenWaterIntakeWithNullId_whenCreate_thenUsesQueryWithoutExcludeId() {
        // Given
        var waterIntakeWithNullId =
                new WaterIntake(
                        WATER_INTAKE.getDateTimeUTC(),
                        WATER_INTAKE.getVolume(),
                        WATER_INTAKE.getVolumeUnit(),
                        USER_ID);

        when(waterIntakeMapper.toEntity(WATER_INTAKE_DTO, USER_ID))
                .thenReturn(waterIntakeWithNullId);
        when(waterIntakeRepository.existsByDateTimeUTCAndUserId(
                        waterIntakeWithNullId.getDateTimeUTC(), USER_ID))
                .thenReturn(false);
        when(waterIntakeRepository.save(waterIntakeWithNullId)).thenReturn(waterIntakeWithNullId);
        when(waterIntakeMapper.toDto(waterIntakeWithNullId)).thenReturn(RESPONSE_WATER_INTAKE_DTO);

        // When
        waterIntakeService.create(WATER_INTAKE_DTO, USER_UUID);

        // Then
        verify(userService, times(1)).resolveUserIdByPublicId(USER_UUID);
        verify(waterIntakeRepository)
                .existsByDateTimeUTCAndUserId(waterIntakeWithNullId.getDateTimeUTC(), USER_ID);
    }

    @Test
    void givenNonExistentUser_whenCreate_thenThrowsUserNotFoundException() {
        // Given
        var unknownUuid = java.util.UUID.randomUUID();
        when(userService.resolveUserIdByPublicId(unknownUuid))
                .thenThrow(new UserNotFoundException());

        // When & Then
        assertThatThrownBy(() -> waterIntakeService.create(WATER_INTAKE_DTO, unknownUuid))
                .isInstanceOf(UserNotFoundException.class);

        verify(userService, times(1)).resolveUserIdByPublicId(unknownUuid);
        verifyNoInteractions(waterIntakeRepository, waterIntakeMapper);
    }

    @Test
    void givenSearchHasNextButContentEmpty_whenSearch_thenNextCursorIsNull() {
        // Given - size=0 so content is empty, but results has 1 item so hasNext=true
        var filter =
                new WaterIntakeFilterDTO(
                        FILTER_DTO.startDate(),
                        FILTER_DTO.endDate(),
                        null,
                        null,
                        null,
                        0,
                        "dateTimeUTC",
                        "DESC");
        when(waterIntakeSearchRepository.search(
                        USER_ID,
                        filter.startDate(),
                        filter.endDate(),
                        filter.minVolume(),
                        filter.maxVolume(),
                        1,
                        null,
                        filter.sortField(),
                        filter.sortDirection()))
                .thenReturn(List.of(WATER_INTAKE));

        // When
        var result = waterIntakeService.search(filter, USER_UUID);

        // Then
        assertThat(result.hasNext()).isTrue();
        assertThat(result.content()).isEmpty();
        assertThat(result.nextCursor()).isNull();
    }

    @Test
    void givenSearchWithMoreResultsThanPageSize_whenSearch_thenReturnsNextCursor() {
        var startDate = java.time.Instant.now().minus(7, java.time.temporal.ChronoUnit.DAYS);
        var endDate = java.time.Instant.now();
        var filter =
                new WaterIntakeFilterDTO(
                        startDate, endDate, null, null, null, 2, "dateTimeUTC", "DESC");
        var waterIntake1 =
                new WaterIntake(
                        1L,
                        java.time.Instant.now().minus(1, java.time.temporal.ChronoUnit.HOURS),
                        100,
                        VolumeUnit.ML,
                        USER_ID);
        var waterIntake2 =
                new WaterIntake(
                        2L,
                        java.time.Instant.now().minus(2, java.time.temporal.ChronoUnit.HOURS),
                        200,
                        VolumeUnit.ML,
                        USER_ID);
        var waterIntake3 =
                new WaterIntake(
                        3L,
                        java.time.Instant.now().minus(3, java.time.temporal.ChronoUnit.HOURS),
                        300,
                        VolumeUnit.ML,
                        USER_ID);
        when(waterIntakeSearchRepository.search(
                        USER_ID,
                        filter.startDate(),
                        filter.endDate(),
                        filter.minVolume(),
                        filter.maxVolume(),
                        3,
                        null,
                        filter.sortField(),
                        filter.sortDirection()))
                .thenReturn(List.of(waterIntake1, waterIntake2, waterIntake3));
        when(waterIntakeMapper.toDto(waterIntake1))
                .thenReturn(
                        new WaterIntakeResponseDTO(
                                1L, waterIntake1.getDateTimeUTC(), 100, VolumeUnit.ML));
        when(waterIntakeMapper.toDto(waterIntake2))
                .thenReturn(
                        new WaterIntakeResponseDTO(
                                2L, waterIntake2.getDateTimeUTC(), 200, VolumeUnit.ML));

        var result = waterIntakeService.search(filter, USER_UUID);

        assertThat(result.content()).hasSize(2);
        assertThat(result.hasNext()).isTrue();
        assertThat(result.nextCursor()).isNotNull();
    }
}
