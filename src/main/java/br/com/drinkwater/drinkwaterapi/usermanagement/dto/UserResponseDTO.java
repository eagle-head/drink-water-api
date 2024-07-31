package br.com.drinkwater.drinkwaterapi.usermanagement.dto;

import br.com.drinkwater.drinkwaterapi.usermanagement.model.BiologicalSex;
import br.com.drinkwater.drinkwaterapi.usermanagement.model.HeightUnit;
import br.com.drinkwater.drinkwaterapi.usermanagement.model.WeightUnit;

import java.time.OffsetDateTime;

public record UserResponseDTO(
        Long id,
        String email,
        String firstName,
        String lastName,
        OffsetDateTime birthDate,
        BiologicalSex biologicalSex,
        double weight,
        WeightUnit weightUnit,
        double height,
        HeightUnit heightUnit
) {}
