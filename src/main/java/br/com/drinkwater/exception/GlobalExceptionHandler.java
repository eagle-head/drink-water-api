package br.com.drinkwater.exception;

import br.com.drinkwater.hydrationtracking.exception.DuplicateDateTimeException;
import br.com.drinkwater.hydrationtracking.exception.InvalidFilterException;
import br.com.drinkwater.hydrationtracking.exception.WaterIntakeNotFoundException;
import br.com.drinkwater.usermanagement.exception.UserAlreadyExistsException;
import br.com.drinkwater.usermanagement.exception.UserNotFoundException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String PROBLEM_DETAILS_BASE_URL = "https://www.drinkwater.com.br";

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(InvalidFilterException.class)
    public ResponseEntity<Object> handleInvalidFilterException(InvalidFilterException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String detail = this.messageSource.getMessage("invalid.filter.detail", null, LocaleContextHolder.getLocale());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setType(URI.create(PROBLEM_DETAILS_BASE_URL + "/invalid-filter"));
        problemDetail.setProperty("errors", ex.getErrors());

        return super.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Object> handleAuthorizationDeniedException(AuthorizationDeniedException ex, WebRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        String detail = this.messageSource.getMessage("authorization.denied.detail", null, LocaleContextHolder.getLocale());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setType(URI.create(PROBLEM_DETAILS_BASE_URL + "/forbidden"));

        return super.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(WaterIntakeNotFoundException.class)
    public ResponseEntity<Object> handleWaterIntakeNotFoundException(WaterIntakeNotFoundException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String detail = this.messageSource.getMessage("waterintake.not.found.detail", null, LocaleContextHolder.getLocale());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setType(URI.create(PROBLEM_DETAILS_BASE_URL + "/waterintake-not-found"));

        return super.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(DuplicateDateTimeException.class)
    public ResponseEntity<Object> handleDuplicateDateTimeException(DuplicateDateTimeException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String detail = this.messageSource.getMessage("duplicate.date.time.detail", null, LocaleContextHolder.getLocale());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setType(URI.create(PROBLEM_DETAILS_BASE_URL + "/time-range-validation-error"));

        return super.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String detail = this.messageSource.getMessage("user.not.found.detail", null, LocaleContextHolder.getLocale());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setType(URI.create(PROBLEM_DETAILS_BASE_URL + "/user-not-found"));

        return super.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> handleEmailAlreadyUsedException(UserAlreadyExistsException ex, WebRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        String detail = this.messageSource.getMessage("user.already.exists.detail", null, LocaleContextHolder.getLocale());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setType(URI.create(PROBLEM_DETAILS_BASE_URL + "/user-already-exists"));

        return super.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String detail = this.messageSource.getMessage("internal.server.error.detail", null, LocaleContextHolder.getLocale());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setType(URI.create(PROBLEM_DETAILS_BASE_URL + "/internal-server-error"));

        return super.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode statusCode,
                                                                  @NonNull WebRequest request) {
        // Set basic error response properties
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String detail = this.messageSource.getMessage(
                "parsing.error.detail",
                null,
                LocaleContextHolder.getLocale()
        );

        // Create problem detail with basic information
        ProblemDetail problemDetail = this.createProblemDetail(
                ex,
                status,
                detail,
                null,
                null,
                request
        );
        problemDetail.setDetail(detail);
        problemDetail.setStatus(statusCode.value());
        problemDetail.setType(URI.create(PROBLEM_DETAILS_BASE_URL + "/parsing-error"));

        // Check for InvalidFormatException to provide more detailed error information
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException invalidFormatException) {
            // Extract field information and rejected values from the exception
            List<Map<String, String>> errors = invalidFormatException.getPath().stream()
                    .map(ref -> {
                        String field = ref.getFieldName();
                        String rejectedValue = invalidFormatException.getValue().toString();

                        // Create error detail with field name and localized message
                        Map<String, String> error = new HashMap<>();
                        error.put("field", field);
                        error.put("message", this.messageSource.getMessage(
                                "validation.invalid.value",
                                new Object[]{rejectedValue, field},
                                LocaleContextHolder.getLocale()));
                        return error;
                    })
                    .collect(Collectors.toList());

            problemDetail.setProperty("errors", errors);
        } else if (cause != null) {
            // For other exceptions, just include the cause message
            problemDetail.setProperty("cause", cause.getMessage());
        }

        return super.handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
                                                        @NonNull HttpHeaders headers,
                                                        @NonNull HttpStatusCode statusCode,
                                                        @NonNull WebRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        String detail = messageSource.getMessage("validation.error.detail", null, LocaleContextHolder.getLocale());

        // Get the problem detail from the exception and set basic properties
        ProblemDetail problemDetail = ex.getBody();
        problemDetail.setDetail(detail);
        problemDetail.setStatus(status.value());
        problemDetail.setType(URI.create("https://www.drinkwater.com.br/validation-error"));

        // Process field-level validation errors
        List<Map<String, String>> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> {
                    Map<String, String> error = new HashMap<>();
                    error.put("field", fieldError.getField());

                    // Get localized message for the error
                    String message = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());

                    // Handle type mismatch errors (e.g., invalid date format) separately
                    if (fieldError.getCode() != null && fieldError.getCode().contains("typeMismatch")) {
                        message = this.messageSource.getMessage(
                                "validation.datetime.invalid.format",
                                null,
                                LocaleContextHolder.getLocale()
                        );
                    }

                    error.put("message", message);
                    return error;
                })
                .collect(Collectors.toList());

        // Process object-level (global) validation errors
        List<Map<String, String>> globalErrors = ex.getBindingResult().getGlobalErrors().stream()
                .map(objectError -> {
                    Map<String, String> error = new HashMap<>();
                    error.put("field", objectError.getObjectName());
                    error.put("message", this.messageSource.getMessage(objectError, LocaleContextHolder.getLocale()));
                    return error;
                })
                .collect(Collectors.toList());

        // Combine field and global errors into a single list
        fieldErrors.addAll(globalErrors);
        problemDetail.setProperty("errors", fieldErrors);

        return super.handleExceptionInternal(ex, problemDetail, headers, status, request);
    }
}