package br.com.drinkwater.usermanagement.constants.primitive;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class DateTimeTestData {

    private DateTimeTestData() {
    }

    // Fixed dates for testing
    public static final OffsetDateTime DEFAULT_DATE = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    public static final OffsetDateTime PAST_DATE = OffsetDateTime.of(1990, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    public static final OffsetDateTime FUTURE_DATE = OffsetDateTime.of(2025, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC);

    // Time ranges
    public static final OffsetDateTime START_OF_DAY = OffsetDateTime.of(2024, 1, 1, 8, 0, 0, 0, ZoneOffset.UTC);
    public static final OffsetDateTime END_OF_DAY = OffsetDateTime.of(2024, 1, 1, 22, 0, 0, 0, ZoneOffset.UTC);

    // Invalid dates
    public static final OffsetDateTime NULL_DATE = null;
}