package br.com.drinkwater.exception;

import br.com.drinkwater.config.keycloak.KeycloakOperationException;
import br.com.drinkwater.config.runtime.RuntimeConfigurationException;
import br.com.drinkwater.config.security.InsufficientScopeException;
import br.com.drinkwater.config.security.ScopeAwareAccessDeniedHandler;
import br.com.drinkwater.core.MessageResolver;
import br.com.drinkwater.hydrationtracking.exception.DuplicateDateTimeException;
import br.com.drinkwater.hydrationtracking.exception.WaterIntakeNotFoundException;
import br.com.drinkwater.usermanagement.exception.UserAlreadyExistsException;
import br.com.drinkwater.usermanagement.exception.UserNotFoundException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Centralized exception handler that translates application exceptions into RFC 7807 ProblemDetail
 * responses. Each handler maps a specific exception to an appropriate HTTP status code and a
 * localized error message.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String PROBLEM_DETAILS_BASE_URL = "https://www.drinkwater.com.br";

    private final MessageResolver messageResolver;
    private final MessageSource messageSource;
    private final ScopeAwareAccessDeniedHandler scopeAwareAccessDeniedHandler;

    public GlobalExceptionHandler(
            MessageResolver messageResolver,
            MessageSource messageSource,
            ScopeAwareAccessDeniedHandler scopeAwareAccessDeniedHandler) {
        this.messageResolver = messageResolver;
        this.messageSource = messageSource;
        this.scopeAwareAccessDeniedHandler = scopeAwareAccessDeniedHandler;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        return buildResponse(
                ex,
                request,
                HttpStatus.BAD_REQUEST,
                "exception.illegal-argument",
                "invalid-argument");
    }

    @ExceptionHandler(InsufficientScopeException.class)
    public ResponseEntity<Object> handleInsufficientScopeException(
            InsufficientScopeException ex, WebRequest request) {
        ProblemDetail problemDetail =
                buildProblemDetail(
                        HttpStatus.FORBIDDEN, "exception.insufficient-scope", "insufficient-scope");
        problemDetail.setProperty("required_scope", ex.getRequiredScope());
        return handleExceptionInternal(
                ex, problemDetail, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Object> handleAuthorizationDeniedException(
            AuthorizationDeniedException ex, WebRequest request) {
        Optional<String> missingScope = scopeAwareAccessDeniedHandler.extractMissingScope(ex);

        if (missingScope.isPresent()) {
            ProblemDetail problemDetail =
                    buildProblemDetail(
                            HttpStatus.FORBIDDEN,
                            "exception.insufficient-scope",
                            "insufficient-scope");
            problemDetail.setProperty("required_scope", missingScope.get());
            return handleExceptionInternal(
                    ex, problemDetail, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
        }

        return buildResponse(
                ex, request, HttpStatus.FORBIDDEN, "exception.authorization-denied", "forbidden");
    }

    @ExceptionHandler(WaterIntakeNotFoundException.class)
    public ResponseEntity<Object> handleWaterIntakeNotFoundException(
            WaterIntakeNotFoundException ex, WebRequest request) {
        return buildResponse(
                ex,
                request,
                HttpStatus.NOT_FOUND,
                "exception.water-intake.not-found",
                "waterintake-not-found");
    }

    @ExceptionHandler(DuplicateDateTimeException.class)
    public ResponseEntity<Object> handleDuplicateDateTimeException(
            DuplicateDateTimeException ex, WebRequest request) {
        return buildResponse(
                ex,
                request,
                HttpStatus.BAD_REQUEST,
                "exception.water-intake.duplicate-datetime",
                "time-range-validation-error");
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(
            UserNotFoundException ex, WebRequest request) {
        return buildResponse(
                ex, request, HttpStatus.NOT_FOUND, "exception.user.not-found", "user-not-found");
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> handleUserAlreadyExistsException(
            UserAlreadyExistsException ex, WebRequest request) {
        return buildResponse(
                ex,
                request,
                HttpStatus.CONFLICT,
                "exception.user.already-exists",
                "user-already-exists");
    }

    @ExceptionHandler(RuntimeConfigurationException.class)
    public ResponseEntity<Object> handleRuntimeConfigurationException(
            RuntimeConfigurationException ex, WebRequest request) {
        return buildResponse(
                ex,
                request,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "exception.runtime-configuration",
                "runtime-configuration-error");
    }

    @ExceptionHandler(KeycloakOperationException.class)
    public ResponseEntity<Object> handleKeycloakOperationException(
            KeycloakOperationException ex, WebRequest request) {
        return buildResponse(
                ex,
                request,
                HttpStatus.SERVICE_UNAVAILABLE,
                "exception.keycloak.unavailable",
                "keycloak-unavailable");
    }

    /**
     * Handles rate limit exceeded scenarios from Resilience4j RateLimiter.
     *
     * <p>Returns a 429 Too Many Requests response with a {@code Retry-After} header indicating when
     * the client may retry. The header value matches the {@code limit-refresh-period} configured
     * for the default rate limiter (60 seconds).
     *
     * @param ex the exception thrown when the rate limit is exceeded
     * @param request the current web request
     * @return a 429 response with RFC 7807 ProblemDetail body and Retry-After header
     */
    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<Object> handleRateLimitExceeded(
            RequestNotPermitted ex, WebRequest request) {
        HttpStatus status = HttpStatus.TOO_MANY_REQUESTS;
        ProblemDetail problemDetail =
                buildProblemDetail(status, "exception.rate-limit-exceeded", "rate-limit-exceeded");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Retry-After", "60");
        return handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        return buildResponse(
                ex,
                request,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "exception.internal-server-error",
                "internal-server-error");
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            @NonNull HttpMessageNotReadableException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode statusCode,
            @NonNull WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemDetail problemDetail =
                buildProblemDetail(status, "exception.parsing-error", "parsing-error");
        problemDetail.setStatus(statusCode.value());

        switch (ex.getCause()) {
            case InvalidFormatException invalidFormatException -> {
                List<Map<String, String>> errors =
                        invalidFormatException.getPath().stream()
                                .map(
                                        ref ->
                                                fieldError(
                                                        ref.getFieldName(),
                                                        messageResolver.resolve(
                                                                "validation.invalid-value",
                                                                invalidFormatException
                                                                        .getValue()
                                                                        .toString(),
                                                                ref.getFieldName())))
                                .toList();
                problemDetail.setProperty("errors", errors);
            }
            case null -> {}
            case Throwable cause -> problemDetail.setProperty("cause", cause.getMessage());
        }

        return handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode statusCode,
            @NonNull WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemDetail problemDetail = ex.getBody();
        problemDetail.setDetail(messageResolver.resolve("validation.error"));
        problemDetail.setStatus(status.value());
        problemDetail.setType(
                Objects.requireNonNull(URI.create(PROBLEM_DETAILS_BASE_URL + "/validation-error")));

        var fieldErrors =
                new java.util.ArrayList<>(
                        ex.getBindingResult().getFieldErrors().stream()
                                .map(
                                        fieldError -> {
                                            String message =
                                                    messageSource.getMessage(
                                                            Objects.requireNonNull(
                                                                    (MessageSourceResolvable)
                                                                            fieldError),
                                                            LocaleContextHolder.getLocale());

                                            String code = fieldError.getCode();
                                            if (code != null && code.contains("typeMismatch")) {
                                                message =
                                                        messageResolver.resolve(
                                                                "validation.datetime.invalid-format");
                                            }

                                            return fieldError(fieldError.getField(), message);
                                        })
                                .toList());

        var globalErrors =
                ex.getBindingResult().getGlobalErrors().stream()
                        .map(
                                objectError ->
                                        fieldError(
                                                objectError.getObjectName(),
                                                messageSource.getMessage(
                                                        Objects.requireNonNull(
                                                                (MessageSourceResolvable)
                                                                        objectError),
                                                        LocaleContextHolder.getLocale())))
                        .toList();

        fieldErrors.addAll(globalErrors);
        problemDetail.setProperty("errors", fieldErrors);

        return handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemDetail problemDetail =
                buildProblemDetail(status, "validation.constraint", "constraint-violation");

        List<Map<String, String>> errors =
                ex.getConstraintViolations().stream()
                        .map(
                                violation -> {
                                    var error =
                                            fieldError(
                                                    extractFieldName(violation),
                                                    violation.getMessage());
                                    if (violation.getInvalidValue() != null) {
                                        error.put(
                                                "invalidValue",
                                                violation.getInvalidValue().toString());
                                    }
                                    return error;
                                })
                        .toList();

        problemDetail.setProperty("errors", errors);
        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemDetail problemDetail =
                buildProblemDetail(status, "validation.type-mismatch", "type-mismatch");

        Class<?> requiredType = ex.getRequiredType();
        String expectedType = requiredType != null ? requiredType.getSimpleName() : "correct type";

        var error =
                fieldError(
                        ex.getName(),
                        messageResolver.resolve(
                                "validation.type-mismatch.field", ex.getName(), expectedType));

        Object invalidValue = ex.getValue();
        if (invalidValue != null) {
            error.put("invalidValue", invalidValue.toString());
        }

        problemDetail.setProperty("errors", List.of(error));
        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            @NonNull MissingServletRequestParameterException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        HttpStatus httpStatus = HttpStatus.valueOf(status.value());
        ProblemDetail problemDetail =
                buildProblemDetail(httpStatus, "validation.missing-parameter", "missing-parameter");

        var error =
                fieldError(
                        ex.getParameterName(),
                        messageResolver.resolve(
                                "validation.missing-parameter.field", ex.getParameterName()));

        problemDetail.setProperty("errors", List.of(error));
        return handleExceptionInternal(ex, problemDetail, headers, httpStatus, request);
    }

    private ResponseEntity<Object> buildResponse(
            Exception ex,
            WebRequest request,
            HttpStatus status,
            String messageKey,
            String typeSlug) {
        ProblemDetail problemDetail = buildProblemDetail(status, messageKey, typeSlug);
        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    private ProblemDetail buildProblemDetail(
            HttpStatus status, String messageKey, String typeSlug) {
        String detail = messageResolver.resolve(messageKey);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setType(
                Objects.requireNonNull(URI.create(PROBLEM_DETAILS_BASE_URL + "/" + typeSlug)));
        return problemDetail;
    }

    private static Map<String, String> fieldError(String field, String message) {
        return new java.util.HashMap<>(Map.of("field", field, "message", message));
    }

    private static String extractFieldName(ConstraintViolation<?> violation) {
        String path = violation.getPropertyPath().toString();
        int lastDotIndex = path.lastIndexOf('.');
        return lastDotIndex > 0 ? path.substring(lastDotIndex + 1) : path;
    }
}
