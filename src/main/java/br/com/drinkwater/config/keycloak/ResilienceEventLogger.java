package br.com.drinkwater.config.keycloak;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ResilienceEventLogger {

    private static final Logger log = LoggerFactory.getLogger(ResilienceEventLogger.class);
    static final String KEYCLOAK_INSTANCE = "keycloak";

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;
    private final RateLimiterRegistry rateLimiterRegistry;

    public ResilienceEventLogger(
            CircuitBreakerRegistry circuitBreakerRegistry,
            RetryRegistry retryRegistry,
            RateLimiterRegistry rateLimiterRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.retryRegistry = retryRegistry;
        this.rateLimiterRegistry = rateLimiterRegistry;
    }

    @PostConstruct
    void registerEventListeners() {
        registerCircuitBreakerEvents();
        registerRetryEvents();
        registerRateLimiterEvents();
    }

    private void registerCircuitBreakerEvents() {
        var cb = circuitBreakerRegistry.circuitBreaker(KEYCLOAK_INSTANCE);
        var publisher = cb.getEventPublisher();

        publisher.onStateTransition(
                event ->
                        log.warn(
                                "Keycloak circuit breaker state transition: {} -> {}",
                                event.getStateTransition().getFromState(),
                                event.getStateTransition().getToState()));

        publisher.onError(
                event ->
                        log.warn(
                                "Keycloak circuit breaker recorded error: {} (duration: {}ms)",
                                event.getThrowable().getMessage(),
                                event.getElapsedDuration().toMillis()));

        publisher.onSuccess(
                event ->
                        log.debug(
                                "Keycloak circuit breaker call succeeded (duration: {}ms)",
                                event.getElapsedDuration().toMillis()));
    }

    private void registerRetryEvents() {
        var retry = retryRegistry.retry(KEYCLOAK_INSTANCE);
        var publisher = retry.getEventPublisher();

        publisher.onRetry(
                event ->
                        log.warn(
                                "Keycloak retry attempt #{}: {}",
                                event.getNumberOfRetryAttempts(),
                                event.getLastThrowable().getMessage()));

        publisher.onError(
                event ->
                        log.error(
                                "Keycloak retry exhausted after {} attempts: {}",
                                event.getNumberOfRetryAttempts(),
                                event.getLastThrowable().getMessage()));

        publisher.onSuccess(event -> log.debug("Keycloak call succeeded without retry"));
    }

    private void registerRateLimiterEvents() {
        rateLimiterRegistry
                .getAllRateLimiters()
                .forEach(
                        rateLimiter -> {
                            var publisher = rateLimiter.getEventPublisher();
                            publisher.onSuccess(
                                    event ->
                                            log.debug(
                                                    "RateLimiter [{}]: request permitted",
                                                    rateLimiter.getName()));
                            publisher.onFailure(
                                    event ->
                                            log.warn(
                                                    "RateLimiter [{}]: request rejected",
                                                    rateLimiter.getName()));
                        });
    }
}
