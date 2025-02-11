package br.com.drinkwater.core.validation.date;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeValidator {

    private static final DateTimeFormatter UTC_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public static ValidationResult<OffsetDateTime> validateUTCDateTime(String dateTimeStr,
                                                                       DateTimeValidationConfig config) {
        try {
            OffsetDateTime dateTime = OffsetDateTime.parse(dateTimeStr, UTC_FORMATTER);

            if (!dateTime.getOffset().equals(ZoneOffset.UTC)) {
                return ValidationResult.invalid("Date must be in UTC timezone");
            }

            if (!config.allowMilliseconds() && dateTime.getNano() > 0) {
                return ValidationResult.invalid("Date cannot contain milliseconds");
            }

            if (config.requirePast() && dateTime.isAfter(OffsetDateTime.now())) {
                return ValidationResult.invalid("Date must be in the past");
            }

            if (config.minimumAge() != null) {
                LocalDate birthDate = dateTime.toLocalDate();
                LocalDate now = LocalDate.now();
                Period age = Period.between(birthDate, now);

                if (age.getYears() < config.minimumAge()) {
                    return ValidationResult.invalid(
                            String.format("Must be at least %d years old", config.minimumAge())
                    );
                }
            }

            return ValidationResult.valid(dateTime);

        } catch (DateTimeParseException e) {
            return ValidationResult.invalid("Invalid date format. Expected format: YYYY-MM-DDThh:mm:ssZ");
        }
    }

    public static ValidationResult<OffsetDateTime> validateUTCDateTime(OffsetDateTime dateTime,
                                                                       DateTimeValidationConfig config) {
        if (dateTime == null) {
            return ValidationResult.invalid("Date cannot be null");
        }

        return validateUTCDateTime(dateTime.format(UTC_FORMATTER), config);
    }
}