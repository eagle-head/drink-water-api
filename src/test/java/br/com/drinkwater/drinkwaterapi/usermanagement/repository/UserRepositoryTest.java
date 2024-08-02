package br.com.drinkwater.drinkwaterapi.usermanagement.repository;

import static br.com.drinkwater.drinkwaterapi.usermanagement.constants.UserConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.drinkwater.drinkwaterapi.usermanagement.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.validation.annotation.Validated;

@DataJpaTest
@ComponentScan(basePackages = "br.com.drinkwater.drinkwaterapi",
        includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Validated.class))
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void createUser_WithValidData_ReturnsUser() {
        User user = userRepository.save(USER);

        Optional<User> optionalUser = userRepository.findById(user.getId());
        assertThat(optionalUser).isPresent();

        User sut = optionalUser.get();
        assertThat(sut.getEmail()).isEqualTo(USER.getEmail());
        assertThat(sut.getPassword()).isEqualTo(USER.getPassword());
        assertThat(sut.getFirstName()).isEqualTo(USER.getFirstName());
        assertThat(sut.getLastName()).isEqualTo(USER.getLastName());
        assertThat(sut.getBirthDate()).isEqualTo(USER.getBirthDate());
        assertThat(sut.getBiologicalSex()).isEqualTo(USER.getBiologicalSex());
        assertThat(sut.getWeight()).isEqualTo(USER.getWeight());
        assertThat(sut.getWeightUnit()).isEqualTo(USER.getWeightUnit());
        assertThat(sut.getHeight()).isEqualTo(USER.getHeight());
        assertThat(sut.getHeightUnit()).isEqualTo(USER.getHeightUnit());
    }

    @Test
    public void createUser_WithExistingEmail_ThrowsException() {
        userRepository.save(USER);
        assertThatThrownBy(() -> userRepository.save(USER_WITH_SAME_EMAIL))
                .isInstanceOf(RuntimeException.class);
    }
}
