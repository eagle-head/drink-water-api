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
        when(this.userRepository.existsByPublicId(USER_UUID))
                .thenReturn(false);
        when(this.userMapper.toEntity(USER_DTO))
                .thenReturn(USER);
        when(this.userRepository.save(USER))
                .thenReturn(USER);
        when(this.userMapper.toDto(USER))
                .thenReturn(USER_RESPONSE_DTO);

        var sut = this.userService.createUser(USER_UUID, USER_DTO);

        assertThat(sut).isEqualTo(USER_RESPONSE_DTO);
        verify(this.userRepository).existsByPublicId(USER_UUID);
        verify(this.userMapper).toEntity(USER_DTO);
        verify(this.userRepository).save(USER);
        verify(this.userMapper).toDto(USER);
        verifyNoMoreInteractions(this.userRepository, this.userMapper);
    }

    @Test
    public void givenExistingPublicId_whenCreateUser_thenThrowEmailAlreadyUsedException() {
        when(this.userRepository.existsByPublicId(USER_UUID))
                .thenReturn(true);

        assertThatThrownBy(() -> this.userService.createUser(USER_UUID, USER_DTO))
                .isInstanceOf(UserAlreadyExistsException.class);

        verify(this.userRepository).existsByPublicId(USER_UUID);
        verify(this.userMapper, never()).toEntity(any());
        verify(this.userRepository, never()).save(any());
        verify(this.userMapper, never()).toDto(any());
        verifyNoMoreInteractions(this.userRepository, this.userMapper);
    }

    @Test
    public void givenValidPublicId_whenGetUserByPublicId_thenReturnUserResponseDTO() {
        when(this.userRepository.findByPublicId(USER_UUID))
                .thenReturn(Optional.of(USER));
        when(this.userMapper.toDto(USER))
                .thenReturn(USER_RESPONSE_DTO);

        var sut = this.userService.getUserByPublicId(USER_UUID);

        assertThat(sut).isEqualTo(USER_RESPONSE_DTO);
        verify(this.userRepository).findByPublicId(USER_UUID);
        verify(this.userMapper).toDto(USER);
        verifyNoMoreInteractions(this.userRepository, this.userMapper);
    }

    @Test
    public void givenInvalidPublicId_whenGetUserByPublicId_thenThrowUserNotFoundException() {
        when(this.userRepository.findByPublicId(USER_UUID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.userService.getUserByPublicId(USER_UUID))
                .isInstanceOf(UserNotFoundException.class);

        verify(this.userRepository).findByPublicId(USER_UUID);
        verify(this.userMapper, never()).toDto(any());
        verifyNoMoreInteractions(this.userRepository, this.userMapper);
    }

    @Test
    public void givenValidUserAndUpdateData_whenUpdateUser_thenReturnUpdatedUserDTO() {
        when(this.userRepository.findByPublicId(USER_UUID))
                .thenReturn(Optional.of(USER));
        when(this.userRepository.save(USER))
                .thenReturn(USER);
        when(this.userMapper.toDto(USER))
                .thenReturn(USER_RESPONSE_DTO);

        var sut = this.userService.updateUser(USER_UUID, USER_DTO);

        assertThat(sut).isEqualTo(USER_RESPONSE_DTO);
        verify(this.userRepository).findByPublicId(USER_UUID);
        verify(this.userMapper).updateUserFromDTO(USER, USER_DTO);
        verify(this.userRepository).save(USER);
        verify(this.userMapper).toDto(USER);
        verifyNoMoreInteractions(this.userRepository, this.userMapper);
    }

    @Test
    public void givenInvalidPublicId_whenUpdateUser_thenThrowUserNotFoundException() {
        when(this.userRepository.findByPublicId(USER_UUID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.userService.updateUser(USER_UUID, USER_DTO))
                .isInstanceOf(UserNotFoundException.class);

        verify(this.userRepository).findByPublicId(USER_UUID);
        verify(this.userMapper, never()).updateUserFromDTO(any(), any());
        verify(this.userRepository, never()).save(any());
        verify(this.userMapper, never()).toDto(any());
        verifyNoMoreInteractions(this.userRepository, this.userMapper);
    }

    @Test
    public void givenValidPublicId_whenDeleteUser_thenByPublicIdShouldBeDeleted() {
        assertThatCode(() -> this.userService.deleteByPublicId(USER_UUID))
                .doesNotThrowAnyException();

        verify(this.userRepository).deleteByPublicId(USER_UUID);
        verifyNoMoreInteractions(this.userRepository);
    }

    @Test
    public void givenValidPublicId_whenFindByPublicId_thenReturnUser() {
        when(this.userRepository.findByPublicId(USER_UUID))
                .thenReturn(Optional.of(USER));

        var sut = this.userService.findByPublicId(USER_UUID);

        assertThat(sut)
                .isNotNull()
                .isEqualTo(USER);
        verify(this.userRepository).findByPublicId(USER_UUID);
        verifyNoMoreInteractions(this.userRepository);
    }

    @Test
    public void givenInvalidPublicId_whenFindByPublicId_thenThrowUserNotFoundException() {
        when(this.userRepository.findByPublicId(USER_UUID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.userService.findByPublicId(USER_UUID))
                .isInstanceOf(UserNotFoundException.class);

        verify(this.userRepository).findByPublicId(USER_UUID);
        verifyNoMoreInteractions(this.userRepository);
    }
}