package br.com.drinkwater.drinkwaterapi.usermanagement.service;

import static br.com.drinkwater.drinkwaterapi.usermanagement.constants.UserConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import br.com.drinkwater.drinkwaterapi.usermanagement.dto.UserCreateDTO;
import br.com.drinkwater.drinkwaterapi.usermanagement.exception.EmailAlreadyUsedException;
import br.com.drinkwater.drinkwaterapi.usermanagement.mapper.UserMapper;
import br.com.drinkwater.drinkwaterapi.usermanagement.dto.UserResponseDTO;
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

    @Mock
    private UserMapper mapper;

    @Test
    public void createUser_WithValidData_ReturnsUserResponseDTO() {
        when(userRepository.save(USER)).thenReturn(USER);
        when(mapper.convertToEntity(USER_CREATE_DTO)).thenReturn(USER);
        when(mapper.convertToDTO(USER)).thenReturn(USER_RESPONSE_DTO);

        UserResponseDTO sut = userService.create(USER_CREATE_DTO);
        assertThat(sut).isEqualTo(USER_RESPONSE_DTO);
    }

    @Test
    public void createUser_WithInvalidData_ReturnsUser() {
        when(userRepository.existsByEmail(USER_CREATE_DTO.email())).thenReturn(false);
        when(userRepository.save(USER_WITH_INVALID_DATA)).thenThrow(RuntimeException.class);
        when(mapper.convertToEntity(any(UserCreateDTO.class))).thenReturn(USER_WITH_INVALID_DATA);

        assertThatThrownBy(() -> userService.create(USER_CREATE_DTO)).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void createUser_WithExistingEmail_ThrowsEmailAlreadyUsedException() {
        when(userRepository.existsByEmail(USER_CREATE_DTO.email())).thenReturn(true);

        assertThatThrownBy(() -> userService.create(USER_CREATE_DTO))
                .isInstanceOf(EmailAlreadyUsedException.class)
                .hasMessage("The email provided is already in use.");
    }

    @Test
    public void findUserById_WithValidData_ReturnsUserResponseDTO() {
        when(userRepository.findById(USER.getId())).thenReturn(Optional.of(USER));
        when(mapper.convertToDTO(USER)).thenReturn(USER_RESPONSE_DTO);

        Optional<UserResponseDTO> sut = userService.findById(USER.getId());
        assertThat(sut).isNotEmpty();
        assertThat(sut.get()).isEqualTo(USER_RESPONSE_DTO);
    }

    @Test
    public void findUserById_WithInvalidData_ReturnsEmpty() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<UserResponseDTO> sut = userService.findById(anyLong());
        assertThat(sut).isEmpty();
    }

    @Test
    public void deleteUserById_WithExistingId_DoesNotThrowAnyException() {
        assertThatCode(() -> userService.deleteById(anyLong())).doesNotThrowAnyException();
    }

    @Test
    public void deleteUserById_WithNonExistingId_ThrowsException() {
        doThrow(new RuntimeException()).when(userRepository).deleteById(USER.getId());

        assertThatThrownBy(() -> userService.deleteById(USER.getId())).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void updateUser_WithValidData_ReturnsUpdatedUserResponseDTO() {
        when(userRepository.findById(USER.getId())).thenReturn(Optional.of(USER));
        when(userRepository.save(USER)).thenReturn(USER);
        when(mapper.convertToDTO(USER)).thenReturn(USER_RESPONSE_DTO);

        Optional<UserResponseDTO> sut = userService.update(USER.getId(), USER);
        assertThat(sut).isNotEmpty();
        assertThat(sut.get()).isEqualTo(USER_RESPONSE_DTO);
    }

    @Test
    public void updateUser_WithNonExistingId_ReturnsEmpty() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<UserResponseDTO> sut = userService.update(anyLong(), USER);
        assertThat(sut).isEmpty();
    }

    @Test
    public void existsById_WithExistingId_ReturnsTrue() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        boolean result = userService.existsById(anyLong());

        assertThat(result).isTrue();
    }

    @Test
    public void existsById_WithNonExistingId_ReturnsFalse() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        boolean result = userService.existsById(anyLong());

        assertThat(result).isFalse();
    }
}
