package br.com.drinkwater.usermanagement.service;

import br.com.drinkwater.usermanagement.dto.UserUpdateDTO;
import br.com.drinkwater.usermanagement.exception.EmailAlreadyUsedException;
import br.com.drinkwater.usermanagement.exception.UserNotFoundException;
import br.com.drinkwater.usermanagement.mapper.UserMapper;
import br.com.drinkwater.usermanagement.model.User;
import br.com.drinkwater.usermanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final IdentityProviderService identityProviderService;

    public UserService(UserRepository userRepository, UserMapper userMapper, IdentityProviderService identityProviderService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.identityProviderService = identityProviderService;
    }

    @Transactional
    public User create(User user, String password) {
        var emailExists = this.existsByEmail(user.getEmail());
        if (emailExists) {
            throw new EmailAlreadyUsedException();
        }

        this.identityProviderService.register(user, password);

        return this.userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return this.userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public User update(String userEmail, UserUpdateDTO userUpdateDTO) {
        var existingUser = this.findByEmail(userEmail);
        this.userMapper.toEntity(userUpdateDTO, existingUser);

        return this.userRepository.save(existingUser);
    }

    @Transactional
    public void delete(String email) {
        this.userRepository.findByEmail(email).ifPresent(this.userRepository::delete);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }
}
