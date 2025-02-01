package br.com.drinkwater.usermanagement.service;

import br.com.drinkwater.usermanagement.dto.UserResponseDTO;
import br.com.drinkwater.usermanagement.dto.UserDTO;
import br.com.drinkwater.usermanagement.exception.UserAlreadyExistsException;
import br.com.drinkwater.usermanagement.exception.UserNotFoundException;
import br.com.drinkwater.usermanagement.mapper.UserMapper;
import br.com.drinkwater.usermanagement.model.User;
import br.com.drinkwater.usermanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    public UserResponseDTO createUser(UUID publicId, UserDTO userDTO) {
        this.validateUserExistence(publicId);
        User userEntity = this.userMapper.toEntity(userDTO);
        User savedUser = this.userRepository.save(userEntity);

        return this.userMapper.toDto(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserByPublicId(UUID publicId) {
        User existingUser = this.findByPublicId(publicId);
        return this.userMapper.toDto(existingUser);
    }

    @Transactional
    public UserResponseDTO updateUser(UUID publicId, UserDTO updateUserDTO) {
        User existingUser = this.findByPublicId(publicId);
        this.userMapper.updateUserFromDTO(existingUser, updateUserDTO);
        User updatedUser = this.userRepository.save(existingUser);

        return this.userMapper.toDto(updatedUser);
    }

    @Transactional
    public void deleteByPublicId(UUID publicId) {
        this.userRepository.deleteByPublicId(publicId);
    }

    @Transactional(readOnly = true)
    public User findByPublicId(UUID publicId) {
        return this.userRepository.findByPublicId(publicId).orElseThrow(UserNotFoundException::new);
    }

    @Transactional(readOnly = true)
    protected void validateUserExistence(UUID publicId) {
        if (this.userRepository.existsByPublicId(publicId)) {
            throw new UserAlreadyExistsException();
        }
    }
}
