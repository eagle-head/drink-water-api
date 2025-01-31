package br.com.drinkwater.usermanagement.service;

import br.com.drinkwater.usermanagement.exception.EmailAlreadyUsedException;
import br.com.drinkwater.usermanagement.mapper.UserMapper;
import br.com.drinkwater.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static br.com.drinkwater.usermanagement.constants.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Test
    public void givenValidUserData_WhenCreateUser_ThenReturnsUserResponseDTO() {
        // Arrange
        when(this.userMapper.toEntity(DEFAULT_USER_DTO))
                .thenReturn(DEFAULT_USER);
        when(this.userRepository.save(DEFAULT_USER))
                .thenReturn(DEFAULT_USER);
        when(this.userMapper.toDto(DEFAULT_USER))
                .thenReturn(DEFAULT_USER_RESPONSE_DTO);

        // Act
        var actualResponse = this.userService.createUser(DEFAULT_UUID, DEFAULT_USER_DTO);

        // Assert
        assertThat(actualResponse).isEqualTo(DEFAULT_USER_RESPONSE_DTO);
        verify(this.userMapper).toEntity(DEFAULT_USER_DTO);
        verify(this.userRepository).save(DEFAULT_USER);
        verify(this.userMapper).toDto(DEFAULT_USER);
    }

    @Test
    public void givenExistingPublicId_whenCreateUser_thenThrowEmailAlreadyUsedException() {

        when(this.userRepository.existsByPublicId(DEFAULT_UUID))
                .thenReturn(true);

        assertThatThrownBy(() -> this.userService.createUser(DEFAULT_UUID, DEFAULT_USER_DTO))
                .isInstanceOf(EmailAlreadyUsedException.class);

        verify(this.userRepository).existsByPublicId(DEFAULT_UUID);
    }

    @Test
    public void givenValidPublicId_whenGetUserByPublicId_thenReturnUserResponseDTO() {

        when(this.userRepository.findByPublicId(DEFAULT_UUID))
                .thenReturn(Optional.of(DEFAULT_USER));
        when(this.userMapper.toDto(DEFAULT_USER))
                .thenReturn(DEFAULT_USER_RESPONSE_DTO);

        var actualResponse = this.userService.getUserByPublicId(DEFAULT_UUID);

        assertThat(actualResponse).isEqualTo(DEFAULT_USER_RESPONSE_DTO);
        verify(this.userRepository).findByPublicId(DEFAULT_UUID);
        verify(this.userMapper).toDto(DEFAULT_USER);
    }

    @Test
    public void givenInvalidPublicId_whenGetUserByPublicId_thenThrowUserNotFoundException() {
        throw new RuntimeException("Test not implemented");
    }

    @Test
    public void givenValidUserAndUpdateData_whenUpdateUser_thenReturnUpdatedUserDTO() {
        throw new RuntimeException("Test not implemented");
    }

    @Test
    public void givenInvalidPublicId_whenUpdateUser_thenThrowUserNotFoundException() {
        throw new RuntimeException("Test not implemented");
    }

    @Test
    public void givenValidPublicId_whenDeleteUser_thenUserShouldBeDeleted() {
        throw new RuntimeException("Test not implemented");
    }

    @Test
    public void givenInvalidPublicId_whenDeleteUser_thenThrowUserNotFoundException() {
        throw new RuntimeException("Test not implemented");
    }

    @Test
    public void givenValidPublicId_whenFindByPublicId_thenReturnUser() {
        throw new RuntimeException("Test not implemented");
    }

    @Test
    public void givenInvalidPublicId_whenFindByPublicId_thenThrowUserNotFoundException() {
        throw new RuntimeException("Test not implemented");
    }

    @Test
    public void givenNonExistingPublicId_whenValidateUserExistence_thenShouldNotThrowException() {
        throw new RuntimeException("Test not implemented");
    }

    @Test
    public void givenExistingPublicId_whenValidateUserExistence_thenThrowEmailAlreadyUsedException() {
        throw new RuntimeException("Test not implemented");
    }
}