package br.com.drinkwater.hydrationtracking.specification;

import br.com.drinkwater.hydrationtracking.dto.WaterIntakeFilterDTO;
import br.com.drinkwater.hydrationtracking.model.WaterIntake;
import br.com.drinkwater.usermanagement.model.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

/**
 * Utility class to create specifications for dynamic WaterIntake queries
 */
public final class WaterIntakeSpecification {

    private WaterIntakeSpecification() {
    }

    /**
     * Creates a specification for filtering water intake records
     */
    public static Specification<WaterIntake> buildSpecification(WaterIntakeFilterDTO filter, User user) {
        return Specification
                .where(belongsToUser(user))
                .and(betweenDates(filter.startDate(), filter.endDate()))
                .and(hasMinVolume(filter.minVolume()))
                .and(hasMaxVolume(filter.maxVolume()));
    }

    private static Specification<WaterIntake> belongsToUser(User user) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user").get("id"), user.getId());
    }

    private static Specification<WaterIntake> betweenDates(Instant startDate, Instant endDate) {
        return (root, query, criteriaBuilder) -> {
            // Skip this filter if any date is null
            if (startDate == null || endDate == null) return null;

            return criteriaBuilder.between(root.get("dateTimeUTC"), startDate, endDate);
        };
    }

    private static Specification<WaterIntake> hasMinVolume(Integer minVolume) {
        return (root, query, criteriaBuilder) -> {
            // Skip this filter if minVolume is null
            if (minVolume == null) return null;

            return criteriaBuilder.greaterThanOrEqualTo(root.get("volume"), minVolume);
        };
    }

    private static Specification<WaterIntake> hasMaxVolume(Integer maxVolume) {
        return (root, query, criteriaBuilder) -> {
            // Skip this filter if maxVolume is null
            if (maxVolume == null) return null;

            return criteriaBuilder.lessThanOrEqualTo(root.get("volume"), maxVolume);
        };
    }
}