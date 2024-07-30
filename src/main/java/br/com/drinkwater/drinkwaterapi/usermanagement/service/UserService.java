package br.com.drinkwater.drinkwaterapi.usermanagement.service;

import br.com.drinkwater.drinkwaterapi.usermanagement.exception.EmailAlreadyUsedException;
import br.com.drinkwater.drinkwaterapi.usermanagement.model.User;
import br.com.drinkwater.drinkwaterapi.usermanagement.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(User user) {
        boolean emailExists = userRepository.existsByEmail(user.getEmail());
        if (emailExists) {
            throw new EmailAlreadyUsedException("The email provided is already in use.");
        }

        return userRepository.save(user);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> update(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setEmail(updatedUser.getEmail());
                    existingUser.setPassword(updatedUser.getPassword());
                    existingUser.setFirstName(updatedUser.getFirstName());
                    existingUser.setLastName(updatedUser.getLastName());
                    existingUser.setBirthDate(updatedUser.getBirthDate());
                    existingUser.setBiologicalSex(updatedUser.getBiologicalSex());
                    existingUser.setWeight(updatedUser.getWeight());
                    existingUser.setWeightUnit(updatedUser.getWeightUnit());
                    existingUser.setHeight(updatedUser.getHeight());
                    existingUser.setHeightUnit(updatedUser.getHeightUnit());
                    return userRepository.save(existingUser);
                });
    }
}
