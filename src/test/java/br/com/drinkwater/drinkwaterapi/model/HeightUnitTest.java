package br.com.drinkwater.drinkwaterapi.model;

import br.com.drinkwater.drinkwaterapi.usermanagement.model.HeightUnit;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class HeightUnitTest {

    @Test
    public void fromCode_ValidCode_ReturnsHeightUnit() {
        assertThat(HeightUnit.fromCode(1)).isEqualTo(HeightUnit.CM);
    }

    @Test
    public void fromCode_InvalidCode_ThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> HeightUnit.fromCode(2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid HeightUnit code: 2");
    }

    @Test
    public void getCode_ReturnsCorrectCode() {
        assertThat(HeightUnit.CM.getCode()).isEqualTo(1);
    }
}
