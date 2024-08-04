package br.com.drinkwater.drinkwaterapi.model;

import br.com.drinkwater.drinkwaterapi.usermanagement.model.WeightUnit;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class WeightUnitTest {

    @Test
    public void fromCode_ValidCode_ReturnsWeightUnit() {
        assertThat(WeightUnit.fromCode(1)).isEqualTo(WeightUnit.KG);
    }

    @Test
    public void fromCode_InvalidCode_ThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> WeightUnit.fromCode(2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid WeightUnit code: 2");
    }

    @Test
    public void getCode_ReturnsCorrectCode() {
        assertThat(WeightUnit.KG.getCode()).isEqualTo(1);
    }
}
