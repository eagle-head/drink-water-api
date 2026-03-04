package br.com.drinkwater.core;

import java.util.List;
import org.springframework.data.domain.Page;

/**
 * Generic response wrapper for offset-based pagination. The content list is defensively copied to
 * an unmodifiable list.
 *
 * @param <T> the type of elements in the page
 */
public record PageResponse<T>(
        List<T> content,
        long totalElements,
        int totalPages,
        int pageSize,
        int pageNumber,
        boolean first,
        boolean last) {
    public PageResponse {
        content = List.copyOf(content);
    }

    /**
     * Creates a PageResponse from a Spring Data {@link Page}.
     *
     * @param page the Spring Data page
     * @param <T> the type of elements
     * @return a new PageResponse wrapping the page data
     */
    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getSize(),
                page.getNumber(),
                page.isFirst(),
                page.isLast());
    }
}
