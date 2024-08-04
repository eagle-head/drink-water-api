package br.com.drinkwater.drinkwaterapi.model;

import br.com.drinkwater.drinkwaterapi.usermanagement.model.BiologicalSex;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BiologicalSexTest {

    @Test
    public void fromCode_ValidCode_ReturnsBiologicalSex() {
        assertThat(BiologicalSex.fromCode(1)).isEqualTo(BiologicalSex.MALE);
        assertThat(BiologicalSex.fromCode(2)).isEqualTo(BiologicalSex.FEMALE);
    }

    @Test
    public void fromCode_InvalidCode_ThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> BiologicalSex.fromCode(3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid BiologicalSex code: 3");
    }

    @Test
    public void getCode_ReturnsCorrectCode() {
        assertThat(BiologicalSex.MALE.getCode()).isEqualTo(1);
        assertThat(BiologicalSex.FEMALE.getCode()).isEqualTo(2);
    }
}
