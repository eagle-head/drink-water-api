package br.com.drinkwater.usermanagement.service;

import static br.com.drinkwater.usermanagement.constants.UserTestConstants.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.com.drinkwater.usermanagement.exception.UserAlreadyExistsException;
import br.com.drinkwater.usermanagement.exception.UserNotFoundException;
import br.com.drinkwater.usermanagement.mapper.UserMapper;
import br.com.drinkwater.usermanagement.repository.UserRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
final class UserServiceTest {

    @Mock private UserRepository userRepository;

    @Mock private UserMapper userMapper;

    private final MeterRegistry meterRegistry = new SimpleMeterRegistry();

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, userMapper, meterRegistry);
    }

    @Test
    void givenValidUserData_WhenCreateUser_ThenReturnsUserResponseDTO() {
        // Given
        when(userRepository.existsByPublicId(USER_UUID)).thenReturn(false);
        when(userMapper.toEntity(USER_DTO, USER_UUID)).thenReturn(USER);
        when(userRepository.save(USER)).thenReturn(USER);
        when(userMapper.toDto(USER)).thenReturn(USER_RESPONSE_DTO);

        // When
        var sut = this.userService.createUser(USER_UUID, USER_DTO);

        // Then
        assertThat(sut).isEqualTo(USER_RESPONSE_DTO);
        verify(userRepository).existsByPublicId(USER_UUID);
        verify(userMapper).toEntity(USER_DTO, USER_UUID);
        verify(userRepository).save(USER);
        verify(userMapper).toDto(USER);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void givenExistingPublicId_whenCreateUser_thenThrowUserAlreadyExistsException() {
        // Given
        when(userRepository.existsByPublicId(USER_UUID)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> this.userService.createUser(USER_UUID, USER_DTO))
                .isInstanceOf(UserAlreadyExistsException.class);

        verify(userRepository).existsByPublicId(USER_UUID);
        verify(userMapper, never()).toEntity(any(), any());
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toDto(any());
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void givenValidPublicId_whenGetUserByPublicId_thenReturnUserResponseDTO() {
        // Given
        when(userRepository.findByPublicId(USER_UUID)).thenReturn(Optional.of(USER));
        when(userMapper.toDto(USER)).thenReturn(USER_RESPONSE_DTO);

        // When
        var sut = this.userService.getUserByPublicId(USER_UUID);

        // Then
        assertThat(sut).isEqualTo(USER_RESPONSE_DTO);
        verify(userRepository).findByPublicId(USER_UUID);
        verify(userMapper).toDto(USER);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void givenInvalidPublicId_whenGetUserByPublicId_thenThrowUserNotFoundException() {
        // Given
        when(userRepository.findByPublicId(USER_UUID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> this.userService.getUserByPublicId(USER_UUID))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByPublicId(USER_UUID);
        verify(userMapper, never()).toDto(any());
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void givenValidUserAndUpdateData_whenUpdateUser_thenReturnUpdatedUserDTO() {
        // Given
        when(userRepository.findByPublicId(USER_UUID)).thenReturn(Optional.of(USER));
        when(userMapper.updateUser(USER, USER_DTO)).thenReturn(USER);
        when(userRepository.save(USER)).thenReturn(USER);
        when(userMapper.toDto(USER)).thenReturn(USER_RESPONSE_DTO);

        // When
        var sut = this.userService.updateUser(USER_UUID, USER_DTO);

        // Then
        assertThat(sut).isEqualTo(USER_RESPONSE_DTO);
        verify(userRepository).findByPublicId(USER_UUID);
        verify(userMapper).updateUser(USER, USER_DTO);
        verify(userRepository).save(USER);
        verify(userMapper).toDto(USER);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void givenInvalidPublicId_whenUpdateUser_thenThrowUserNotFoundException() {
        // Given
        when(userRepository.findByPublicId(USER_UUID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> this.userService.updateUser(USER_UUID, USER_DTO))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByPublicId(USER_UUID);
        verify(userMapper, never()).updateUser(any(), any());
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toDto(any());
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void givenValidPublicId_whenDeleteUser_thenByPublicIdShouldBeDeleted() {
        // When & Then
        assertThatCode(() -> this.userService.deleteByPublicId(USER_UUID))
                .doesNotThrowAnyException();

        verify(userRepository).deleteByPublicId(USER_UUID);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void givenValidPublicId_whenFindByPublicId_thenReturnUser() {
        // Given
        when(userRepository.findByPublicId(USER_UUID)).thenReturn(Optional.of(USER));

        // When
        var sut = this.userService.findByPublicId(USER_UUID);

        // Then
        assertThat(sut).isNotNull().isEqualTo(USER);
        verify(userRepository).findByPublicId(USER_UUID);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void givenInvalidPublicId_whenFindByPublicId_thenThrowUserNotFoundException() {
        // Given
        when(userRepository.findByPublicId(USER_UUID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> this.userService.findByPublicId(USER_UUID))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByPublicId(USER_UUID);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void givenValidPublicId_whenResolveUserIdByPublicId_thenReturnUserId() {
        // Given
        when(userRepository.findByPublicId(USER_UUID)).thenReturn(Optional.of(USER));

        // When
        var sut = this.userService.resolveUserIdByPublicId(USER_UUID);

        // Then
        assertThat(sut).isEqualTo(USER_ID);
        verify(userRepository).findByPublicId(USER_UUID);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void givenInvalidPublicId_whenResolveUserIdByPublicId_thenThrowUserNotFoundException() {
        // Given
        when(userRepository.findByPublicId(USER_UUID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> this.userService.resolveUserIdByPublicId(USER_UUID))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByPublicId(USER_UUID);
        verifyNoMoreInteractions(userRepository);
    }
}
