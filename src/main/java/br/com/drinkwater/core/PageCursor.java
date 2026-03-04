package br.com.drinkwater.core;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import org.springframework.lang.Nullable;

/**
 * Keyset pagination cursor that encodes a date/time and record ID pair. The cursor is serialized as
 * a Base64 URL-safe string in the format {@code <ISO-8601 instant>|<id>} for use in query
 * parameters.
 *
 * @param dateTimeUTC the date/time of the last record in the previous page
 * @param id the database ID of the last record in the previous page (must be positive)
 */
public record PageCursor(Instant dateTimeUTC, Long id) {

    private static final String SEPARATOR = "|";

    public PageCursor {
        if (dateTimeUTC == null) {
            throw new IllegalArgumentException("Cursor dateTimeUTC cannot be null");
        }
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Cursor id must be a positive number");
        }
    }

    /**
     * Encodes this cursor as a Base64 URL-safe string without padding.
     *
     * @return the encoded cursor string
     */
    public String encode() {
        var raw = dateTimeUTC.toString() + SEPARATOR + id;
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decodes a cursor string back into a PageCursor instance.
     *
     * @param cursor the Base64-encoded cursor string, or null/blank
     * @return the decoded PageCursor, or null if the input is null or blank
     * @throws IllegalArgumentException if the cursor format is invalid
     */
    @Nullable
    public static PageCursor decode(@Nullable String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }

        try {
            var decoded = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
            var separatorIndex = decoded.lastIndexOf(SEPARATOR);
            if (separatorIndex < 0) {
                throw new IllegalArgumentException("Invalid cursor format");
            }

            var timestampPart = decoded.substring(0, separatorIndex);
            var idPart = decoded.substring(separatorIndex + 1);

            return new PageCursor(Instant.parse(timestampPart), Long.parseLong(idPart));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid cursor: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid cursor: " + e.getMessage(), e);
        }
    }
}
