package br.com.drinkwater.hydrationtracking.service;

import br.com.drinkwater.core.CursorPageResponse;
import br.com.drinkwater.core.MessageResolver;
import br.com.drinkwater.core.PageCursor;
import br.com.drinkwater.hydrationtracking.dto.WaterIntakeDTO;
import br.com.drinkwater.hydrationtracking.dto.WaterIntakeFilterDTO;
import br.com.drinkwater.hydrationtracking.dto.WaterIntakeResponseDTO;
import br.com.drinkwater.hydrationtracking.exception.DuplicateDateTimeException;
import br.com.drinkwater.hydrationtracking.exception.WaterIntakeNotFoundException;
import br.com.drinkwater.hydrationtracking.mapper.WaterIntakeMapper;
import br.com.drinkwater.hydrationtracking.model.WaterIntake;
import br.com.drinkwater.hydrationtracking.repository.WaterIntakeRepository;
import br.com.drinkwater.hydrationtracking.repository.WaterIntakeSearchRepository;
import br.com.drinkwater.usermanagement.service.UserService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for water intake record lifecycle and search operations.
 *
 * <p>Manages creation, retrieval, update, deletion, and cursor-based paginated search of water
 * intake records. Resolves the authenticated user's Keycloak public ID to the internal database
 * user ID via {@link UserService#resolveUserIdByPublicId(UUID)}. Publishes Micrometer metrics for
 * creation/deletion counts and search latency.
 */
@Service
public class WaterIntakeService {

    private static final Logger log = LoggerFactory.getLogger(WaterIntakeService.class);

    private final WaterIntakeRepository waterIntakeRepository;
    private final WaterIntakeSearchRepository waterIntakeSearchRepository;
    private final WaterIntakeMapper waterIntakeMapper;
    private final MessageResolver messageResolver;
    private final UserService userService;
    private final Counter waterIntakesCreatedCounter;
    private final Counter waterIntakesDeletedCounter;
    private final Timer waterIntakeSearchTimer;

    public WaterIntakeService(
            WaterIntakeRepository waterIntakeRepository,
            WaterIntakeSearchRepository waterIntakeSearchRepository,
            WaterIntakeMapper waterIntakeMapper,
            MessageResolver messageResolver,
            UserService userService,
            MeterRegistry meterRegistry) {
        this.waterIntakeRepository = waterIntakeRepository;
        this.waterIntakeSearchRepository = waterIntakeSearchRepository;
        this.waterIntakeMapper = waterIntakeMapper;
        this.messageResolver = messageResolver;
        this.userService = userService;
        this.waterIntakesCreatedCounter =
                Counter.builder("water_intakes.created")
                        .description("Total number of water intakes created")
                        .register(meterRegistry);
        this.waterIntakesDeletedCounter =
                Counter.builder("water_intakes.deleted")
                        .description("Total number of water intakes deleted")
                        .register(meterRegistry);
        this.waterIntakeSearchTimer =
                Timer.builder("water_intakes.search")
                        .description("Time spent searching water intakes")
                        .register(meterRegistry);
    }

    /**
     * Creates a new water intake record for the given user.
     *
     * @param dto the validated water intake data
     * @param publicId the Keycloak public ID of the authenticated user
     * @return the created water intake as a response DTO
     * @throws DuplicateDateTimeException if a record with the same date/time already exists for
     *     this user
     */
    @Transactional
    public WaterIntakeResponseDTO create(WaterIntakeDTO dto, UUID publicId) {
        log.info("Creating water intake for user: {}", publicId);
        Long userId = resolveUserId(publicId);
        WaterIntake waterIntake = this.waterIntakeMapper.toEntity(dto, userId);
        this.validateDuplicateDateTime(waterIntake);
        WaterIntake savedWaterIntake = this.waterIntakeRepository.save(waterIntake);
        this.waterIntakesCreatedCounter.increment();
        log.info(
                "Water intake created with id: {} for user: {}",
                savedWaterIntake.getId(),
                publicId);

        return this.waterIntakeMapper.toDto(savedWaterIntake);
    }

    /**
     * Updates an existing water intake record.
     *
     * @param waterIntakeId the ID of the record to update
     * @param dto the validated updated water intake data
     * @param publicId the Keycloak public ID of the authenticated user
     * @return the updated water intake as a response DTO
     * @throws WaterIntakeNotFoundException if no record exists with the given ID for this user
     * @throws DuplicateDateTimeException if the updated date/time conflicts with another record
     */
    @Transactional
    public WaterIntakeResponseDTO update(Long waterIntakeId, WaterIntakeDTO dto, UUID publicId) {
        log.info("Updating water intake id: {} for user: {}", waterIntakeId, publicId);
        Long userId = resolveUserId(publicId);

        if (!waterIntakeRepository.existsByIdAndUserId(waterIntakeId, userId)) {
            log.warn("Water intake not found with id: {} for user: {}", waterIntakeId, publicId);
            throw new WaterIntakeNotFoundException(
                    messageResolver.resolve("water-intake.not-found-for-user", waterIntakeId));
        }

        WaterIntake waterIntake = this.waterIntakeMapper.toEntity(dto, userId, waterIntakeId);
        this.validateDuplicateDateTime(waterIntake);
        WaterIntake savedWaterIntake = this.waterIntakeRepository.save(waterIntake);
        log.info("Water intake updated with id: {} for user: {}", waterIntakeId, publicId);

        return this.waterIntakeMapper.toDto(savedWaterIntake);
    }

    /**
     * Retrieves a single water intake record by its ID for the given user.
     *
     * @param requestedId the water intake record ID
     * @param publicId the Keycloak public ID of the authenticated user
     * @return the water intake as a response DTO
     * @throws WaterIntakeNotFoundException if no record exists with the given ID for this user
     */
    @Transactional(readOnly = true)
    public WaterIntakeResponseDTO findByIdAndUserId(Long requestedId, UUID publicId) {
        log.debug("Fetching water intake id: {} for user: {}", requestedId, publicId);
        Long userId = resolveUserId(publicId);
        return this.waterIntakeRepository
                .findByIdAndUserId(requestedId, userId)
                .map(waterIntakeMapper::toDto)
                .orElseThrow(
                        () -> {
                            log.warn(
                                    "Water intake not found with id: {} for user: {}",
                                    requestedId,
                                    publicId);
                            return new WaterIntakeNotFoundException(
                                    messageResolver.resolve(
                                            "water-intake.not-found-for-user", requestedId));
                        });
    }

    /**
     * Deletes a water intake by its ID and the authenticated user's public ID.
     *
     * <p>This operation is idempotent: if no record matches the given {@code id} and user, the
     * method completes successfully without throwing an exception, and the controller returns 204
     * No Content regardless.
     */
    @Transactional
    public void deleteByIdAndUserId(Long id, UUID publicId) {
        log.info("Deleting water intake id: {} for user: {}", id, publicId);
        Long userId = resolveUserId(publicId);
        this.waterIntakeRepository.deleteByIdAndUserId(id, userId);
        this.waterIntakesDeletedCounter.increment();
        log.info("Water intake deleted with id: {} for user: {}", id, publicId);
    }

    /**
     * Searches water intake records using cursor-based pagination. Fetches one extra record beyond
     * the requested page size to determine if a next page exists, then encodes the last item's
     * date/time and ID as the next cursor.
     *
     * @param filter the search criteria including date range, volume range, sort, and cursor
     * @param publicId the Keycloak public ID of the authenticated user
     * @return a cursor-paginated page of matching water intake records
     */
    @Transactional(readOnly = true)
    public CursorPageResponse<WaterIntakeResponseDTO> search(
            WaterIntakeFilterDTO filter, UUID publicId) {
        log.debug("Searching water intakes for user: {}", publicId);
        Long userId = resolveUserId(publicId);
        PageCursor cursor = PageCursor.decode(filter.cursor());
        int fetchSize = filter.size() + 1;

        var sample = Timer.start();
        List<WaterIntake> results =
                waterIntakeSearchRepository.search(
                        userId,
                        filter.startDate(),
                        filter.endDate(),
                        filter.minVolume(),
                        filter.maxVolume(),
                        fetchSize,
                        cursor,
                        filter.sortField(),
                        filter.sortDirection());
        sample.stop(waterIntakeSearchTimer);

        boolean hasNext = results.size() > filter.size();

        var content = results.stream().limit(filter.size()).map(waterIntakeMapper::toDto).toList();

        String nextCursor = null;
        if (hasNext && !content.isEmpty()) {
            var lastItem = results.get(filter.size() - 1);
            nextCursor =
                    new PageCursor(
                                    lastItem.getDateTimeUTC(),
                                    Objects.requireNonNull(
                                            lastItem.getId(),
                                            "Persisted water intake must have a non-null ID"))
                            .encode();
        }

        log.debug(
                "Search returned {} results (hasNext: {}) for user: {}",
                content.size(),
                hasNext,
                publicId);
        return new CursorPageResponse<>(content, filter.size(), hasNext, nextCursor);
    }

    private Long resolveUserId(UUID publicId) {
        return this.userService.resolveUserIdByPublicId(publicId);
    }

    private void validateDuplicateDateTime(WaterIntake waterIntake) {
        Long id = waterIntake.getId();
        boolean exists;

        if (id == null) {
            exists =
                    waterIntakeRepository.existsByDateTimeUTCAndUserId(
                            waterIntake.getDateTimeUTC(), waterIntake.getUserId());
        } else {
            exists =
                    waterIntakeRepository.existsByDateTimeUTCAndUserIdAndIdIsNot(
                            waterIntake.getDateTimeUTC(), waterIntake.getUserId(), id);
        }

        if (exists) {
            log.warn(
                    "Duplicate dateTime {} for userId: {}",
                    waterIntake.getDateTimeUTC(),
                    waterIntake.getUserId());
            throw new DuplicateDateTimeException(
                    messageResolver.resolve("exception.water-intake.duplicate-datetime"));
        }
    }
}
