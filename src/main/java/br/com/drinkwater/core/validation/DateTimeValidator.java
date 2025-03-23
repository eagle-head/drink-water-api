package br.com.drinkwater.core.validation;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeValidator {

    private static final DateTimeFormatter UTC_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    private final MessageSource messageSource;

    public DateTimeValidator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public ValidationResult<OffsetDateTime> validateUTCDateTime(String dateTimeStr,
                                                                DateTimeValidationRules config) {
        try {
            OffsetDateTime dateTime = OffsetDateTime.parse(dateTimeStr, UTC_FORMATTER);

            if (!dateTime.getOffset().equals(ZoneOffset.UTC)) {
                return ValidationResult.invalid(getMessage("validation.datetime.utc.timezone"));
            }

            if (!config.allowMilliseconds() && dateTime.getNano() > 0) {
                return ValidationResult.invalid(getMessage("validation.datetime.no.milliseconds"));
            }

            if (config.requirePast() && dateTime.isAfter(OffsetDateTime.now())) {
                return ValidationResult.invalid(getMessage("validation.datetime.must.past"));
            }

            if (config.minimumAge() != null) {
                LocalDate birthDate = dateTime.toLocalDate();
                LocalDate now = LocalDate.now();
                Period age = Period.between(birthDate, now);

                if (age.getYears() < config.minimumAge()) {
                    return ValidationResult.invalid(
                            getMessage("validation.datetime.minimum.age",
                                    new Object[]{config.minimumAge()})
                    );
                }
            }

            return ValidationResult.valid(dateTime);

        } catch (DateTimeParseException e) {
            return ValidationResult.invalid(getMessage("validation.datetime.invalid.format"));
        }
    }

    public ValidationResult<OffsetDateTime> validateUTCDateTime(OffsetDateTime dateTime,
                                                                DateTimeValidationRules config) {
        if (dateTime == null) {
            return ValidationResult.invalid(getMessage("validation.datetime.null"));
        }

        return validateUTCDateTime(dateTime.format(UTC_FORMATTER), config);
    }

    private String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}