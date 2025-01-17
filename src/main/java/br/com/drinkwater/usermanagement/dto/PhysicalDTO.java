package br.com.drinkwater.usermanagement.dto;

import br.com.drinkwater.usermanagement.model.HeightUnit;
import br.com.drinkwater.usermanagement.model.WeightUnit;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PhysicalDTO(

        @NotNull(message = "Weight is required")
        @Positive(message = "Weight must be a positive value")
        @DecimalMax(value = "500.0", message = "Weight cannot be greater than 500")
        @DecimalMin(value = "20.0", message = "Weight cannot be less than 20")
        BigDecimal weight,

        @NotNull(message = "Weight unit is required")
        WeightUnit weightUnit,

        @NotNull(message = "Height is required")
        @Positive(message = "Height must be a positive value")
        @DecimalMax(value = "250", message = "Height cannot be greater than 250 centimeters")
        @DecimalMin(value = "50", message = "Height cannot be less than 50 centimeters")
        BigDecimal height,

        @NotNull(message = "Height unit is required")
        HeightUnit heightUnit
) {
}