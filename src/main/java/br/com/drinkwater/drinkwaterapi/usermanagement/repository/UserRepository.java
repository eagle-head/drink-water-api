package br.com.drinkwater.drinkwaterapi.usermanagement.repository;

import br.com.drinkwater.drinkwaterapi.usermanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
}
