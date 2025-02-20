package br.com.drinkwater.core.validation.date;

import java.time.Duration;
import java.util.Set;

public record DateTimeValidationRules(
        boolean allowMilliseconds,
        boolean requirePast,
        boolean requireFuture,
        boolean requireSameDay,
        Integer minimumMinutesInterval,
        Integer minimumAge,
        Set<String> allowedTimeZones,
        Duration maxTimeRange
) {
    public static DateTimeValidationRules forHydrationTracking() {
        return new DateTimeValidationRules(
                false,
                false,
                false,
                false,
                15,
                null,
                Set.of("UTC"),
                Duration.ofDays(30)
        );
    }

    public static DateTimeValidationRules forAlarmSettings() {
        return new DateTimeValidationRules(
                false,
                false,
                false,
                true,
                15,
                null,
                Set.of("UTC"),
                Duration.ofDays(1)
        );
    }

    public static DateTimeValidationRules forBirthDate() {
        return new DateTimeValidationRules(
                false,
                true,
                false,
                false,
                null,
                18,
                Set.of("UTC"),
                ofApproximateYears(120)
        );
    }

    public static Duration ofApproximateYears(int years) {
        return Duration.ofDays(years * 365L + years / 4 - years / 100 + years / 400);
    }
}