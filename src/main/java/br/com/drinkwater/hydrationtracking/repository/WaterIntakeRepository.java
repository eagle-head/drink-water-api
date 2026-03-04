package br.com.drinkwater.hydrationtracking.repository;

import br.com.drinkwater.hydrationtracking.model.WaterIntake;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JDBC repository for {@link WaterIntake} entities. All queries are scoped to a
 * specific user via their internal database user ID.
 */
@Repository
public interface WaterIntakeRepository
        extends CrudRepository<WaterIntake, Long>, PagingAndSortingRepository<WaterIntake, Long> {

    /**
     * Finds a water intake record by its ID and user ID.
     *
     * @param id the water intake record ID
     * @param userId the internal database user ID
     * @return the water intake if found, or empty
     */
    @Query("SELECT * FROM water_intakes WHERE id = :id AND user_id = :userId")
    Optional<WaterIntake> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * Checks whether a water intake record exists for the given ID and user ID.
     *
     * @param id the water intake record ID
     * @param userId the internal database user ID
     * @return {@code true} if the record exists
     */
    @Query(
            """
            SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END
            FROM water_intakes
            WHERE id = :id AND user_id = :userId
            """)
    boolean existsByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * Deletes a water intake record by its ID and user ID. This is a no-op if no record exists.
     *
     * @param id the water intake record ID
     * @param userId the internal database user ID
     */
    @Modifying
    @Query("DELETE FROM water_intakes WHERE id = :id AND user_id = :userId")
    void deleteByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * Checks whether a water intake record with the given date/time exists for the user. Used for
     * duplicate detection during creation.
     *
     * @param dateTimeUTC the UTC date/time to check
     * @param userId the internal database user ID
     * @return {@code true} if a record with the same date/time exists
     */
    @Query(
            """
            SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END
            FROM water_intakes
            WHERE date_time_utc = :dateTimeUTC AND user_id = :userId
            """)
    boolean existsByDateTimeUTCAndUserId(
            @Param("dateTimeUTC") Instant dateTimeUTC, @Param("userId") Long userId);

    /**
     * Checks whether a water intake record with the given date/time exists for the user, excluding
     * a specific record. Used for duplicate detection during updates.
     *
     * @param dateTimeUTC the UTC date/time to check
     * @param userId the internal database user ID
     * @param excludeId the record ID to exclude from the check
     * @return {@code true} if another record with the same date/time exists
     */
    @Query(
            """
            SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END
            FROM water_intakes
            WHERE date_time_utc = :dateTimeUTC AND user_id = :userId AND id != :excludeId
            """)
    boolean existsByDateTimeUTCAndUserIdAndIdIsNot(
            @Param("dateTimeUTC") Instant dateTimeUTC,
            @Param("userId") Long userId,
            @Param("excludeId") Long excludeId);
}
