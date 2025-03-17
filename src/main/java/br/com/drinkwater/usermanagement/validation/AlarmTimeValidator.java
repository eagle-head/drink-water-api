package br.com.drinkwater.usermanagement.validation;

import br.com.drinkwater.usermanagement.dto.AlarmSettingsDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class AlarmTimeValidator implements ConstraintValidator<ValidAlarmTime, AlarmSettingsDTO> {

    private static final LocalTime BUSINESS_START = LocalTime.of(6, 0);
    private static final LocalTime BUSINESS_END = LocalTime.of(22, 0);

    private final MessageSource messageSource;

    public AlarmTimeValidator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public boolean isValid(AlarmSettingsDTO dto, ConstraintValidatorContext context) {
        // If the DTO is null, let other annotations (e.g., @NotNull) handle it
        if (dto == null) return true;

        var start = dto.dailyStartTime();
        var end = dto.dailyEndTime();

        // If either field is null, skip validation (handled by @NotNull elsewhere)
        if (start == null || end == null) return true;

        var valid = true;
        context.disableDefaultConstraintViolation();

        if (!start.isBefore(end)) {
            this.addFieldViolation(context, "alarmTime.start.before.end", "dailyStartTime");
            valid = false;
        }

        if (start.isBefore(BUSINESS_START) || start.isAfter(BUSINESS_END)) {
            this.addFieldViolation(
                    context,
                    "alarmTime.start.business.hours",
                    "dailyStartTime",
                    BUSINESS_START,
                    BUSINESS_END
            );

            valid = false;
        }

        if (end.isBefore(BUSINESS_START) || end.isAfter(BUSINESS_END)) {
            this.addFieldViolation(
                    context,
                    "alarmTime.end.business.hours",
                    "dailyEndTime",
                    BUSINESS_START,
                    BUSINESS_END
            );

            valid = false;
        }

        return valid;
    }

    private String getLocalizedMessage(String key, Object... args) {
        return this.messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

    private void addFieldViolation(ConstraintValidatorContext context, String messageKey, String field, Object... args) {
        context
                .buildConstraintViolationWithTemplate(this.getLocalizedMessage(messageKey, args))
                .addPropertyNode(field)
                .addConstraintViolation();
    }
}
