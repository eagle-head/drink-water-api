package br.com.drinkwater.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.drinkwater.config.keycloak.KeycloakOperationException;
import br.com.drinkwater.config.runtime.RuntimeConfigurationException;
import br.com.drinkwater.config.security.InsufficientScopeException;
import br.com.drinkwater.config.security.ScopeAwareAccessDeniedHandler;
import br.com.drinkwater.core.MessageResolver;
import br.com.drinkwater.hydrationtracking.exception.DuplicateDateTimeException;
import br.com.drinkwater.hydrationtracking.exception.WaterIntakeNotFoundException;
import br.com.drinkwater.usermanagement.exception.UserAlreadyExistsException;
import br.com.drinkwater.usermanagement.exception.UserNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
final class GlobalExceptionHandlerTest {

    private static final String TEST_MESSAGE = "test message";
    private static final String PROBLEM_DETAILS_BASE_URL = "https://www.drinkwater.com.br";

    @Mock private MessageResolver messageResolver;
    @Mock private MessageSource messageSource;
    @Mock private ScopeAwareAccessDeniedHandler scopeAwareAccessDeniedHandler;
    @Mock private WebRequest request;

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler =
                new GlobalExceptionHandler(
                        messageResolver, messageSource, scopeAwareAccessDeniedHandler);
        when(messageResolver.resolve(anyString())).thenReturn(TEST_MESSAGE);
        when(messageResolver.resolve(anyString(), any(Object[].class))).thenReturn(TEST_MESSAGE);
    }

    @Test
    void handleIllegalArgumentException_returnsBadRequest() {
        IllegalArgumentException ex = new IllegalArgumentException("invalid");

        ResponseEntity<Object> response = handler.handleIllegalArgumentException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ProblemDetail.class);
        ProblemDetail body = (ProblemDetail) response.getBody();
        assertThat(body.getDetail()).isEqualTo(TEST_MESSAGE);
        assertThat(body.getType()).hasToString(PROBLEM_DETAILS_BASE_URL + "/invalid-argument");
    }

    @Test
    void handleInsufficientScopeException_returnsForbiddenWithRequiredScope() {
        InsufficientScopeException ex = new InsufficientScopeException("drinkwater:read");

        ResponseEntity<Object> response = handler.handleInsufficientScopeException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isInstanceOf(ProblemDetail.class);
        ProblemDetail body = (ProblemDetail) response.getBody();
        assertThat(body.getDetail()).isEqualTo(TEST_MESSAGE);
        assertThat(body.getProperties()).isNotNull();
        assertThat(body.getProperties().get("required_scope")).isEqualTo("drinkwater:read");
        assertThat(body.getType()).hasToString(PROBLEM_DETAILS_BASE_URL + "/insufficient-scope");
    }

    @Test
    void handleAuthorizationDeniedException_withoutScope_returnsForbidden() {
        AuthorizationDeniedException ex = new AuthorizationDeniedException("Access Denied");
        when(scopeAwareAccessDeniedHandler.extractMissingScope(ex)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = handler.handleAuthorizationDeniedException(ex, request);

        verify(scopeAwareAccessDeniedHandler).extractMissingScope(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isInstanceOf(ProblemDetail.class);
        ProblemDetail body = (ProblemDetail) response.getBody();
        assertThat(body.getDetail()).isEqualTo(TEST_MESSAGE);
        assertThat(body.getType()).hasToString(PROBLEM_DETAILS_BASE_URL + "/forbidden");
    }

    @Test
    void handleAuthorizationDeniedException_withMissingScope_returnsForbiddenWithScope() {
        AuthorizationDeniedException ex = new AuthorizationDeniedException("Access Denied");
        when(scopeAwareAccessDeniedHandler.extractMissingScope(ex))
                .thenReturn(Optional.of("drinkwater:v1:user:profile:read"));

        ResponseEntity<Object> response = handler.handleAuthorizationDeniedException(ex, request);

        verify(scopeAwareAccessDeniedHandler).extractMissingScope(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isInstanceOf(ProblemDetail.class);
        ProblemDetail body = (ProblemDetail) response.getBody();
        assertThat(body.getDetail()).isEqualTo(TEST_MESSAGE);
        assertThat(body.getProperties()).isNotNull();
        assertThat(body.getProperties().get("required_scope"))
                .isEqualTo("drinkwater:v1:user:profile:read");
        assertThat(body.getType()).hasToString(PROBLEM_DETAILS_BASE_URL + "/insufficient-scope");
    }

    @Test
    void handleWaterIntakeNotFoundException_returnsNotFound() {
        WaterIntakeNotFoundException ex = new WaterIntakeNotFoundException("Not found");

        ResponseEntity<Object> response = handler.handleWaterIntakeNotFoundException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isInstanceOf(ProblemDetail.class);
        ProblemDetail body = (ProblemDetail) response.getBody();
        assertThat(body.getDetail()).isEqualTo(TEST_MESSAGE);
        assertThat(body.getType()).hasToString(PROBLEM_DETAILS_BASE_URL + "/waterintake-not-found");
    }

    @Test
    void handleDuplicateDateTimeException_returnsBadRequest() {
        DuplicateDateTimeException ex = new DuplicateDateTimeException("Duplicate datetime");

        ResponseEntity<Object> response = handler.handleDuplicateDateTimeException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ProblemDetail.class);
        ProblemDetail body = (ProblemDetail) response.getBody();
        assertThat(body.getDetail()).isEqualTo(TEST_MESSAGE);
        assertThat(body.getType())
                .hasToString(PROBLEM_DETAILS_BASE_URL + "/time-range-validation-error");
    }

    @Test
    void handleUserNotFoundException_returnsNotFound() {
        UserNotFoundException ex = new UserNotFoundException("User not found");

        ResponseEntity<Object> response = handler.handleUserNotFoundException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isInstanceOf(ProblemDetail.class);
        ProblemDetail body = (ProblemDetail) response.getBody();
        assertThat(body.getDetail()).isEqualTo(TEST_MESSAGE);
        assertThat(body.getType()).hasToString(PROBLEM_DETAILS_BASE_URL + "/user-not-found");
    }

    @Test
    void handleUserAlreadyExistsException_returnsConflict() {
        UserAlreadyExistsException ex = new UserAlreadyExistsException("User exists");

        ResponseEntity<Object> response = handler.handleUserAlreadyExistsException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isInstanceOf(ProblemDetail.class);
        ProblemDetail body = (ProblemDetail) response.getBody();
        assertThat(body.getDetail()).isEqualTo(TEST_MESSAGE);
        assertThat(body.getType()).hasToString(PROBLEM_DETAILS_BASE_URL + "/user-already-exists");
    }

    @Test
    void handleRuntimeConfigurationException_returnsInternalServerError() {
        RuntimeConfigurationException ex = new RuntimeConfigurationException("Config error");

        ResponseEntity<Object> response = handler.handleRuntimeConfigurationException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isInstanceOf(ProblemDetail.class);
        ProblemDetail body = (ProblemDetail) response.getBody();
        assertThat(body.getDetail()).isEqualTo(TEST_MESSAGE);
        assertThat(body.getType())
                .hasToString(PROBLEM_DETAILS_BASE_URL + "/runtime-configuration-error");
    }

    @Test
    void handleKeycloakOperationException_returnsServiceUnavailable() {
        KeycloakOperationException ex =
                new KeycloakOperationException(
                        "Keycloak unavailable", new RuntimeException("connection timeout"));

        ResponseEntity<Object> response = handler.handleKeycloakOperationException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isInstanceOf(ProblemDetail.class);
        ProblemDetail body = (ProblemDetail) response.getBody();
        assertThat(body.getDetail()).isEqualTo(TEST_MESSAGE);
        assertThat(body.getType()).hasToString(PROBLEM_DETAILS_BASE_URL + "/keycloak-unavailable");
    }

    @Test
    void handleRateLimitExceeded_returnsTooManyRequests() {
        RequestNotPermitted ex = mock(RequestNotPermitted.class);

        ResponseEntity<Object> response = handler.handleRateLimitExceeded(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(response.getBody()).isInstanceOf(ProblemDetail.class);
        ProblemDetail body = (ProblemDetail) response.getBody();
        assertThat(body.getDetail()).isEqualTo(TEST_MESSAGE);
        assertThat(body.getType()).hasToString(PROBLEM_DETAILS_BASE_URL + "/rate-limit-exceeded");
        assertThat(response.getHeaders().getFirst("Retry-After")).isEqualTo("60");
    }

    @Test
    void handleAllExceptions_returnsInternalServerError() {
        Exception ex = new Exception("Unexpected");

        ResponseEntity<Object> response = handler.handleAllExceptions(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isInstanceOf(ProblemDetail.class);
        ProblemDetail body = (ProblemDetail) response.getBody();
        assertThat(body.getDetail()).isEqualTo(TEST_MESSAGE);
        assertThat(body.getType()).hasToString(PROBLEM_DETAILS_BASE_URL + "/internal-server-error");
    }

    @Test
    void handleHttpMessageNotReadable_withInvalidFormatException_addsErrorsProperty()
            throws Exception {
        InvalidFormatException invalidFormat = createInvalidFormatException();
        HttpMessageNotReadableException ex =
                new HttpMessageNotReadableException("Cannot read", invalidFormat, null);
        HttpHeaders headers = new HttpHeaders();
        HttpStatusCode statusCode = HttpStatusCode.valueOf(400);

        ResponseEntity<Object> response =
                handler.handleHttpMessageNotReadable(ex, headers, statusCode, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ProblemDetail.class);
        ProblemDetail body = (ProblemDetail) response.getBody();
        assertThat(body.getDetail()).isEqualTo(TEST_MESSAGE);
        assertThat(body.getProperties()).isNotNull();
        assertThat(body.getProperties().get("errors")).isNotNull();
        @SuppressWarnings("unchecked")
        List<Object> errors = (List<Object>) body.getProperties().get("errors");
        assertThat(errors).isNotEmpty();
        verify(messageResolver).resolve(eq("validation.invalid-value"), any(Object[].class));
    }

    @Test
    void handleHttpMessageNotReadable_withNullCause_hasNoExtras() {
        HttpMessageNotReadableException ex =
                new HttpMessageNotReadableException("Cannot read", null, null);
        HttpHeaders headers = new HttpHeaders();
        HttpStatusCode statusCode = HttpStatusCode.valueOf(400);

        ResponseEntity<Object> response =
                handler.handleHttpMessageNotReadable(ex, headers, statusCode, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ProblemDetail.class);
        ProblemDetail body = (ProblemDetail) response.getBody();
        assertThat(body.getDetail()).isEqualTo(TEST_MESSAGE);
        assertThat(body.getProperties()).isNull();
    }

    @Test
    void handleHttpMessageNotReadable_withOtherThrowableCause_addsCauseMessage() {
        Throwable cause = new RuntimeException("Parse failed");
        HttpMessageNotReadableException ex =
                new HttpMessageNotReadableException("Cannot read", cause, null);
        HttpHeaders headers = new HttpHeaders();
        HttpStatusCode statusCode = HttpStatusCode.valueOf(400);

        ResponseEntity<Object> response =
                handler.handleHttpMessageNotReadable(ex, headers, statusCode, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ProblemDetail.class);
        ProblemDetail body = (ProblemDetail) response.getBody();
        assertThat(body.getDetail()).isEqualTo(TEST_MESSAGE);
        assertThat(body.getProperties()).isNotNull();
        assertThat(body.getProperties().get("cause")).isEqualTo("Parse failed");
    }

    @Test
    void handleMethodArgumentNotValid_withFieldErrorsAndGlobalErrors_returnsBadRequest() {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(target, "testObject");
        bindingResult.addError(new FieldError("testObject", "field1", "must not be null"));
        bindingResult.addError(
                new ObjectError("testObject", new String[] {"error.code"}, null, "global error"));
        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, bindingResult);
        HttpHeaders headers = new HttpHeaders();
        HttpStatusCode statusCode = HttpStatusCode.valueOf(400);

        when(messageSource.getMessage(
                        any(org.springframework.context.MessageSourceResolvable.class),
                        eq(LocaleContextHolder.getLocale())))
                .thenReturn("field error msg", "global error msg");

        ResponseEntity<Object> response =
                handler.handleMethodArgumentNotValid(ex, headers, statusCode, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ProblemDetail.class);
        ProblemDetail body = (ProblemDetail) response.getBody();
        assertThat(body.getDetail()).isEqualTo(TEST_MESSAGE);
        assertThat(body.getProperties()).isNotNull();
        assertThat(body.getProperties().get("errors")).isNotNull();
        @SuppressWarnings("unchecked")
        List<Object> errors = (List<Object>) body.getProperties().get("errors");
        assertThat(errors).hasSize(2);
    }

    @Test
    void handleMethodArgumentNotValid_withTypeMismatchFieldError_usesDatetimeMessage() {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(target, "testObject");
        bindingResult.addError(
                new FieldError(
                        "testObject",
                        "dateField",
                        "invalid",
                        false,
                        new String[] {"typeMismatch.dateField", "typeMismatch"},
                        null,
                        "invalid date"));
        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, bindingResult);
        HttpHeaders headers = new HttpHeaders();
        HttpStatusCode statusCode = HttpStatusCode.valueOf(400);

        when(messageSource.getMessage(
                        any(org.springframework.context.MessageSourceResolvable.class),
                        eq(LocaleContextHolder.getLocale())))
                .thenReturn("original message");

        ResponseEntity<Object> response =
                handler.handleMethodArgumentNotValid(ex, headers, statusCode, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ProblemDetail.class);
        ProblemDetail body = (ProblemDetail) response.getBody();
        assertThat(body.getProperties()).isNotNull();
        assertThat(body.getProperties().get("errors")).isNotNull();
        verify(messageResolver).resolve("validation.datetime.invalid-format");
    }

    @Test
    void handleConstraintViolation_withViolations_addsErrors() {
        @SuppressWarnings("unchecked")
        ConstraintViolation<Object> violation1 = mock(ConstraintViolation.class);
        Path path1 = mock(Path.class);
        when(path1.toString()).thenReturn("parent.fieldName");
        when(violation1.getPropertyPath()).thenReturn(path1);
        when(violation1.getMessage()).thenReturn("must not be null");
        when(violation1.getInvalidValue()).thenReturn("invalid");

        @SuppressWarnings("unchecked")
        ConstraintViolation<Object> violation2 = mock(ConstraintViolation.class);
        Path path2 = mock(Path.class);
        when(path2.toString()).thenReturn("simpleField");
        when(violation2.getPropertyPath()).thenReturn(path2);
        when(violation2.getMessage()).thenReturn("must be positive");
        when(violation2.getInvalidValue()).thenReturn(null);

        ConstraintViolationException ex =
                new ConstraintViolationException(
                        "validation failed", Set.of(violation1, violation2));

        ResponseEntity<Object> response = handler.handleConstraintViolation(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ProblemDetail.class);
        ProblemDetail body = (ProblemDetail) response.getBody();
        assertThat(body.getDetail()).isEqualTo(TEST_MESSAGE);
        assertThat(body.getProperties()).isNotNull();
        assertThat(body.getProperties().get("errors")).isNotNull();
        @SuppressWarnings("unchecked")
        List<Object> errors = (List<Object>) body.getProperties().get("errors");
        assertThat(errors).hasSize(2);
    }

    @Test
    void handleMethodArgumentTypeMismatch_withValueAndRequiredType_returnsBadRequest() {
        MethodArgumentTypeMismatchException ex =
                new MethodArgumentTypeMismatchException("invalid", Integer.class, "id", null, null);

        ResponseEntity<Object> response = handler.handleMethodArgumentTypeMismatch(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ProblemDetail.class);
        ProblemDetail body = (ProblemDetail) response.getBody();
        assertThat(body.getDetail()).isEqualTo(TEST_MESSAGE);
        assertThat(body.getProperties()).isNotNull();
        assertThat(body.getProperties().get("errors")).isNotNull();
        @SuppressWarnings("unchecked")
        List<Object> errors = (List<Object>) body.getProperties().get("errors");
        assertThat(errors).hasSize(1);
        verify(messageResolver)
                .resolve(eq("validation.type-mismatch.field"), eq("id"), eq("Integer"));
    }

    @Test
    void handleMethodArgumentTypeMismatch_withNullRequiredType_usesCorrectType() {
        MethodArgumentTypeMismatchException ex =
                new MethodArgumentTypeMismatchException("invalid", null, "param", null, null);

        ResponseEntity<Object> response = handler.handleMethodArgumentTypeMismatch(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(messageResolver)
                .resolve(eq("validation.type-mismatch.field"), eq("param"), eq("correct type"));
    }

    @Test
    void handleMethodArgumentTypeMismatch_withNullValue_noInvalidValueInError() {
        MethodArgumentTypeMismatchException ex =
                new MethodArgumentTypeMismatchException(null, String.class, "name", null, null);

        ResponseEntity<Object> response = handler.handleMethodArgumentTypeMismatch(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ProblemDetail.class);
        ProblemDetail body = (ProblemDetail) response.getBody();
        assertThat(body.getProperties()).isNotNull();
        @SuppressWarnings("unchecked")
        List<Object> errors = (List<Object>) body.getProperties().get("errors");
        assertThat(errors).hasSize(1);
    }

    @Test
    void handleMissingServletRequestParameter_returnsBadRequest() {
        MissingServletRequestParameterException ex =
                new MissingServletRequestParameterException("requiredParam", "String");
        HttpHeaders headers = new HttpHeaders();
        HttpStatusCode status = HttpStatusCode.valueOf(400);

        ResponseEntity<Object> response =
                handler.handleMissingServletRequestParameter(ex, headers, status, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ProblemDetail.class);
        ProblemDetail body = (ProblemDetail) response.getBody();
        assertThat(body.getDetail()).isEqualTo(TEST_MESSAGE);
        assertThat(body.getProperties()).isNotNull();
        assertThat(body.getProperties().get("errors")).isNotNull();
        verify(messageResolver)
                .resolve(eq("validation.missing-parameter.field"), eq("requiredParam"));
    }

    private InvalidFormatException createInvalidFormatException() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.readValue("{\"volumeUnit\": \"invalid\"}", VolumeUnitTestDto.class);
        } catch (InvalidFormatException e) {
            return e;
        }
        throw new AssertionError("Expected InvalidFormatException");
    }

    @SuppressWarnings("unused")
    private static class VolumeUnitTestDto {
        public br.com.drinkwater.hydrationtracking.model.VolumeUnit volumeUnit;
    }
}
