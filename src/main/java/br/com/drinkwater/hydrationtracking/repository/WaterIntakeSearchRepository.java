package br.com.drinkwater.hydrationtracking.repository;

import br.com.drinkwater.core.PageCursor;
import br.com.drinkwater.hydrationtracking.model.WaterIntake;
import java.time.Instant;
import java.util.List;
import org.springframework.lang.Nullable;

/**
 * Custom search repository for water intake records with dynamic filtering and cursor-based
 * pagination. Separated from {@link WaterIntakeRepository} to encapsulate dynamic SQL construction.
 */
public interface WaterIntakeSearchRepository {

    /**
     * Searches water intake records with optional filters and cursor-based pagination.
     *
     * @param userId the internal database user ID
     * @param startDate optional inclusive start of the date range filter
     * @param endDate optional inclusive end of the date range filter
     * @param minVolume optional minimum volume filter
     * @param maxVolume optional maximum volume filter
     * @param limit the maximum number of records to return
     * @param cursor optional cursor for keyset pagination (date/time + ID)
     * @param sortField the field to sort by (e.g. "dateTimeUTC", "volume")
     * @param sortDirection the sort direction ("ASC" or "DESC")
     * @return the matching water intake records
     */
    List<WaterIntake> search(
            Long userId,
            @Nullable Instant startDate,
            @Nullable Instant endDate,
            @Nullable Integer minVolume,
            @Nullable Integer maxVolume,
            int limit,
            @Nullable PageCursor cursor,
            String sortField,
            String sortDirection);
}
