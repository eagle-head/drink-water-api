package br.com.drinkwater.config.keycloak;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.drinkwater.config.MockContainersConfig;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("it-no-containers")
@Import(MockContainersConfig.class)
final class RateLimitingNoContainersIT {

    @Autowired private RateLimiterRegistry rateLimiterRegistry;

    @Test
    void givenResilience4jConfigured_whenGetRateLimiter_thenUserApiInstanceExists() {
        RateLimiter rl = rateLimiterRegistry.rateLimiter("user-api");

        assertThat(rl).isNotNull();
        assertThat(rl.getName()).isEqualTo("user-api");
    }

    @Test
    void givenResilience4jConfigured_whenGetRateLimiter_thenWaterintakeApiInstanceExists() {
        RateLimiter rl = rateLimiterRegistry.rateLimiter("waterintake-api");

        assertThat(rl).isNotNull();
        assertThat(rl.getName()).isEqualTo("waterintake-api");
    }

    @Test
    void givenResilience4jConfigured_whenGetRateLimiter_thenWaterintakeSearchInstanceExists() {
        RateLimiter rl = rateLimiterRegistry.rateLimiter("waterintake-search");

        assertThat(rl).isNotNull();
        assertThat(rl.getName()).isEqualTo("waterintake-search");
    }

    @Test
    void givenUserApiRateLimiter_whenCheckConfig_thenConfigMatchesTestProfile() {
        RateLimiter rl = rateLimiterRegistry.rateLimiter("user-api");

        var config = rl.getRateLimiterConfig();
        assertThat(config.getLimitForPeriod()).isEqualTo(1000);
        assertThat(config.getLimitRefreshPeriod()).isEqualTo(Duration.ofMinutes(1));
        assertThat(config.getTimeoutDuration()).isEqualTo(Duration.ZERO);
    }

    @Test
    void givenRateLimiter_whenLimitExceeded_thenRequestNotPermittedIsThrown() {
        var config =
                RateLimiterConfig.custom()
                        .limitForPeriod(2)
                        .limitRefreshPeriod(Duration.ofMinutes(1))
                        .timeoutDuration(Duration.ZERO)
                        .build();
        RateLimiter rl = rateLimiterRegistry.rateLimiter("exceed-test", config);

        for (int i = 0; i < 2; i++) {
            RateLimiter.waitForPermission(rl);
        }

        assertThatThrownBy(() -> RateLimiter.waitForPermission(rl))
                .isInstanceOf(RequestNotPermitted.class);
    }
}
