package br.com.drinkwater.config.keycloak;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.drinkwater.config.MockContainersConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("it-no-containers")
@Import(MockContainersConfig.class)
final class KeycloakResilienceNoContainersIT {

    @Autowired private CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired private RetryRegistry retryRegistry;

    @Test
    void givenResilience4jConfigured_whenGetCircuitBreaker_thenKeycloakInstanceExists() {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("keycloak");

        assertThat(cb).isNotNull();
        assertThat(cb.getName()).isEqualTo("keycloak");
        assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
    }

    @Test
    void givenResilience4jConfigured_whenGetCircuitBreaker_thenConfigMatchesExpected() {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("keycloak");

        var config = cb.getCircuitBreakerConfig();
        assertThat(config.getSlidingWindowSize()).isEqualTo(5);
        assertThat(config.getFailureRateThreshold()).isEqualTo(50f);
        assertThat(config.getWaitIntervalFunctionInOpenState()).isNotNull();
        assertThat(config.isAutomaticTransitionFromOpenToHalfOpenEnabled()).isTrue();
    }

    @Test
    void givenResilience4jConfigured_whenGetRetry_thenKeycloakInstanceExists() {
        var retry = retryRegistry.retry("keycloak");

        assertThat(retry).isNotNull();
        assertThat(retry.getName()).isEqualTo("keycloak");
    }

    @Test
    void givenResilience4jConfigured_whenGetRetry_thenConfigMatchesExpected() {
        var retry = retryRegistry.retry("keycloak");

        var config = retry.getRetryConfig();
        assertThat(config.getMaxAttempts()).isEqualTo(2);
    }

    @Test
    void givenCircuitBreakerClosed_whenRecordFailures_thenStateTransitionsToOpen() {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("keycloak-test-transition");

        for (int i = 0; i < 10; i++) {
            cb.onError(
                    0,
                    java.util.concurrent.TimeUnit.MILLISECONDS,
                    new java.io.IOException("test failure"));
        }

        assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    }
}
