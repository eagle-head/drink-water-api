package br.com.drinkwater.hydrationtracking.specification;

import br.com.drinkwater.hydrationtracking.dto.WaterIntakeFilterDTO;
import br.com.drinkwater.hydrationtracking.model.WaterIntake;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;

public final class WaterIntakeSpecification {

    private WaterIntakeSpecification() {
    }

    public static Specification<WaterIntake> withFilters(WaterIntakeFilterDTO filter, Long userId) {
        return Specification.where(byUserId(userId))
                .and(byDateRange(filter.startDate(), filter.endDate()))
                .and(byVolumeRange(filter.minVolume(), filter.maxVolume()));
    }

    private static Specification<WaterIntake> byUserId(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    private static Specification<WaterIntake> byDateRange(OffsetDateTime startDate, OffsetDateTime endDate) {
        return (root, query, cb) -> {
            if (startDate == null && endDate == null) {
                return null;
            }

            if (startDate != null && endDate != null) {
                return cb.between(root.get("dateTimeUTC"), startDate, endDate);
            }

            if (startDate != null) {
                return cb.greaterThanOrEqualTo(root.get("dateTimeUTC"), startDate);
            }

            return cb.lessThanOrEqualTo(root.get("dateTimeUTC"), endDate);
        };
    }

    private static Specification<WaterIntake> byVolumeRange(Integer minVolume, Integer maxVolume) {
        return (root, query, cb) -> {
            if (minVolume == null && maxVolume == null) {
                return null;
            }

            if (minVolume != null && maxVolume != null) {
                return cb.between(root.get("volume"), minVolume, maxVolume);
            }

            if (minVolume != null) {
                return cb.greaterThanOrEqualTo(root.get("volume"), minVolume);
            }

            return cb.lessThanOrEqualTo(root.get("volume"), maxVolume);
        };
    }
}