package br.com.drinkwater.usermanagement.service;

import br.com.drinkwater.usermanagement.dto.UserUpdateDTO;
import br.com.drinkwater.usermanagement.exception.EmailAlreadyUsedException;
import br.com.drinkwater.usermanagement.exception.UserNotFoundException;
import br.com.drinkwater.usermanagement.mapper.UserMapper;
import br.com.drinkwater.usermanagement.model.User;
import br.com.drinkwater.usermanagement.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAuthority('SCOPE_free:create')")
    @Transactional
    public User create(User user) {
        if (this.userRepository.existsByPublicId(user.getPublicId())) {
            throw new EmailAlreadyUsedException();
        }

        return this.userRepository.save(user);
    }

    @PreAuthorize("hasAuthority('SCOPE_free:read')")
    @Transactional(readOnly = true)
    public User findByPublicId(UUID publicId) {
        return this.userRepository.findByPublicId(publicId).orElseThrow(UserNotFoundException::new);
    }

    @PreAuthorize("hasAuthority('SCOPE_free:update')")
    @Transactional
    public User update(UUID publicId, UserUpdateDTO userUpdateDTO, String email) {
        User existingUser = this.findByPublicId(publicId);

        this.userMapper.toEntity(userUpdateDTO, existingUser, existingUser.getPublicId(), email);

        return this.userRepository.save(existingUser);
    }

    @PreAuthorize("hasAuthority('SCOPE_free:delete')")
    @Transactional
    public void delete(UUID publicId) {
        User user = this.findByPublicId(publicId);
        this.userRepository.delete(user);
    }
}
