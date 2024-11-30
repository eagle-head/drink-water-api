package br.com.drinkwater.hydrationtracking.repository;

import br.com.drinkwater.hydrationtracking.model.WaterIntake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public interface WaterIntakeRepository extends JpaRepository<WaterIntake, Long>, JpaSpecificationExecutor<WaterIntake> {

    Optional<WaterIntake> findByIdAndUser_Id(Long id, Long userId);

    void deleteByIdAndUser_Id(Long id, Long userId);

    boolean existsByDateTimeUTCAndUser_IdAndIdNot(OffsetDateTime dateTimeUTC, Long userId, Long id);
}
