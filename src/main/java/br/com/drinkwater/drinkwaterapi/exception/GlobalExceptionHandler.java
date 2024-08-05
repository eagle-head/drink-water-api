package br.com.drinkwater.drinkwaterapi.exception;

import br.com.drinkwater.drinkwaterapi.usermanagement.exception.EmailAlreadyUsedException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String UNEXPECTED_INTERNAL_ERROR = "An unexpected internal error occurred in the system."
            + " Please try again and if the problem persists, contact the system administrator.";

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode statusCode,
                                                                  WebRequest request) {
        ProblemDetailResponseType type = ProblemDetailResponseType.VALIDATION_ERROR;
        String detail = "Validation error: some entries are incorrect or incomplete. "
                + "Please review the details and correct them.";

        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

        BindingResult bindingResult = ex.getBindingResult();

        List<ProblemDetailResponse.Constraint> constraints = bindingResult
                .getAllErrors()
                .stream()
                .map(objectError -> {
                    String message = messageSource.getMessage(objectError, LocaleContextHolder.getLocale());

                    String name = objectError.getObjectName();

                    if (objectError instanceof FieldError) {
                        name = ((FieldError) objectError).getField();
                    }

                    return ProblemDetailResponse.Constraint.builder()
                            .name(name)
                            .userMessage(message)
                            .build();
                })
                .toList();

        ProblemDetailResponse response = createProblemDetailResponseBuilder(status, type, detail)
                .userMessage(detail)
                .constraints(constraints)
                .build();

        return handleExceptionInternal(ex, response, headers, status, request);
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    protected ResponseEntity<Object> handleEmailAlreadyUsed(EmailAlreadyUsedException exception, WebRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        String detail = exception.getMessage();
        ProblemDetailResponseType type = ProblemDetailResponseType.CONFLICT;
        ProblemDetailResponse responseBody = createProblemDetailResponseBuilder(status, type, detail)
                .userMessage(detail)
                .build();

        return handleExceptionInternal(exception, responseBody, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException exception, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String detail = exception.getMessage();
        ProblemDetailResponseType type = ProblemDetailResponseType.ENTITY_NOT_FOUND;
        ProblemDetailResponse responseBody = createProblemDetailResponseBuilder(status, type, detail)
                .userMessage(detail)
                .build();

        return handleExceptionInternal(exception, responseBody, new HttpHeaders(), status, request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex,
                                                        HttpHeaders headers,
                                                        HttpStatusCode statusCode,
                                                        WebRequest request) {
        HttpStatus status = HttpStatus.resolve(statusCode.value());
        if (status == null) {
            status = HttpStatus.BAD_REQUEST;
        }

        if (ex instanceof MethodArgumentTypeMismatchException) {
            return handleMethodArgumentTypeMismatch(
                    (MethodArgumentTypeMismatchException) ex, headers, status, request);
        }

        return super.handleTypeMismatch(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex,
                                                             Object body,
                                                             HttpHeaders headers,
                                                             HttpStatusCode statusCode,
                                                             WebRequest request) {
        HttpStatus status = HttpStatus.resolve(statusCode.value());
        String reasonPhrase = status != null ? status.getReasonPhrase() : "Unknown Status";

        if (body == null) {
            body = ProblemDetailResponse.builder()
                    .timestamp(OffsetDateTime.now(ZoneOffset.UTC))
                    .status(statusCode.value())
                    .detail(reasonPhrase)
                    .userMessage(UNEXPECTED_INTERNAL_ERROR)
                    .build();
        } else if (body instanceof String) {
            body = ProblemDetailResponse.builder()
                    .timestamp(OffsetDateTime.now(ZoneOffset.UTC))
                    .status(statusCode.value())
                    .detail((String) body)
                    .userMessage(UNEXPECTED_INTERNAL_ERROR)
                    .build();
        }

        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }

    private ProblemDetailResponse.ProblemDetailResponseBuilder createProblemDetailResponseBuilder(
            HttpStatus status, ProblemDetailResponseType type, String detail) {

        return ProblemDetailResponse
                .builder()
                .status(status.value())
                .type(URI.create(type.getUri()))
                .title(type.getTitle())
                .detail(detail)
                .timestamp(OffsetDateTime.now(ZoneOffset.UTC));
    }

    private ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                                    HttpHeaders headers,
                                                                    HttpStatus status,
                                                                    WebRequest request) {
        ProblemDetailResponseType type = ProblemDetailResponseType.TYPE_MISMATCH;

        String detail = String.format("The URL parameter '%s' received the value '%s', which is of an invalid type."
                        + " Please correct and provide a value that is compatible with the type %s.",
                ex.getName(), ex.getValue(), Objects.requireNonNull(ex.getRequiredType()).getSimpleName());

        ProblemDetailResponse response = createProblemDetailResponseBuilder(status, type, detail)
                .userMessage(UNEXPECTED_INTERNAL_ERROR)
                .build();

        return handleExceptionInternal(ex, response, headers, status, request);
    }
}
