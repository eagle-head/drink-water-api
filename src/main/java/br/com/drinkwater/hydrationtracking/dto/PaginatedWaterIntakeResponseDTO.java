package br.com.drinkwater.hydrationtracking.dto;

import java.util.List;

public record PaginatedWaterIntakeResponseDTO(
        List<WaterIntakeResponseDTO> data,
        PaginationDTO pagination
) {
}
