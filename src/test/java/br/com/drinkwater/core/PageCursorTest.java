package br.com.drinkwater.core;

import static org.assertj.core.api.Assertions.*;

import java.time.Instant;
import org.junit.jupiter.api.Test;

final class PageCursorTest {

    private static final Instant SAMPLE_INSTANT = Instant.parse("2025-06-15T10:30:00Z");
    private static final Long SAMPLE_ID = 42L;

    @Test
    void givenValidData_whenEncode_thenReturnsNonEmptyString() {
        // Given
        var cursor = new PageCursor(SAMPLE_INSTANT, SAMPLE_ID);

        // When
        var encoded = cursor.encode();

        // Then
        assertThat(encoded).isNotBlank();
    }

    @Test
    void givenEncodedCursor_whenDecode_thenReturnsOriginalValues() {
        // Given
        var original = new PageCursor(SAMPLE_INSTANT, SAMPLE_ID);
        var encoded = original.encode();

        // When
        var decoded = PageCursor.decode(encoded);

        // Then
        assertThat(decoded).isNotNull();
        assertThat(decoded.dateTimeUTC()).isEqualTo(SAMPLE_INSTANT);
        assertThat(decoded.id()).isEqualTo(SAMPLE_ID);
    }

    @Test
    void givenNullCursor_whenDecode_thenReturnsNull() {
        // When
        var decoded = PageCursor.decode(null);

        // Then
        assertThat(decoded).isNull();
    }

    @Test
    void givenBlankCursor_whenDecode_thenReturnsNull() {
        // When
        var decoded = PageCursor.decode("   ");

        // Then
        assertThat(decoded).isNull();
    }

    @Test
    void givenEmptyCursor_whenDecode_thenReturnsNull() {
        // When
        var decoded = PageCursor.decode("");

        // Then
        assertThat(decoded).isNull();
    }

    @Test
    void givenInvalidBase64_whenDecode_thenThrowsIllegalArgumentException() {
        // When & Then
        assertThatThrownBy(() -> PageCursor.decode("!!!not-valid-base64!!!"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenMalformedContent_whenDecode_thenThrowsIllegalArgumentException() {
        // Given - valid Base64 but no separator
        var encoded =
                java.util.Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString("noseparator".getBytes());

        // When & Then
        assertThatThrownBy(() -> PageCursor.decode(encoded))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenInvalidTimestamp_whenDecode_thenThrowsIllegalArgumentException() {
        // Given
        var encoded =
                java.util.Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString("not-a-timestamp|42".getBytes());

        // When & Then
        assertThatThrownBy(() -> PageCursor.decode(encoded))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenInvalidId_whenDecode_thenThrowsIllegalArgumentException() {
        // Given
        var encoded =
                java.util.Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString("2025-06-15T10:30:00Z|not-a-number".getBytes());

        // When & Then
        assertThatThrownBy(() -> PageCursor.decode(encoded))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenNullDateTimeUTC_whenConstruct_thenThrowsIllegalArgumentException() {
        // When & Then
        assertThatThrownBy(() -> new PageCursor(null, SAMPLE_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("dateTimeUTC");
    }

    @Test
    void givenNullId_whenConstruct_thenThrowsIllegalArgumentException() {
        // When & Then
        assertThatThrownBy(() -> new PageCursor(SAMPLE_INSTANT, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id");
    }

    @Test
    void givenZeroId_whenConstruct_thenThrowsIllegalArgumentException() {
        // When & Then
        assertThatThrownBy(() -> new PageCursor(SAMPLE_INSTANT, 0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id");
    }

    @Test
    void givenNegativeId_whenConstruct_thenThrowsIllegalArgumentException() {
        // When & Then
        assertThatThrownBy(() -> new PageCursor(SAMPLE_INSTANT, -1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id");
    }

    @Test
    void givenRoundTrip_whenEncodeAndDecode_thenValuesPreserved() {
        // Given
        var instants =
                new Instant[] {
                    Instant.parse("2020-01-01T00:00:00Z"),
                    Instant.parse("2025-12-31T23:59:59Z"),
                    Instant.parse("2025-06-15T10:30:00.123456789Z")
                };

        for (Instant instant : instants) {
            var original = new PageCursor(instant, 999L);
            var decoded = PageCursor.decode(original.encode());

            assertThat(decoded).isNotNull();
            assertThat(decoded.dateTimeUTC()).isEqualTo(instant);
            assertThat(decoded.id()).isEqualTo(999L);
        }
    }
}
