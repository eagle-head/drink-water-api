package br.com.drinkwater.hydrationtracking.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;

final class WaterIntakeFilterDTOTest {

    private static final Instant START = Instant.parse("2025-01-01T00:00:00Z");
    private static final Instant END = Instant.parse("2025-01-31T23:59:59Z");

    @Test
    void givenNullSize_whenCreated_thenDefaultsTo10() {
        var dto = new WaterIntakeFilterDTO(START, END, null, null, null, null, null, null);

        assertThat(dto.size()).isEqualTo(10);
    }

    @Test
    void givenExplicitSize_whenCreated_thenUsesProvidedValue() {
        var dto = new WaterIntakeFilterDTO(START, END, null, null, null, 25, null, null);

        assertThat(dto.size()).isEqualTo(25);
    }

    @Test
    void givenNullSortField_whenCreated_thenDefaultsToDateTimeUTC() {
        var dto = new WaterIntakeFilterDTO(START, END, null, null, null, null, null, null);

        assertThat(dto.sortField()).isEqualTo("dateTimeUTC");
    }

    @Test
    void givenValidSortField_whenCreated_thenUsesProvidedValue() {
        var dto = new WaterIntakeFilterDTO(START, END, null, null, null, null, "volume", null);

        assertThat(dto.sortField()).isEqualTo("volume");
    }

    @Test
    void givenInvalidSortField_whenCreated_thenFallsBackToDateTimeUTC() {
        var dto =
                new WaterIntakeFilterDTO(START, END, null, null, null, null, "invalidField", null);

        assertThat(dto.sortField()).isEqualTo("dateTimeUTC");
    }

    @Test
    void givenNullSortDirection_whenCreated_thenDefaultsToDESC() {
        var dto = new WaterIntakeFilterDTO(START, END, null, null, null, null, null, null);

        assertThat(dto.sortDirection()).isEqualTo("DESC");
    }

    @Test
    void givenLowercaseSortDirection_whenCreated_thenConvertsToUpperCase() {
        var dto = new WaterIntakeFilterDTO(START, END, null, null, null, null, null, "asc");

        assertThat(dto.sortDirection()).isEqualTo("ASC");
    }

    @Test
    void givenExplicitSortDirection_whenCreated_thenUsesProvidedValue() {
        var dto = new WaterIntakeFilterDTO(START, END, null, null, null, null, null, "ASC");

        assertThat(dto.sortDirection()).isEqualTo("ASC");
    }

    @Test
    void givenAllDefaults_whenCreated_thenAllDefaultsApplied() {
        var dto = new WaterIntakeFilterDTO(START, END, null, null, null, null, null, null);

        assertThat(dto.size()).isEqualTo(10);
        assertThat(dto.sortField()).isEqualTo("dateTimeUTC");
        assertThat(dto.sortDirection()).isEqualTo("DESC");
    }
}
