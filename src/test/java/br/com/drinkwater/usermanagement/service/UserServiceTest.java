package br.com.drinkwater.usermanagement.service;

import br.com.drinkwater.usermanagement.mapper.UserMapper;
import br.com.drinkwater.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static br.com.drinkwater.usermanagement.constants.ResponseUserDTOConstants.JOHN_DOE_RESPONSE_DTO;
import static br.com.drinkwater.usermanagement.constants.UserConstants.JOHN_DOE;
import static br.com.drinkwater.usermanagement.constants.UserDTOConstants.JOHN_DOE_DTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Test
    public void givenValidUserData_WhenCreateUser_ThenReturnsResponseUserDTO() {

        // Arrange
        when(this.userMapper.toEntity(JOHN_DOE_DTO)).thenReturn(JOHN_DOE);
        when(this.userRepository.save(JOHN_DOE)).thenReturn(JOHN_DOE);
        when(this.userMapper.toDto(JOHN_DOE)).thenReturn(JOHN_DOE_RESPONSE_DTO);

        // Act
        var actualResponse = this.userService.createUser(JOHN_DOE.getPublicId(), JOHN_DOE_DTO);

        // Assert
        assertThat(actualResponse).isEqualTo(JOHN_DOE_RESPONSE_DTO);
        verify(this.userMapper).toEntity(JOHN_DOE_DTO);
        verify(this.userRepository).save(JOHN_DOE);
        verify(this.userMapper).toDto(JOHN_DOE);
    }
}
