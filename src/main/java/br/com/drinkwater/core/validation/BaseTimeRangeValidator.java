package br.com.drinkwater.core.validation;

import br.com.drinkwater.core.validation.date.DateTimeValidator;
import br.com.drinkwater.core.validation.date.ValidationResult;
import br.com.drinkwater.core.validation.date.DateTimeValidationConfig;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import java.time.Duration;
import java.time.OffsetDateTime;

public abstract class BaseTimeRangeValidator<T> implements ConstraintValidator<TimeRangeConstraint, T> {

    protected TimeRangeConstraint constraint;

    @Override
    public void initialize(TimeRangeConstraint constraint) {
        this.constraint = constraint;
    }

    /**
     * Permite que implementações específicas definam suas próprias configurações de validação.
     * Por padrão, usa uma configuração básica que pode ser sobrescrita pelos validadores filhos.
     */
    protected DateTimeValidationConfig getDateTimeValidationConfig() {
        return new DateTimeValidationConfig(
                false,  // não permite milissegundos
                false,  // não requer data no passado
                false,  // não requer data no futuro
                false,  // não requer mesmo dia
                null,   // sem intervalo mínimo
                null    // sem idade mínima
        );
    }

    @Override
    public boolean isValid(T value, ConstraintValidatorContext context) {
        // Se o objeto for nulo, deixamos outras validações (como @NotNull) tratarem
        if (value == null) {
            return true;
        }

        // Desabilitamos a mensagem padrão para usar mensagens mais específicas
        context.disableDefaultConstraintViolation();

        DateTimeValidationConfig config = getDateTimeValidationConfig();

        // Obtém e valida a data inicial
        OffsetDateTime startDate = getOffsetDateTimeProperty(value, constraint.startDateField(), context);
        if (startDate == null) {
            return false; // getOffsetDateTimeProperty já adicionou a violação
        }

        ValidationResult<OffsetDateTime> startValidation = DateTimeValidator.validateUTCDateTime(startDate, config);
        if (!startValidation.isValid()) {
            addConstraintViolation(context, startValidation.getErrorMessage(), constraint.startDateField());
            return false;
        }

        // Obtém e valida a data final
        OffsetDateTime endDate = getOffsetDateTimeProperty(value, constraint.endDateField(), context);
        if (endDate == null) {
            return false; // getOffsetDateTimeProperty já adicionou a violação
        }

        ValidationResult<OffsetDateTime> endValidation = DateTimeValidator.validateUTCDateTime(endDate, config);
        if (!endValidation.isValid()) {
            addConstraintViolation(context, endValidation.getErrorMessage(), constraint.endDateField());
            return false;
        }

        // Realiza validações específicas do range de datas
        return validateDateRange(startValidation.getValue(), endValidation.getValue(), config, context);
    }

    /**
     * Realiza as validações específicas para o intervalo entre as datas.
     * Este método centraliza todas as regras relacionadas ao intervalo temporal.
     */
    protected boolean validateDateRange(OffsetDateTime startDate,
                                        OffsetDateTime endDate,
                                        DateTimeValidationConfig config,
                                        ConstraintValidatorContext context) {
        // Valida se a data inicial é anterior à final
        if (!startDate.isBefore(endDate)) {
            addConstraintViolation(context, "Start date must be before end date", constraint.startDateField());
            return false;
        }

        // Se a configuração exige que as datas sejam no mesmo dia
        if (config.requireSameDay() && !startDate.toLocalDate().equals(endDate.toLocalDate())) {
            addConstraintViolation(context, "Start and end dates must be on the same day", constraint.startDateField());
            return false;
        }

        // Se existe um intervalo mínimo configurado, valida-o
        if (config.minimumMinutesInterval() != null) {
            long minutesBetween = Duration.between(startDate, endDate).toMinutes();
            if (minutesBetween < config.minimumMinutesInterval()) {
                addConstraintViolation(
                        context,
                        String.format("The time range must be at least %d minutes", config.minimumMinutesInterval()),
                        constraint.startDateField()
                );
                return false;
            }
        }

        return true;
    }

    /**
     * Extrai uma propriedade do tipo OffsetDateTime do objeto sendo validado.
     * Suporta tanto propriedades do tipo OffsetDateTime quanto String.
     */
    protected OffsetDateTime getOffsetDateTimeProperty(T value,
                                                       String propertyName,
                                                       ConstraintValidatorContext context) {
        try {
            BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(value);

            // Verifica se a propriedade existe e é acessível
            if (!wrapper.isReadableProperty(propertyName)) {
                addConstraintViolation(
                        context,
                        String.format("Property '%s' does not exist or is not accessible", propertyName),
                        propertyName
                );
                return null;
            }

            // Obtém o tipo da propriedade
            Class<?> propertyType = wrapper.getPropertyType(propertyName);
            if (propertyType == null) {
                addConstraintViolation(
                        context,
                        String.format("Cannot determine type of property '%s'", propertyName),
                        propertyName
                );
                return null;
            }

            // Verifica se o tipo é compatível
            if (!OffsetDateTime.class.isAssignableFrom(propertyType) &&
                    !String.class.isAssignableFrom(propertyType)) {
                addConstraintViolation(
                        context,
                        String.format("Property '%s' must be either OffsetDateTime or String", propertyName),
                        propertyName
                );
                return null;
            }

            // Obtém o valor da propriedade
            Object propertyValue = wrapper.getPropertyValue(propertyName);
            if (propertyValue == null) {
                return null; // Deixa a validação @NotNull lidar com isso
            }

            // Se já é um OffsetDateTime, retorna diretamente
            if (propertyValue instanceof OffsetDateTime) {
                return (OffsetDateTime) propertyValue;
            }

            // Se é uma String, tenta fazer o parse
            if (propertyValue instanceof String) {
                String strValue = ((String) propertyValue).trim();
                if (strValue.isEmpty()) {
                    addConstraintViolation(context, "Date string cannot be empty", propertyName);
                    return null;
                }

                try {
                    return OffsetDateTime.parse(strValue);
                } catch (Exception e) {
                    addConstraintViolation(
                            context,
                            String.format("Invalid date format: %s", e.getMessage()),
                            propertyName
                    );
                    return null;
                }
            }

            // Não deveria chegar aqui devido à validação de tipo anterior
            throw new IllegalStateException("Unexpected property type");

        } catch (Exception e) {
            addConstraintViolation(
                    context,
                    String.format("Error accessing property '%s': %s", propertyName, e.getMessage()),
                    propertyName
            );
            return null;
        }
    }

    /**
     * Método utilitário para adicionar violações de constraint de forma padronizada.
     */
    protected void addConstraintViolation(ConstraintValidatorContext context,
                                          String errorMessage,
                                          String fieldName) {
        context.buildConstraintViolationWithTemplate(errorMessage)
                .addPropertyNode(fieldName)
                .addConstraintViolation();
    }
}