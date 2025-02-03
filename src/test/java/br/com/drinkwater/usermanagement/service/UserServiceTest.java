package br.com.drinkwater.usermanagement.service;

import br.com.drinkwater.usermanagement.exception.UserAlreadyExistsException;
import br.com.drinkwater.usermanagement.exception.UserNotFoundException;
import br.com.drinkwater.usermanagement.mapper.UserMapper;
import br.com.drinkwater.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static br.com.drinkwater.usermanagement.constants.UserTestConstants.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public final class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Test
    public void givenValidUserData_WhenCreateUser_ThenReturnsUserResponseDTO() {
        when(userRepository.existsByPublicId(USER_UUID))
                .thenReturn(false);
        when(userMapper.toEntity(USER_DTO))
                .thenReturn(USER);
        when(userRepository.save(USER))
                .thenReturn(USER);
        when(userMapper.toDto(USER))
                .thenReturn(USER_RESPONSE_DTO);

        var actualResponse = userService.createUser(USER_UUID, USER_DTO);

        assertThat(actualResponse).isEqualTo(USER_RESPONSE_DTO);
        verify(userRepository).existsByPublicId(USER_UUID);
        verify(userMapper).toEntity(USER_DTO);
        verify(userRepository).save(USER);
        verify(userMapper).toDto(USER);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    public void givenExistingPublicId_whenCreateUser_thenThrowEmailAlreadyUsedException() {
        when(userRepository.existsByPublicId(USER_UUID))
                .thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(USER_UUID, USER_DTO))
                .isInstanceOf(UserAlreadyExistsException.class);

        verify(userRepository).existsByPublicId(USER_UUID);
        verify(userMapper, never()).toEntity(any());
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toDto(any());
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    public void givenValidPublicId_whenGetUserByPublicId_thenReturnUserResponseDTO() {
        when(userRepository.findByPublicId(USER_UUID))
                .thenReturn(Optional.of(USER));
        when(userMapper.toDto(USER))
                .thenReturn(USER_RESPONSE_DTO);

        var result = userService.getUserByPublicId(USER_UUID);

        assertThat(result).isEqualTo(USER_RESPONSE_DTO);
        verify(userRepository).findByPublicId(USER_UUID);
        verify(userMapper).toDto(USER);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    public void givenInvalidPublicId_whenGetUserByPublicId_thenThrowUserNotFoundException() {
        when(userRepository.findByPublicId(USER_UUID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByPublicId(USER_UUID))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByPublicId(USER_UUID);
        verify(userMapper, never()).toDto(any());
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    public void givenValidUserAndUpdateData_whenUpdateUser_thenReturnUpdatedUserDTO() {
        when(userRepository.findByPublicId(USER_UUID))
                .thenReturn(Optional.of(USER));
        when(userRepository.save(USER))
                .thenReturn(USER);
        when(userMapper.toDto(USER))
                .thenReturn(USER_RESPONSE_DTO);

        var result = userService.updateUser(USER_UUID, USER_DTO);

        assertThat(result).isEqualTo(USER_RESPONSE_DTO);
        verify(userRepository).findByPublicId(USER_UUID);
        verify(userMapper).updateUserFromDTO(USER, USER_DTO);
        verify(userRepository).save(USER);
        verify(userMapper).toDto(USER);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    public void givenInvalidPublicId_whenUpdateUser_thenThrowUserNotFoundException() {
        when(userRepository.findByPublicId(USER_UUID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(USER_UUID, USER_DTO))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByPublicId(USER_UUID);
        verify(userMapper, never()).updateUserFromDTO(any(), any());
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toDto(any());
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    public void givenValidPublicId_whenDeleteUser_thenByPublicIdShouldBeDeleted() {
        assertThatCode(() -> userService.deleteByPublicId(USER_UUID))
                .doesNotThrowAnyException();

        verify(userRepository).deleteByPublicId(USER_UUID);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void givenValidPublicId_whenFindByPublicId_thenReturnUser() {
        when(userRepository.findByPublicId(USER_UUID))
                .thenReturn(Optional.of(USER));

        var result = userService.findByPublicId(USER_UUID);

        assertThat(result)
                .isNotNull()
                .isEqualTo(USER);
        verify(userRepository).findByPublicId(USER_UUID);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void givenInvalidPublicId_whenFindByPublicId_thenThrowUserNotFoundException() {
        when(userRepository.findByPublicId(USER_UUID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByPublicId(USER_UUID))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByPublicId(USER_UUID);
        verifyNoMoreInteractions(userRepository);
    }
}