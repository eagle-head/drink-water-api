package br.com.drinkwater.core;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponse<T>(

        List<T> content,
        long totalElements,
        int totalPages,
        int pageSize,
        int pageNumber,
        boolean first,
        boolean last
) {
    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getSize(),
                page.getNumber(),
                page.isFirst(),
                page.isLast()
        );
    }
}