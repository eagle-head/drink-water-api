package br.com.drinkwater.drinkwaterapi.usermanagement.dto;

import br.com.drinkwater.drinkwaterapi.usermanagement.model.BiologicalSex;
import br.com.drinkwater.drinkwaterapi.usermanagement.model.HeightUnit;
import br.com.drinkwater.drinkwaterapi.usermanagement.model.WeightUnit;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class UserCreateDTO {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6, max = 20)
    private String password;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotNull
    @Past
    private OffsetDateTime birthDate;

    @NotNull
    private BiologicalSex biologicalSex;

    @Min(45)
    private double weight;

    @NotNull
    private WeightUnit weightUnit;

    @Min(100)
    private double height;

    @NotNull
    private HeightUnit heightUnit;
}
