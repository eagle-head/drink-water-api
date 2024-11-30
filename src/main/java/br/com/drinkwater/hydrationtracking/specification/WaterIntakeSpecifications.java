package br.com.drinkwater.hydrationtracking.specification;

import br.com.drinkwater.hydrationtracking.model.VolumeUnit;
import br.com.drinkwater.hydrationtracking.model.WaterIntake;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;

public class WaterIntakeSpecifications {

    public static Specification<WaterIntake> userIdEqual(Long userId) {
        return (root, query, builder) -> builder.equal(root.get("user").get("id"), userId);
    }

    public static Specification<WaterIntake> dateTimeBetween(OffsetDateTime startDate, OffsetDateTime endDate) {
        return (root, query, builder) -> {
            if (startDate != null && endDate != null) {
                return builder.between(root.get("dateTimeUTC"), startDate, endDate);
            } else if (startDate != null) {
                return builder.greaterThanOrEqualTo(root.get("dateTimeUTC"), startDate);
            } else if (endDate != null) {
                return builder.lessThanOrEqualTo(root.get("dateTimeUTC"), endDate);
            } else {
                return null;
            }
        };
    }

    public static Specification<WaterIntake> volumeBetween(Integer minVolume, Integer maxVolume) {
        return (root, query, builder) -> {
            if (minVolume != null && maxVolume != null) {
                return builder.between(root.get("volume"), minVolume, maxVolume);
            } else if (minVolume != null) {
                return builder.greaterThanOrEqualTo(root.get("volume"), minVolume);
            } else if (maxVolume != null) {
                return builder.lessThanOrEqualTo(root.get("volume"), maxVolume);
            } else {
                return null;
            }
        };
    }

    public static Specification<WaterIntake> volumeUnitEqual(VolumeUnit volumeUnit) {
        return (root, query, builder) -> {
            if (volumeUnit != null) {
                return builder.equal(root.get("volumeUnit"), volumeUnit);
            } else {
                return null;
            }
        };
    }
}
