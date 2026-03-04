package br.com.drinkwater.hydrationtracking.repository;

import static br.com.drinkwater.usermanagement.constants.UserRepositoryTestConstants.createTestUser;
import static org.assertj.core.api.Assertions.assertThat;

import br.com.drinkwater.core.PageCursor;
import br.com.drinkwater.hydrationtracking.model.VolumeUnit;
import br.com.drinkwater.hydrationtracking.model.WaterIntake;
import br.com.drinkwater.usermanagement.model.User;
import br.com.drinkwater.usermanagement.repository.UserRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(WaterIntakeSearchRepositoryImpl.class)
final class WaterIntakeSearchRepositoryImplTest {

    @Autowired private WaterIntakeSearchRepositoryImpl searchRepository;

    @Autowired private WaterIntakeRepository waterIntakeRepository;

    @Autowired private UserRepository userRepository;

    private User testUser;
    private Instant baseTime;

    @BeforeEach
    void setUp() {
        testUser = userRepository.save(createTestUser());
        baseTime = Instant.now().truncatedTo(ChronoUnit.SECONDS).minus(1, ChronoUnit.HOURS);
    }

    @Test
    void givenNoData_whenSearch_thenReturnsEmptyList() {
        // When
        var results =
                searchRepository.search(
                        testUser.getId(), null, null, null, null, 11, null, "dateTimeUTC", "DESC");

        // Then
        assertThat(results).isEmpty();
    }

    @Test
    void givenDataWithinDateRange_whenSearchWithDateFilter_thenReturnsFilteredResults() {
        // Given
        var startDate = baseTime;
        var endDate = baseTime.plus(30, ChronoUnit.MINUTES);

        saveWaterIntake(baseTime.plus(10, ChronoUnit.MINUTES), 200);
        saveWaterIntake(baseTime.plus(20, ChronoUnit.MINUTES), 300);
        saveWaterIntake(baseTime.plus(50, ChronoUnit.MINUTES), 400);

        // When
        var results =
                searchRepository.search(
                        testUser.getId(),
                        startDate,
                        endDate,
                        null,
                        null,
                        11,
                        null,
                        "dateTimeUTC",
                        "DESC");

        // Then
        assertThat(results).hasSize(2);
    }

