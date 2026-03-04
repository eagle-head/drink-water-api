package br.com.drinkwater.usermanagement.service;

import br.com.drinkwater.usermanagement.dto.UserDTO;
import br.com.drinkwater.usermanagement.dto.UserResponseDTO;
import br.com.drinkwater.usermanagement.exception.UserAlreadyExistsException;
import br.com.drinkwater.usermanagement.exception.UserNotFoundException;
import br.com.drinkwater.usermanagement.mapper.UserMapper;
import br.com.drinkwater.usermanagement.model.User;
import br.com.drinkwater.usermanagement.repository.UserRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for user profile lifecycle operations.
 *
 * <p>Manages creation, retrieval, update, and deletion of user profiles. Resolves the Keycloak
 * public ID to an internal database ID using a Caffeine-backed cache. Publishes Micrometer metrics
 * for user creation and deletion counts.
 */
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final Counter usersCreatedCounter;
    private final Counter usersDeletedCounter;

    public UserService(
            UserRepository userRepository, UserMapper userMapper, MeterRegistry meterRegistry) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.usersCreatedCounter =
                Counter.builder("users.created")
                        .description("Total number of users created")
                        .register(meterRegistry);
        this.usersDeletedCounter =
                Counter.builder("users.deleted")
                        .description("Total number of users deleted")
                        .register(meterRegistry);
    }

    /**
     * Creates a new user profile.
     *
     * @param publicId the Keycloak public ID for the new user
     * @param userDTO the validated user data
     * @return the created user profile as a response DTO
     * @throws UserAlreadyExistsException if a user already exists with the given public ID
     */
    @Transactional
    public UserResponseDTO createUser(UUID publicId, UserDTO userDTO) {
        log.info("Creating user with publicId: {}", publicId);
        this.validateUserExistence(publicId);
        User userEntity = this.userMapper.toEntity(userDTO, publicId);
        User savedUser = this.userRepository.save(userEntity);
        this.usersCreatedCounter.increment();
        log.info("User created successfully with publicId: {}", publicId);

        return this.userMapper.toDto(savedUser);
    }

    /**
     * Retrieves a user profile by their Keycloak public ID.
     *
     * @param publicId the Keycloak public ID
     * @return the user profile as a response DTO
     * @throws UserNotFoundException if no user exists with the given public ID
     */
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByPublicId(UUID publicId) {
        log.debug("Fetching user by publicId: {}", publicId);
        User existingUser = this.findByPublicId(publicId);
        return this.userMapper.toDto(existingUser);
    }

    /**
     * Updates an existing user profile.
     *
     * @param publicId the Keycloak public ID of the user to update
     * @param updateUserDTO the validated updated user data
     * @return the updated user profile as a response DTO
     * @throws UserNotFoundException if no user exists with the given public ID
     */
    @Transactional
    public UserResponseDTO updateUser(UUID publicId, UserDTO updateUserDTO) {
        log.info("Updating user with publicId: {}", publicId);
        User existingUser = this.findByPublicId(publicId);
        User updatedUser = this.userMapper.updateUser(existingUser, updateUserDTO);
        User savedUser = this.userRepository.save(updatedUser);
        log.info("User updated successfully with publicId: {}", publicId);

        return this.userMapper.toDto(savedUser);
    }

    /**
     * Deletes a user by their public ID.
     *
     * <p>This operation is idempotent: if no record matches the given {@code publicId}, the method
     * completes successfully without throwing an exception, and the controller returns 204 No
     * Content regardless.
     */
    @CacheEvict(value = "userIdByPublicId", key = "#publicId")
    @Transactional
    public void deleteByPublicId(UUID publicId) {
        log.info("Deleting user with publicId: {}", publicId);
        this.userRepository.deleteByPublicId(publicId);
        this.usersDeletedCounter.increment();
        log.info("User deleted with publicId: {}", publicId);
    }

    /**
     * Resolves a Keycloak public ID to the internal database user ID. Results are cached in the
     * {@code userIdByPublicId} Caffeine cache since this mapping is immutable.
     *
     * @param publicId the Keycloak public ID
     * @return the internal database user ID
     * @throws UserNotFoundException if no user exists with the given public ID
     */
    @Cacheable("userIdByPublicId")
    @Transactional(readOnly = true)
    public Long resolveUserIdByPublicId(UUID publicId) {
        log.debug("Cache miss - resolving userId for publicId: {}", publicId);
        return Objects.requireNonNull(
                this.findByPublicId(publicId).getId(), "Persisted user must have a non-null ID");
    }

    /**
     * Finds the user entity by their Keycloak public ID.
     *
     * @param publicId the Keycloak public ID
     * @return the user entity
     * @throws UserNotFoundException if no user exists with the given public ID
     */
    @Transactional(readOnly = true)
    public User findByPublicId(UUID publicId) {
        log.debug("Looking up user by publicId: {}", publicId);
        return this.userRepository.findByPublicId(publicId).orElseThrow(UserNotFoundException::new);
    }

    private void validateUserExistence(UUID publicId) {
        if (this.userRepository.existsByPublicId(publicId)) {
            log.warn("User already exists with publicId: {}", publicId);
            throw new UserAlreadyExistsException();
        }
    }
}
