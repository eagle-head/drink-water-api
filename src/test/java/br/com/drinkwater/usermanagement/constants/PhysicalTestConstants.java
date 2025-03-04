package br.com.drinkwater.usermanagement.constants;

import br.com.drinkwater.usermanagement.dto.PhysicalDTO;
import br.com.drinkwater.usermanagement.model.HeightUnit;
import br.com.drinkwater.usermanagement.model.Physical;
import br.com.drinkwater.usermanagement.model.WeightUnit;

import java.math.BigDecimal;

public final class PhysicalTestConstants {

    private PhysicalTestConstants() {
    }

    public static final PhysicalDTO PHYSICAL_DTO;
    public static final Physical PHYSICAL;

    static {
        PHYSICAL_DTO = new PhysicalDTO(
                BigDecimal.valueOf(70.5),
                WeightUnit.KG,
                BigDecimal.valueOf(175.0),
                HeightUnit.CM
        );

        PHYSICAL = createPhysicalFromDTO();
    }

    private static Physical createPhysicalFromDTO() {
        return new Physical(
                PHYSICAL_DTO.weight(),
                PHYSICAL_DTO.weightUnit(),
                PHYSICAL_DTO.height(),
                PHYSICAL_DTO.heightUnit()
        );
    }
}