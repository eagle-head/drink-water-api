package br.com.drinkwater.core;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

final class CursorPageResponseTest {

    @Test
    void givenContent_whenCreated_thenContentIsImmutableCopy() {
        // Given
        var mutableList = new ArrayList<>(List.of("a", "b", "c"));

        // When
        var response = new CursorPageResponse<>(mutableList, 10, false, null);
        mutableList.add("d");

        // Then
        assertThat(response.content()).hasSize(3);
        assertThat(response.content()).containsExactly("a", "b", "c");
    }

    @Test
    void givenContent_whenModifyingReturnedList_thenThrowsUnsupportedOperationException() {
        // Given
        var response = new CursorPageResponse<>(List.of("a", "b"), 10, false, null);

        // When & Then
        assertThatThrownBy(() -> response.content().add("c"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void givenHasNextTrue_whenCreated_thenHasNextIsTrue() {
        // Given & When
        var response = new CursorPageResponse<>(List.of("a"), 10, true, "someCursor");

        // Then
        assertThat(response.hasNext()).isTrue();
        assertThat(response.nextCursor()).isEqualTo("someCursor");
    }

    @Test
    void givenHasNextFalse_whenCreated_thenNextCursorIsNull() {
        // Given & When
        var response = new CursorPageResponse<>(List.of("a"), 10, false, null);

        // Then
        assertThat(response.hasNext()).isFalse();
        assertThat(response.nextCursor()).isNull();
    }

    @Test
    void givenEmptyContent_whenCreated_thenContentIsEmpty() {
        // Given & When
        var response = new CursorPageResponse<>(List.of(), 10, false, null);

        // Then
        assertThat(response.content()).isEmpty();
        assertThat(response.pageSize()).isEqualTo(10);
    }
}
