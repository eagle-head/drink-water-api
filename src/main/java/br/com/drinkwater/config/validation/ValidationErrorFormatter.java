package br.com.drinkwater.config.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Shared utility class for formatting validation errors across configuration validators.
 * <p>
 * This component provides centralized validation error formatting to eliminate code duplication
 * between different validator classes. It standardizes the error message format and provides
 * consistent validation behavior across the application.
 * <p>
 * The formatted error messages follow the pattern:
 * {@code [ComponentName] propertyPath: validationMessage}
 * <p>
 * Example usage:
 * <pre>
 * {@code
 * List<String> errors = validationErrorFormatter.formatValidationErrors(
 *     myConfigObject, "Database Configuration", validator);
 * }
 * </pre>
 */
@Component
public class ValidationErrorFormatter {

    /**
     * Validates a configuration object and formats any validation errors into standardized error messages.
     * <p>
     * This method uses Jakarta Bean Validation to validate the provided object and converts
     * any constraint violations into formatted error messages that include the component name,
     * property path, and validation message.
     *
     * @param <T>           the type of the configuration object to validate
     * @param configObject  the configuration object to validate (must not be null)
     * @param componentName the descriptive name of the component being validated (must not be null)
     * @param validator     the Jakarta Bean Validation validator instance (must not be null)
     * @return a list of formatted error messages; empty list if validation passes
     * @throws IllegalArgumentException if any parameter is null
     */
    public <T> List<String> formatValidationErrors(T configObject, String componentName, Validator validator) {
        if (configObject == null) {
            throw new IllegalArgumentException("Configuration object cannot be null");
        }

        if (componentName == null) {
            throw new IllegalArgumentException("Component name cannot be null");
        }

        if (validator == null) {
            throw new IllegalArgumentException("Validator cannot be null");
        }

        Set<ConstraintViolation<T>> violations = validator.validate(configObject);
        List<String> errors = new ArrayList<>();

        for (ConstraintViolation<T> violation : violations) {
            String formattedError = formatSingleError(componentName, violation.getPropertyPath().toString(), violation.getMessage());
            errors.add(formattedError);
        }

        return errors;
    }

    /**
     * Formats a single validation error into a standardized error message.
     * <p>
     * The error message format is: {@code [ComponentName] propertyPath: validationMessage}
     *
     * @param componentName     the name of the component that failed validation
     * @param propertyPath      the property path that caused the validation failure
     * @param validationMessage the validation error message
     * @return formatted error message string
     */
    private String formatSingleError(String componentName, String propertyPath, String validationMessage) {
        return String.format("[%s] %s: %s", componentName, propertyPath, validationMessage);
    }

    /**
     * Validates multiple configuration objects and aggregates all validation errors.
     * <p>
     * This is a convenience method for validating multiple configuration objects in a single call
     * and collecting all validation errors into a single list.
     *
     * @param validator         the Jakarta Bean Validation validator instance
     * @param validationTargets variable number of validation target objects containing the config object and component name
     * @return aggregated list of all validation errors from all objects; empty list if all validations pass
     * @throws IllegalArgumentException if validator is null or any validation target is invalid
     */
    public List<String> validateMultipleObjects(Validator validator, ValidationTarget<?>... validationTargets) {
        if (validator == null) {
            throw new IllegalArgumentException("Validator cannot be null");
        }

        List<String> allErrors = new ArrayList<>();

        for (ValidationTarget<?> target : validationTargets) {
            if (target == null) {
                throw new IllegalArgumentException("Validation target cannot be null");
            }
            allErrors.addAll(formatValidationErrors(target.configObject(), target.componentName(), validator));
        }

        return allErrors;
    }

    /**
     * Record representing a validation target with its configuration object and component name.
     * <p>
     * This record is used with {@link #validateMultipleObjects(Validator, ValidationTarget[])}
     * to validate multiple configuration objects in a single operation.
     *
     * @param <T>           the type of the configuration object
     * @param configObject  the configuration object to validate
     * @param componentName the descriptive name of the component
     */
    public record ValidationTarget<T>(T configObject, String componentName) {
        public ValidationTarget {
            if (configObject == null) {
                throw new IllegalArgumentException("Configuration object cannot be null");
            }

            if (componentName == null || componentName.trim().isEmpty()) {
                throw new IllegalArgumentException("Component name cannot be null or empty");
            }
        }
    }
}