    @Test
    void givenDataWithVolumeRange_whenSearchWithVolumeFilter_thenReturnsFilteredResults() {
        // Given
        saveWaterIntake(baseTime.plus(1, ChronoUnit.MINUTES), 100);
        saveWaterIntake(baseTime.plus(2, ChronoUnit.MINUTES), 250);
        saveWaterIntake(baseTime.plus(3, ChronoUnit.MINUTES), 500);

        // When
        var results =
                searchRepository.search(
                        testUser.getId(), null, null, 200, 400, 11, null, "dateTimeUTC", "DESC");

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getVolume()).isEqualTo(250);
    }

    @Test
    void givenMultipleRecords_whenSearchWithLimit_thenRespectsLimit() {
        // Given
        for (int i = 0; i < 5; i++) {
            saveWaterIntake(baseTime.plus(i, ChronoUnit.MINUTES), 200 + i);
        }

        // When
        var results =
                searchRepository.search(
                        testUser.getId(), null, null, null, null, 3, null, "dateTimeUTC", "DESC");

        // Then
        assertThat(results).hasSize(3);
    }

    @Test
    void givenMultipleRecords_whenSearchDescending_thenReturnsMostRecentFirst() {
        // Given
        var older = saveWaterIntake(baseTime.plus(1, ChronoUnit.MINUTES), 100);
        var newer = saveWaterIntake(baseTime.plus(10, ChronoUnit.MINUTES), 200);

        // When
        var results =
                searchRepository.search(
                        testUser.getId(), null, null, null, null, 11, null, "dateTimeUTC", "DESC");

        // Then
        assertThat(results).hasSize(2);
        assertThat(results.getFirst().getId()).isEqualTo(newer.getId());
        assertThat(results.getLast().getId()).isEqualTo(older.getId());
    }

    @Test
    void givenCursor_whenSearch_thenReturnsItemsAfterCursor() {
        // Given
        var item1 = saveWaterIntake(baseTime.plus(1, ChronoUnit.MINUTES), 100);
        var item2 = saveWaterIntake(baseTime.plus(2, ChronoUnit.MINUTES), 200);
        var item3 = saveWaterIntake(baseTime.plus(3, ChronoUnit.MINUTES), 300);

        var cursor = new PageCursor(item3.getDateTimeUTC(), item3.getId());

        // When
        var results =
                searchRepository.search(
                        testUser.getId(),
                        null,
                        null,
                        null,
                        null,
                        11,
                        cursor,
                        "dateTimeUTC",
                        "DESC");

        // Then
        assertThat(results).hasSize(2);
        assertThat(results.getFirst().getId()).isEqualTo(item2.getId());
        assertThat(results.getLast().getId()).isEqualTo(item1.getId());
    }

    @Test
    void givenCursorAtEnd_whenSearch_thenReturnsEmptyList() {
        // Given
        var item1 = saveWaterIntake(baseTime.plus(1, ChronoUnit.MINUTES), 100);

        var cursor = new PageCursor(item1.getDateTimeUTC(), item1.getId());

        // When
        var results =
                searchRepository.search(
                        testUser.getId(),
                        null,
                        null,
                        null,
                        null,
                        11,
                        cursor,
                        "dateTimeUTC",
                        "DESC");

        // Then
        assertThat(results).isEmpty();
    }

    @Test
    void givenExactLimitResults_whenSearch_thenReturnsExactlyLimitItems() {
        // Given
        for (int i = 0; i < 3; i++) {
            saveWaterIntake(baseTime.plus(i, ChronoUnit.MINUTES), 200 + i);
        }

        // When
        var results =
                searchRepository.search(
                        testUser.getId(), null, null, null, null, 3, null, "dateTimeUTC", "DESC");

        // Then
        assertThat(results).hasSize(3);
    }

    @Test
    void givenDifferentUser_whenSearch_thenDoesNotReturnOtherUsersData() {
        // Given
        saveWaterIntake(baseTime.plus(1, ChronoUnit.MINUTES), 200);

        // When
        var results =
                searchRepository.search(
                        testUser.getId() + 999,
                        null,
                        null,
                        null,
                        null,
                        11,
                        null,
                        "dateTimeUTC",
                        "DESC");

        // Then
        assertThat(results).isEmpty();
    }

    @Test
    void givenUnknownSortField_whenSearch_thenUsesDefaultDateColumn() {
        saveWaterIntake(baseTime.plus(1, ChronoUnit.MINUTES), 200);

        var results =
                searchRepository.search(
                        testUser.getId(), null, null, null, null, 11, null, "unknownField", "DESC");

        assertThat(results).hasSize(1);
    }

    @Test
    void givenInvalidSortDirection_whenSearch_thenUsesDefaultDesc() {
        saveWaterIntake(baseTime.plus(1, ChronoUnit.MINUTES), 200);

        var results =
                searchRepository.search(
                        testUser.getId(),
                        null,
                        null,
                        null,
                        null,
                        11,
                        null,
                        "dateTimeUTC",
                        "invalid");

        assertThat(results).hasSize(1);
    }

    @Test
    void givenStartDateWithoutEndDate_whenSearch_thenIgnoresDateFilter() {
        // Given
        saveWaterIntake(baseTime.plus(10, ChronoUnit.MINUTES), 200);
        saveWaterIntake(baseTime.plus(50, ChronoUnit.MINUTES), 400);

        // When
        var results =
                searchRepository.search(
                        testUser.getId(),
                        baseTime,
                        null,
                        null,
                        null,
                        11,
                        null,
                        "dateTimeUTC",
                        "DESC");

        // Then
        assertThat(results).hasSize(2);
    }

    @Test
    void givenCursorWithAscSort_whenSearch_thenReturnsItemsAfterCursor() {
        // Given
        var item1 = saveWaterIntake(baseTime.plus(1, ChronoUnit.MINUTES), 100);
        var item2 = saveWaterIntake(baseTime.plus(2, ChronoUnit.MINUTES), 200);
        var item3 = saveWaterIntake(baseTime.plus(3, ChronoUnit.MINUTES), 300);

        var cursor = new PageCursor(item1.getDateTimeUTC(), item1.getId());

        // When
        var results =
                searchRepository.search(
                        testUser.getId(), null, null, null, null, 11, cursor, "dateTimeUTC", "ASC");

        // Then
        assertThat(results).hasSize(2);
        assertThat(results.getFirst().getId()).isEqualTo(item2.getId());
        assertThat(results.getLast().getId()).isEqualTo(item3.getId());
    }

    private WaterIntake saveWaterIntake(Instant dateTime, int volume) {
        var waterIntake = new WaterIntake(dateTime, volume, VolumeUnit.ML, testUser.getId());
        return waterIntakeRepository.save(waterIntake);
    }
}
