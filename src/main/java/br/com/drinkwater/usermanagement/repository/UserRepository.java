package br.com.drinkwater.usermanagement.repository;

import br.com.drinkwater.usermanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPublicId(UUID publicId);

    boolean existsByPublicId(UUID publicId);

    void deleteByPublicId(UUID publicId);
}
