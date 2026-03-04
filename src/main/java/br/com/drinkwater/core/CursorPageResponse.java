package br.com.drinkwater.core;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.springframework.lang.Nullable;

/**
 * Generic response wrapper for cursor-based pagination. Contains the page content, the requested
 * page size, whether a next page exists, and the opaque cursor string for fetching the next page.
 * The content list is defensively copied to an unmodifiable list.
 *
 * @param <T> the type of elements in the page
 */
@Schema(description = "Cursor-based paginated response")
public record CursorPageResponse<T>(
        @Schema(description = "List of items in the current page") List<T> content,
        @Schema(description = "Requested page size", example = "10") int pageSize,
        @Schema(description = "Whether more pages are available", example = "true") boolean hasNext,
        @Schema(description = "Opaque cursor for fetching the next page (null if no next page)")
                @Nullable
                String nextCursor) {

    public CursorPageResponse {
        content = List.copyOf(content);
    }
}
