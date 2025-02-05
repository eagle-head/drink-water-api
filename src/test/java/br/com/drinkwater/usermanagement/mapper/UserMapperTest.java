package br.com.drinkwater.usermanagement.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.drinkwater.usermanagement.constants.UserTestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

public class UserMapperTest {

    private UserMapper mapper;

    @BeforeEach
    public void setUp() {
        PersonalMapper personalMapper = new PersonalMapper();
        PhysicalMapper physicalMapper = new PhysicalMapper();
        AlarmSettingsMapper alarmSettingsMapper = new AlarmSettingsMapper();
        this.mapper = new UserMapper(personalMapper, physicalMapper, alarmSettingsMapper);
    }

    // Testes para o método toEntity(UserDTO dto)

    @Test
    void givenValidUserDTO_whenToEntity_thenShouldReturnUserWithGeneratedUUIDAndConvertedSubcomponents() {
        var sut = this.mapper.toEntity(USER_DTO, USER_UUID);

        assertThat(sut)
                .usingRecursiveComparison()
                .ignoringFields("waterIntakes", "createdAt", "updatedAt", "settings.user")
                .isEqualTo(USER);
    }

    @Test
    void givenNullUserDTO_whenToEntity_thenShouldReturnNull() {
        var sut = this.mapper.toEntity(null, USER_UUID);

        assertThat(sut).isNull();
    }

    // Testes para o método toDto(User entity)

    @Test
    void givenValidUserEntity_whenToDto_thenShouldReturnUserResponseDTOWithConvertedSubcomponents() {
        var sut = this.mapper.toDto(USER);

        assertThat(sut).isNotNull().isEqualTo(USER_RESPONSE_DTO);
    }

    @Test
    void givenNullUserEntity_whenToDto_thenShouldReturnNull() {
        var sut = this.mapper.toDto(null);

        assertThat(sut).isNull();
    }

    // Testes para o método updateUserFromDTO(User user, UserDTO userDTO)

    @Test
    void givenValidUserAndUserDTO_whenUpdateUserFromDTO_thenShouldUpdateAllFields() {
    }

    @Test
    void givenUserWithExistingAlarmSettings_whenUpdateUserFromDTO_thenShouldUpdateExistingAlarmSettings() {
        // TODO: Implementar teste para atualização de AlarmSettings existentes
    }

    @Test
    void givenUserDTOWithNullAlarmSettings_whenUpdateUserFromDTO_thenShouldSetUserSettingsToNull() {
        // TODO: Implementar teste para DTO com AlarmSettings nulo
    }

    @Test
    void givenNullUser_whenUpdateUserFromDTO_thenShouldNotThrowException() {
        // TODO: Implementar teste para parâmetro user nulo
    }

    @Test
    void givenNullUserDTO_whenUpdateUserFromDTO_thenShouldNotThrowException() {
        // TODO: Implementar teste para parâmetro userDTO nulo
    }
}
