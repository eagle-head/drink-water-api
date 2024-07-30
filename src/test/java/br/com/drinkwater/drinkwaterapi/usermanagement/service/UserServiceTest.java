package br.com.drinkwater.drinkwaterapi.usermanagement.service;

import static br.com.drinkwater.drinkwaterapi.usermanagement.constants.UserConstants.USER;
import static br.com.drinkwater.drinkwaterapi.usermanagement.constants.UserConstants.USER_WITH_INVALID_DATA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import br.com.drinkwater.drinkwaterapi.usermanagement.model.User;
import br.com.drinkwater.drinkwaterapi.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    public void createUser_WithValidData_ReturnsUser() {
        when(this.userRepository.save(USER)).thenReturn(USER);

        User sut = this.userService.create(USER);
        assertThat(sut).isEqualTo(USER);
    }

    @Test
    public void createUser_WithInvalidData_ReturnsUser() {
        when(this.userRepository.save(USER_WITH_INVALID_DATA)).thenThrow(RuntimeException.class);

        assertThatThrownBy(() -> this.userService.create(USER_WITH_INVALID_DATA)).isInstanceOf(RuntimeException.class);
    }


    @Test
    public void findUserById_WithValidData_ReturnsUser() {
        when(this.userRepository.findById(USER.getId())).thenReturn(Optional.of(USER));

        Optional<User> sut = this.userService.findById(USER.getId());
        assertThat(sut).isNotEmpty();
        assertThat(sut.get()).isEqualTo(USER);
    }

    @Test
    public void findUserById_WithInvalidData_ReturnsUser() {
        when(this.userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<User> sut = this.userService.findById(anyLong());
        assertThat(sut).isEmpty();
    }

    @Test
    public void deleteUserById_WithExistingId_DoesNotThrowAnyException() {
        assertThatCode(() -> this.userService.deleteById(anyLong())).doesNotThrowAnyException();
    }

    @Test
    public void deleteUserById_WithNonExistingId_ThrowsException() {
        doThrow(new RuntimeException()).when(this.userRepository).deleteById(USER.getId());

        assertThatThrownBy(() -> this.userService.deleteById(USER.getId())).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void updateUser_WithValidData_ReturnsUpdatedUser() {
        when(this.userRepository.findById(USER.getId())).thenReturn(Optional.of(USER));
        when(this.userRepository.save(USER)).thenReturn(USER);

        Optional<User> sut = this.userService.update(USER.getId(), USER);
        assertThat(sut).isNotEmpty();
        assertThat(sut.get()).isEqualTo(USER);
    }

    @Test
    public void updateUser_WithNonExistingId_ReturnsEmpty() {
        when(this.userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<User> sut = this.userService.update(anyLong(), USER);
        assertThat(sut).isEmpty();
    }
}
