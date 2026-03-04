package br.com.drinkwater.config.keycloak;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

final class ResilienceEventLoggerTest {

    private CircuitBreakerRegistry circuitBreakerRegistry;
    private RetryRegistry retryRegistry;
    private RateLimiterRegistry rateLimiterRegistry;
    private ResilienceEventLogger resilienceEventLogger;

    @BeforeEach
    void setUp() {
        circuitBreakerRegistry = CircuitBreakerRegistry.ofDefaults();
        retryRegistry =
                RetryRegistry.of(
                        RetryConfig.custom()
                                .maxAttempts(2)
                                .waitDuration(Duration.ZERO)
                                .retryExceptions(RuntimeException.class, IOException.class)
                                .build());
        rateLimiterRegistry =
                RateLimiterRegistry.of(
                        RateLimiterConfig.custom()
                                .limitForPeriod(2)
                                .limitRefreshPeriod(Duration.ofMinutes(1))
                                .timeoutDuration(Duration.ZERO)
                                .build());
        rateLimiterRegistry.rateLimiter("test-limiter");
        resilienceEventLogger =
                new ResilienceEventLogger(
                        circuitBreakerRegistry, retryRegistry, rateLimiterRegistry);
    }

    @Test
    void givenRegistries_whenRegisterEventListeners_thenNoException() {
        assertThatCode(() -> resilienceEventLogger.registerEventListeners())
                .doesNotThrowAnyException();
    }

    @Test
    void givenRegisteredListeners_whenCircuitBreakerRecordsError_thenEventIsConsumed() {
        resilienceEventLogger.registerEventListeners();

        CircuitBreaker cb =
                circuitBreakerRegistry.circuitBreaker(ResilienceEventLogger.KEYCLOAK_INSTANCE);
        cb.onError(0, TimeUnit.MILLISECONDS, new IOException("test error"));

        assertThat(cb.getMetrics().getNumberOfFailedCalls()).isEqualTo(1);
    }

    @Test
    void givenRegisteredListeners_whenCircuitBreakerRecordsSuccess_thenEventIsConsumed() {
        resilienceEventLogger.registerEventListeners();

        CircuitBreaker cb =
                circuitBreakerRegistry.circuitBreaker(ResilienceEventLogger.KEYCLOAK_INSTANCE);
        cb.onSuccess(100, TimeUnit.MILLISECONDS);

        assertThat(cb.getMetrics().getNumberOfSuccessfulCalls()).isEqualTo(1);
    }

    @Test
    void givenRegisteredListeners_whenCircuitBreakerTransitionsState_thenEventIsConsumed() {
        var config =
                CircuitBreakerConfig.custom()
                        .slidingWindowSize(5)
                        .minimumNumberOfCalls(5)
                        .failureRateThreshold(50)
                        .build();
        var registry = CircuitBreakerRegistry.of(config);
        var logger = new ResilienceEventLogger(registry, retryRegistry, rateLimiterRegistry);
        logger.registerEventListeners();

        CircuitBreaker cb = registry.circuitBreaker(ResilienceEventLogger.KEYCLOAK_INSTANCE);
        for (int i = 0; i < 5; i++) {
            cb.onError(0, TimeUnit.MILLISECONDS, new IOException("test failure"));
        }

        assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    }

    @Test
    void givenRegisteredListeners_whenRetrySucceeds_thenSuccessEventIsConsumed() {
        resilienceEventLogger.registerEventListeners();

        Retry retry = retryRegistry.retry(ResilienceEventLogger.KEYCLOAK_INSTANCE);
        var decorated = Retry.decorateRunnable(retry, () -> {});
        decorated.run();

        assertThat(retry.getMetrics().getNumberOfSuccessfulCallsWithoutRetryAttempt()).isEqualTo(1);
    }

    @Test
    void givenRegisteredListeners_whenRetryExhausted_thenErrorEventIsConsumed() {
        resilienceEventLogger.registerEventListeners();

        Retry retry = retryRegistry.retry(ResilienceEventLogger.KEYCLOAK_INSTANCE);
        var decorated =
                Retry.decorateCheckedRunnable(
                        retry,
                        () -> {
                            throw new IOException("connection refused");
                        });

        try {
            decorated.run();
        } catch (Throwable ignored) {
        }

        assertThat(retry.getMetrics().getNumberOfFailedCallsWithRetryAttempt()).isEqualTo(1);
    }

    @Test
    void givenRegisteredListeners_whenRetryRecovers_thenRetryEventIsConsumed() {
        resilienceEventLogger.registerEventListeners();

        Retry retry = retryRegistry.retry(ResilienceEventLogger.KEYCLOAK_INSTANCE);
        var callCount = new AtomicInteger(0);
        var decorated =
                Retry.decorateCheckedRunnable(
                        retry,
                        () -> {
                            if (callCount.incrementAndGet() == 1) {
                                throw new IOException("temporary failure");
                            }
                        });

        try {
            decorated.run();
        } catch (Throwable ignored) {
        }

        assertThat(retry.getMetrics().getNumberOfSuccessfulCallsWithRetryAttempt()).isEqualTo(1);
    }

    @Test
    void givenKeycloakInstance_thenConstantMatchesExpectedValue() {
        assertThat(ResilienceEventLogger.KEYCLOAK_INSTANCE).isEqualTo("keycloak");
    }

    @Test
    void givenRegisteredListeners_whenRateLimiterPermits_thenSuccessEventIsConsumed() {
        resilienceEventLogger.registerEventListeners();

        RateLimiter rl = rateLimiterRegistry.rateLimiter("test-limiter");
        RateLimiter.waitForPermission(rl);

        assertThat(rl.getMetrics().getAvailablePermissions()).isLessThan(2);
    }

    @Test
    void givenRegisteredListeners_whenRateLimiterRejects_thenFailureEventIsConsumed() {
        resilienceEventLogger.registerEventListeners();

        RateLimiter rl = rateLimiterRegistry.rateLimiter("test-limiter");
        for (int i = 0; i < rl.getRateLimiterConfig().getLimitForPeriod(); i++) {
            RateLimiter.waitForPermission(rl);
        }

        assertThatThrownBy(() -> RateLimiter.waitForPermission(rl))
                .isInstanceOf(RequestNotPermitted.class);
    }
}
