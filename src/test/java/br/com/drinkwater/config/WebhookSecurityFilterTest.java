package br.com.drinkwater.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import br.com.drinkwater.config.properties.WebhookProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@ExtendWith(MockitoExtension.class)
final class WebhookSecurityFilterTest {

    private static final String VALID_SECRET = "valid-webhook-secret";

    @Mock private FilterChain filterChain;

    @Test
    void doFilterInternal_withValidSecret_callsFilterChain() throws ServletException, IOException {
        WebhookProperties properties = new WebhookProperties(VALID_SECRET);
        WebhookSecurityFilter filter = new WebhookSecurityFilter(properties);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/internal/webhook");
        request.addHeader(WebhookSecurityFilter.WEBHOOK_SECRET_HEADER, VALID_SECRET);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(response.getStatus()).isNotEqualTo(401);
    }

    @Test
    void doFilterInternal_withInvalidSecret_sendsError401() throws ServletException, IOException {
        WebhookProperties properties = new WebhookProperties(VALID_SECRET);
        WebhookSecurityFilter filter = new WebhookSecurityFilter(properties);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/internal/webhook");
        request.addHeader(WebhookSecurityFilter.WEBHOOK_SECRET_HEADER, "wrong-secret");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        verify(filterChain, never()).doFilter(any(), any());
        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    void doFilterInternal_withMissingSecret_sendsError401() throws ServletException, IOException {
        WebhookProperties properties = new WebhookProperties(VALID_SECRET);
        WebhookSecurityFilter filter = new WebhookSecurityFilter(properties);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/internal/webhook");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        verify(filterChain, never()).doFilter(any(), any());
        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    void shouldNotFilter_returnsTrue_forNonInternalPath() throws ServletException, IOException {
        WebhookProperties properties = new WebhookProperties(VALID_SECRET);
        WebhookSecurityFilter filter = new WebhookSecurityFilter(properties);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/users");

        boolean result = filter.shouldNotFilter(request);

        assertThat(result).isTrue();
    }

    @Test
    void shouldNotFilter_returnsFalse_forInternalPath() throws ServletException, IOException {
        WebhookProperties properties = new WebhookProperties(VALID_SECRET);
        WebhookSecurityFilter filter = new WebhookSecurityFilter(properties);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/internal/webhook");

        boolean result = filter.shouldNotFilter(request);

        assertThat(result).isFalse();
    }
}
