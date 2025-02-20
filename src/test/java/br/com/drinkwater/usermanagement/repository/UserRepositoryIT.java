package br.com.drinkwater.usermanagement.repository;

import br.com.drinkwater.usermanagement.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static br.com.drinkwater.usermanagement.constants.RepositoryTestConstants.REPOSITORY_USER_UUID;
import static br.com.drinkwater.usermanagement.constants.RepositoryTestConstants.createTestUser;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void givenValidPublicId_whenFindByPublicId_thenReturnUser() {
        User testUser = createTestUser();
        this.testEntityManager.persist(testUser);
        this.testEntityManager.flush();
        this.testEntityManager.clear();

        Optional<User> sut = this.userRepository.findByPublicId(REPOSITORY_USER_UUID);

        assertThat(sut).isPresent();
        assertThat(sut.get().getPublicId()).isEqualTo(REPOSITORY_USER_UUID);
    }

    @Test
    public void givenValidPublicId_whenExistsByPublicId_thenReturnTrue() {
        User testUser = createTestUser();
        this.testEntityManager.persist(testUser);
        this.testEntityManager.flush();
        this.testEntityManager.clear();

        boolean sut = this.userRepository.existsByPublicId(REPOSITORY_USER_UUID);

        assertThat(sut).isTrue();
    }

    @Test
    public void givenValidPublicId_whenDeleteByPublicId_thenSuccess() {
        User testUser = createTestUser();
        this.testEntityManager.persist(testUser);
        this.testEntityManager.flush();
        this.testEntityManager.clear();

        assertThat(this.userRepository.existsByPublicId(REPOSITORY_USER_UUID)).isTrue();

        this.userRepository.deleteByPublicId(REPOSITORY_USER_UUID);

        this.testEntityManager.flush();
        this.testEntityManager.clear();

        assertThat(this.userRepository.existsByPublicId(REPOSITORY_USER_UUID)).isFalse();
    }
}