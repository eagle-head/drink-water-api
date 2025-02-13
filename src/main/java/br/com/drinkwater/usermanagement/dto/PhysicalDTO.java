package br.com.drinkwater.usermanagement.dto;

import br.com.drinkwater.usermanagement.model.HeightUnit;
import br.com.drinkwater.usermanagement.model.WeightUnit;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PhysicalDTO(

        @NotNull(message = "{physicalDTO.weight.notNull}")
        @Positive(message = "{physicalDTO.weight.positive}")
        @DecimalMax(value = "500", message = "{physicalDTO.weight.decimalMax}")
        @DecimalMin(value = "45", message = "{physicalDTO.weight.decimalMin}")
        BigDecimal weight,

        @NotNull(message = "{physicalDTO.weightUnit.notNull}")
        WeightUnit weightUnit,

        @NotNull(message = "{physicalDTO.height.notNull}")
        @Positive(message = "{physicalDTO.height.positive}")
        @DecimalMax(value = "250", message = "{physicalDTO.height.decimalMax}")
        @DecimalMin(value = "50", message = "{physicalDTO.height.decimalMin}")
        BigDecimal height,

        @NotNull(message = "{physicalDTO.heightUnit.notNull}")
        HeightUnit heightUnit
) {
}
