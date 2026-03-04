package br.com.drinkwater.usermanagement.repository;

import br.com.drinkwater.usermanagement.model.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Spring Data JDBC repository for {@link User} entities, keyed by Keycloak public ID. */
@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    /**
     * Finds a user by their Keycloak public ID.
     *
     * @param publicId the Keycloak public ID
     * @return the user if found, or empty
     */
    Optional<User> findByPublicId(UUID publicId);

    /**
     * Checks whether a user exists with the given Keycloak public ID.
     *
     * @param publicId the Keycloak public ID
     * @return {@code true} if a user exists
     */
    boolean existsByPublicId(UUID publicId);

    /**
     * Deletes a user by their Keycloak public ID. This is a no-op if no user exists.
     *
     * @param publicId the Keycloak public ID
     */
    @Modifying
    @Query("DELETE FROM users WHERE public_id = :publicId")
    void deleteByPublicId(@Param("publicId") UUID publicId);
}
