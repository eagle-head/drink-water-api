package br.com.drinkwater.hydrationtracking.dto;

public record PaginationDTO(
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
}
