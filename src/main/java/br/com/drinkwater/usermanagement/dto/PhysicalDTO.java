package br.com.drinkwater.usermanagement.dto;

import br.com.drinkwater.usermanagement.model.HeightUnit;
import br.com.drinkwater.usermanagement.model.WeightUnit;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/** Request DTO for a user's physical measurements (weight and height with units). */
@Schema(description = "Physical measurements of a user")
public record PhysicalDTO(
        @Schema(description = "Body weight", example = "70.5", minimum = "45", maximum = "500")
                @NotNull(message = "{physical.weight.not-null}")
                @Positive(message = "{physical.weight.positive}")
                @DecimalMax(value = "500", message = "{physical.weight.decimal-max}")
                @DecimalMin(value = "45", message = "{physical.weight.decimal-min}")
                BigDecimal weight,
        @Schema(description = "Weight unit", example = "KG")
                @NotNull(message = "{physical.weight-unit.not-null}")
                WeightUnit weightUnit,
        @Schema(description = "Body height", example = "175", minimum = "50", maximum = "250")
                @NotNull(message = "{physical.height.not-null}")
                @Positive(message = "{physical.height.positive}")
                @DecimalMax(value = "250", message = "{physical.height.decimal-max}")
                @DecimalMin(value = "50", message = "{physical.height.decimal-min}")
                BigDecimal height,
        @Schema(description = "Height unit", example = "CM")
                @NotNull(message = "{physical.height-unit.not-null}")
                HeightUnit heightUnit) {}
