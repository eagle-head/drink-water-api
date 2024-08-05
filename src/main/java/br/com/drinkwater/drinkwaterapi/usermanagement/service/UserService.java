package br.com.drinkwater.drinkwaterapi.usermanagement.service;

import br.com.drinkwater.drinkwaterapi.usermanagement.dto.UserCreateDTO;
import br.com.drinkwater.drinkwaterapi.usermanagement.mapper.UserMapper;
import br.com.drinkwater.drinkwaterapi.usermanagement.dto.UserResponseDTO;
import br.com.drinkwater.drinkwaterapi.usermanagement.exception.EmailAlreadyUsedException;
import br.com.drinkwater.drinkwaterapi.usermanagement.model.User;
import br.com.drinkwater.drinkwaterapi.usermanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;

    public UserService(UserRepository userRepository, UserMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Transactional
    public UserResponseDTO create(UserCreateDTO userCreateDTO) {
        boolean emailExists = userRepository.existsByEmail(userCreateDTO.email());
        if (emailExists) {
            throw new EmailAlreadyUsedException("The email provided is already in use.");
        }

        User newUser = mapper.convertToEntity(userCreateDTO);
        User savedUser = userRepository.save(newUser);

        return mapper.convertToDTO(savedUser);
    }

    @Transactional(readOnly = true)
    public Optional<UserResponseDTO> findById(Long id) {
        return userRepository.findById(id)
                .map(mapper::convertToDTO);
    }

    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public Optional<UserResponseDTO> update(Long id, User user) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    user.setId(existingUser.getId());
                    User updatedUser = userRepository.save(user);
                    return mapper.convertToDTO(updatedUser);
                });
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }
}
