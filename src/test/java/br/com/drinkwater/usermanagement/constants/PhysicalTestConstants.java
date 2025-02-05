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
        var physical = new Physical();
        physical.setWeight(PHYSICAL_DTO.weight());
        physical.setWeightUnit(PHYSICAL_DTO.weightUnit());
        physical.setHeight(PHYSICAL_DTO.height());
        physical.setHeightUnit(PHYSICAL_DTO.heightUnit());

        return physical;
    }
}