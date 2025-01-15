package br.com.drinkwater.usermanagement.dto;

import br.com.drinkwater.usermanagement.model.HeightUnit;
import br.com.drinkwater.usermanagement.model.WeightUnit;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PhysicalDataDTO(

        @NotNull @Min(45) BigDecimal weight,
        @NotNull WeightUnit weightUnit,
        @NotNull @Min(100) BigDecimal height,
        @NotNull HeightUnit heightUnit
) {
}