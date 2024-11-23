package br.com.drinkwater.exception;

import br.com.drinkwater.hydrationtracking.exception.DuplicateDateTimeException;
import br.com.drinkwater.hydrationtracking.exception.WaterIntakeNotFoundException;
import br.com.drinkwater.usermanagement.exception.EmailAlreadyUsedException;
import br.com.drinkwater.usermanagement.exception.UserNotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
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

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(WaterIntakeNotFoundException.class)
    public ResponseEntity<Object> handleWaterIntakeNotFoundException(WaterIntakeNotFoundException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        String detail = this.messageSource.getMessage("waterintake.not.found", null, LocaleContextHolder.getLocale());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle("Water Intake Not Found");
        problemDetail.setType(URI.create("https://www.drinkwater.com.br/waterintake-not-found"));

        return super.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(DuplicateDateTimeException.class)
    public ResponseEntity<Object> handleDuplicateDateTimeException(DuplicateDateTimeException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String detail = this.messageSource.getMessage("duplicate.date.time", null, LocaleContextHolder.getLocale());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle("Time Range Validation Error");
        problemDetail.setType(URI.create("https://www.drinkwater.com.br/time-range-validation-error"));

        return super.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        String detail = this.messageSource.getMessage("user.not.found", null, LocaleContextHolder.getLocale());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle("User Not Found");
        problemDetail.setType(URI.create("https://www.drinkwater.com.br/user-not-found"));

        return super.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<Object> handleEmailAlreadyUsedException(EmailAlreadyUsedException ex, WebRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;

        String detail = this.messageSource.getMessage("email.already.used", null, LocaleContextHolder.getLocale());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle("Email Already Used");
        problemDetail.setType(URI.create("https://www.drinkwater.com.br/email-already-used"));

        return super.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        String detail = this.messageSource.getMessage("internal.server.error", null, LocaleContextHolder.getLocale());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(detail);
        problemDetail.setType(URI.create("https://www.drinkwater.com.br/internal-server-error"));

        return super.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode statusCode,
                                                                  @NonNull WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        String detail = "Failed to parse the request. Please check the provided data.";

        ProblemDetail problemDetail = createProblemDetail(ex, status, detail, null, null, request);
        problemDetail.setType(URI.create("https://www.drinkwater.com.br/parsing-error"));
        problemDetail.setTitle("Bad Request");

        return super.handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode statusCode,
                                                                  @NonNull WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        String title = this.messageSource.getMessage("validation.error", null, LocaleContextHolder.getLocale());
        String detail = this.messageSource.getMessage("validation.detail", null, LocaleContextHolder.getLocale());

        ProblemDetail problemDetail = ex.getBody();
        problemDetail.setTitle(title);
        problemDetail.setDetail(detail);
        problemDetail.setStatus(statusCode.value());
        problemDetail.setType(URI.create("https://www.drinkwater.com.br/validation-error"));

        BindingResult bindingResult = ex.getBindingResult();

        List<Map<String, String>> errors = bindingResult.getFieldErrors().stream()
            .map(fieldError -> {
                Map<String, String> error = new HashMap<>();
                error.put("field", fieldError.getField());
                error.put("message", this.messageSource.getMessage(fieldError, LocaleContextHolder.getLocale()));
                return error;
            })
            .collect(Collectors.toList());

        problemDetail.setProperty("errors", errors);

        return super.handleExceptionInternal(ex, problemDetail, headers, status, request);
    }
}
