package br.com.drinkwater.hydrationtracking.repository;

import br.com.drinkwater.core.PageCursor;
import br.com.drinkwater.hydrationtracking.model.WaterIntake;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

/**
 * JDBC-based implementation of {@link WaterIntakeSearchRepository}. Builds dynamic SQL queries with
 * optional WHERE clauses for date range, volume range, and cursor-based keyset pagination. Uses
 * row-tuple comparison {@code (column, id) > (:cursorValue, :cursorId)} for stable cursor ordering.
 */
@Repository
public class WaterIntakeSearchRepositoryImpl implements WaterIntakeSearchRepository {

    private static final Map<String, String> SORT_FIELD_MAPPING =
            Map.of(
                    "id", "id",
                    "dateTimeUTC", "date_time_utc",
                    "volume", "volume",
                    "volumeUnit", "volume_unit");

    private static final Set<String> ALLOWED_SORT_DIRECTIONS = Set.of("ASC", "DESC");

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final RowMapper<WaterIntake> rowMapper =
            (rs, rowNum) ->
                    new WaterIntake(
                            rs.getLong("id"),
                            rs.getTimestamp("date_time_utc").toInstant(),
                            rs.getInt("volume"),
                            rs.getInt("volume_unit"),
                            rs.getLong("user_id"));

    public WaterIntakeSearchRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<WaterIntake> search(
            Long userId,
            @Nullable Instant startDate,
            @Nullable Instant endDate,
            @Nullable Integer minVolume,
            @Nullable Integer maxVolume,
            int limit,
            @Nullable PageCursor cursor,
            String sortField,
            String sortDirection) {

        var params = new MapSqlParameterSource();
        params.addValue("userId", userId);

        var whereClause = new StringBuilder("WHERE user_id = :userId");

        if (startDate != null && endDate != null) {
            whereClause.append(" AND date_time_utc >= :startDate AND date_time_utc <= :endDate");
            params.addValue("startDate", java.sql.Timestamp.from(startDate));
            params.addValue("endDate", java.sql.Timestamp.from(endDate));
        }

        if (minVolume != null) {
            whereClause.append(" AND volume >= :minVolume");
            params.addValue("minVolume", minVolume);
        }

        if (maxVolume != null) {
            whereClause.append(" AND volume <= :maxVolume");
            params.addValue("maxVolume", maxVolume);
        }

        if (cursor != null) {
            appendCursorCondition(whereClause, params, cursor, sortField, sortDirection);
        }

        String column = resolveColumn(sortField);
        String direction = resolveDirection(sortDirection);

        String orderByClause = " ORDER BY " + column + " " + direction + ", id " + direction;

        String dataSql =
                "SELECT * FROM water_intakes " + whereClause + orderByClause + " LIMIT :limit";
        params.addValue("limit", limit);

        return jdbcTemplate.query(dataSql, params, rowMapper);
    }

    private void appendCursorCondition(
            StringBuilder whereClause,
            MapSqlParameterSource params,
            PageCursor cursor,
            String sortField,
            String sortDirection) {

        String column = resolveColumn(sortField);
        String comparator = "DESC".equalsIgnoreCase(sortDirection) ? "<" : ">";

        whereClause
                .append(" AND (")
                .append(column)
                .append(", id) ")
                .append(comparator)
                .append(" (:cursorSortValue, :cursorId)");
        params.addValue("cursorSortValue", java.sql.Timestamp.from(cursor.dateTimeUTC()));
        params.addValue("cursorId", cursor.id());
    }

    private String resolveColumn(String sortField) {
        return SORT_FIELD_MAPPING.getOrDefault(sortField, "date_time_utc");
    }

    private String resolveDirection(String sortDirection) {
        String upper = sortDirection.toUpperCase(Locale.ROOT);
        return ALLOWED_SORT_DIRECTIONS.contains(upper) ? upper : "DESC";
    }
}
