package br.com.drinkwater.usermanagement.dto;

import br.com.drinkwater.usermanagement.model.BiologicalSex;
import br.com.drinkwater.usermanagement.validation.ValidBirthDate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/** Request DTO for a user's personal information (name, birth date, biological sex). */
@Schema(description = "Personal information of a user")
public record PersonalDTO(
        @Schema(description = "First name", example = "John", minLength = 2, maxLength = 50)
                @NotBlank(message = "{personal.first-name.not-blank}")
                @Size(min = 2, max = 50, message = "{personal.first-name.size}")
                @Pattern(
                        regexp =
                                "^[a-zA-ZÀ-ÿ\u0080-\u024F']"
                                        + "(?:[a-zA-ZÀ-ÿ\u0080-\u024F'\\s-]"
                                        + "*[a-zA-ZÀ-ÿ\u0080-\u024F']){1,}$",
                        message = "{personal.first-name.pattern}")
                String firstName,
        @Schema(description = "Last name", example = "Doe", minLength = 2, maxLength = 50)
                @NotBlank(message = "{personal.last-name.not-blank}")
                @Size(min = 2, max = 50, message = "{personal.last-name.size}")
                @Pattern(
                        regexp =
                                "^[a-zA-ZÀ-ÿ\u0080-\u024F']"
                                        + "(?:[a-zA-ZÀ-ÿ\u0080-\u024F'\\s-]"
                                        + "*[a-zA-ZÀ-ÿ\u0080-\u024F']){1,}$",
                        message = "{personal.last-name.pattern}")
                String lastName,
        @Schema(
                        description = "Date of birth (age must be between 13 and 99)",
                        example = "1990-01-15")
                @NotNull(message = "{personal.birth-date.not-null}")
                @ValidBirthDate
                LocalDate birthDate,
        @Schema(description = "Biological sex", example = "MALE")
                @NotNull(message = "{personal.biological-sex.not-null}")
                BiologicalSex biologicalSex) {}
