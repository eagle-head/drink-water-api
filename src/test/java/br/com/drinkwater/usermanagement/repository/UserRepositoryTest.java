package br.com.drinkwater.usermanagement.repository;

import static br.com.drinkwater.usermanagement.constants.UserRepositoryTestConstants.REPOSITORY_USER_UUID;
import static br.com.drinkwater.usermanagement.constants.UserRepositoryTestConstants.createTestUser;
import static org.assertj.core.api.Assertions.assertThat;

import br.com.drinkwater.usermanagement.model.User;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired private UserRepository userRepository;

    @Test
    void givenValidPublicId_whenFindByPublicId_thenReturnUser() {
        User testUser = createTestUser();
        userRepository.save(testUser);

        Optional<User> sut = userRepository.findByPublicId(REPOSITORY_USER_UUID);

        assertThat(sut).isPresent();
        assertThat(sut.get().getPublicId()).isEqualTo(REPOSITORY_USER_UUID);
    }

    @Test
    void givenValidPublicId_whenExistsByPublicId_thenReturnTrue() {
        User testUser = createTestUser();
        userRepository.save(testUser);

        boolean sut = userRepository.existsByPublicId(REPOSITORY_USER_UUID);

        assertThat(sut).isTrue();
    }

    @Test
    void givenValidPublicId_whenDeleteByPublicId_thenSuccess() {
        User testUser = createTestUser();
        userRepository.save(testUser);

        assertThat(userRepository.existsByPublicId(REPOSITORY_USER_UUID)).isTrue();

        userRepository.deleteByPublicId(REPOSITORY_USER_UUID);

        assertThat(userRepository.existsByPublicId(REPOSITORY_USER_UUID)).isFalse();
    }

    @Test
    void givenNonExistentPublicId_whenFindByPublicId_thenReturnEmpty() {
        // Given
        var nonExistentUuid = UUID.fromString("00000000-0000-0000-0000-000000000000");

        // When
        Optional<User> sut = userRepository.findByPublicId(nonExistentUuid);

        // Then
        assertThat(sut).isEmpty();
    }

    @Test
    void givenNonExistentPublicId_whenExistsByPublicId_thenReturnFalse() {
        // Given
        var nonExistentUuid = UUID.fromString("00000000-0000-0000-0000-000000000000");

        // When
        boolean sut = userRepository.existsByPublicId(nonExistentUuid);

        // Then
        assertThat(sut).isFalse();
    }
}
