package br.com.drinkwater.core.validation.date;

public record DateTimeValidationConfig(
        boolean allowMilliseconds,
        boolean requirePast,
        boolean requireFuture,
        boolean requireSameDay,
        Integer minimumMinutesInterval,
        Integer minimumAge
) {
    public static DateTimeValidationConfig forHydrationTracking() {
        return new DateTimeValidationConfig(
                false,  // não permite milissegundos
                false,  // não requer data no passado
                false,  // não requer data no futuro
                false,  // não requer mesmo dia
                15,    // intervalo mínimo de 15 minutos
                null   // sem idade mínima
        );
    }

    public static DateTimeValidationConfig forAlarmSettings() {
        return new DateTimeValidationConfig(
                false,  // não permite milissegundos
                false,  // não requer data no passado
                false,  // não requer data no futuro
                true,   // requer mesmo dia
                null,   // sem intervalo mínimo
                null    // sem idade mínima
        );
    }

    public static DateTimeValidationConfig forBirthDate() {
        return new DateTimeValidationConfig(
                false,  // não permite milissegundos
                true,   // requer data no passado
                false,  // não requer data no futuro
                false,  // não requer mesmo dia
                null,   // sem intervalo mínimo
                18     // idade mínima de 18 anos
        );
    }
}