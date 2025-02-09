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
import org.springframework.validation.BindingResult;
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

    private static final String BASE_ERROR_URI = "https://www.drinkwater.com.br";

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(InvalidFilterException.class)
    public ResponseEntity<Object> handleInvalidFilterException(InvalidFilterException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String title = status.getReasonPhrase();
        String detail = this.messageSource.getMessage("invalid.filter.detail", null, LocaleContextHolder.getLocale());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setType(URI.create(BASE_ERROR_URI + "/invalid-filter"));
        problemDetail.setProperty("errors", ex.getErrors());

        return super.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Object> handleAuthorizationDeniedException(AuthorizationDeniedException ex, WebRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        String title = status.getReasonPhrase();
        String detail = this.messageSource.getMessage("authorization.denied.detail", null, LocaleContextHolder.getLocale());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setType(URI.create(BASE_ERROR_URI + "/forbidden"));

        return super.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(WaterIntakeNotFoundException.class)
    public ResponseEntity<Object> handleWaterIntakeNotFoundException(WaterIntakeNotFoundException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String title = status.getReasonPhrase();
        String detail = this.messageSource.getMessage("waterintake.not.found.detail", null, LocaleContextHolder.getLocale());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setType(URI.create(BASE_ERROR_URI + "/waterintake-not-found"));

        return super.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(DuplicateDateTimeException.class)
    public ResponseEntity<Object> handleDuplicateDateTimeException(DuplicateDateTimeException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String title = status.getReasonPhrase();
        String detail = this.messageSource.getMessage("duplicate.date.time.detail", null, LocaleContextHolder.getLocale());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setType(URI.create(BASE_ERROR_URI + "/time-range-validation-error"));

        return super.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String title = status.getReasonPhrase();
        String detail = this.messageSource.getMessage("user.not.found.detail", null, LocaleContextHolder.getLocale());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setType(URI.create(BASE_ERROR_URI + "/user-not-found"));

        return super.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> handleEmailAlreadyUsedException(UserAlreadyExistsException ex, WebRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        String title = status.getReasonPhrase();
        String detail = this.messageSource.getMessage("user.already.exists.detail", null, LocaleContextHolder.getLocale());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setType(URI.create(BASE_ERROR_URI + "/user-already-exists"));

        return super.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String title = status.getReasonPhrase();
        String detail = this.messageSource.getMessage("internal.server.error.detail", null, LocaleContextHolder.getLocale());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setType(URI.create(BASE_ERROR_URI + "/internal-server-error"));

        return super.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode statusCode,
                                                                  @NonNull WebRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        String title = status.getReasonPhrase();
        String detail = this.messageSource.getMessage("parsing.error.detail", null, LocaleContextHolder.getLocale());

        ProblemDetail problemDetail = createProblemDetail(ex, status, detail, null, null, request);
        problemDetail.setTitle(title);
        problemDetail.setDetail(detail);
        problemDetail.setStatus(statusCode.value());
        problemDetail.setType(URI.create(BASE_ERROR_URI + "/parsing-error"));

        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException invalidFormatException) {
            List<Map<String, String>> errors = invalidFormatException.getPath().stream()
                    .map(ref -> {
                        String field = ref.getFieldName();
                        String rejectedValue = invalidFormatException.getValue().toString();

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
        String title = status.getReasonPhrase();
        String detail = this.messageSource
                .getMessage("validation.error.detail", null, LocaleContextHolder.getLocale());

        ProblemDetail problemDetail = ex.getBody();
        problemDetail.setTitle(title);
        problemDetail.setDetail(detail);
        problemDetail.setStatus(statusCode.value());
        problemDetail.setType(URI.create(BASE_ERROR_URI + "/validation-error"));

        BindingResult bindingResult = ex.getBindingResult();

        // Collect field errors
        List<Map<String, String>> fieldErrors = bindingResult.getFieldErrors().stream()
                .map(fieldError -> {
                    Map<String, String> error = new HashMap<>();
                    error.put("field", fieldError.getField());
                    error.put("message", messageSource.getMessage(fieldError, LocaleContextHolder.getLocale()));

                    return error;
                })
                .collect(Collectors.toList());

        // Collect global errors (class level)
        List<Map<String, String>> globalErrors = bindingResult.getGlobalErrors().stream()
                .map(objectError -> {
                    Map<String, String> error = new HashMap<>();
                    error.put("field", objectError.getObjectName());
                    error.put("message", messageSource.getMessage(objectError, LocaleContextHolder.getLocale()));

                    return error;
                })
                .collect(Collectors.toList());

        // Merge field and global errors
        fieldErrors.addAll(globalErrors);
        problemDetail.setProperty("errors", fieldErrors);

        return super.handleExceptionInternal(ex, problemDetail, headers, status, request);
    }
}
