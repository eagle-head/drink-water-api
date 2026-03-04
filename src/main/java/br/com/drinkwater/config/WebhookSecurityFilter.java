package br.com.drinkwater.config;

import br.com.drinkwater.config.properties.WebhookProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Validates requests to {@code /internal/**} endpoints using a shared secret header. Rejects
 * requests with an invalid or missing {@code X-Webhook-Secret} header with HTTP 401.
 *
 * <p>Not a {@code @Component} — registered explicitly in {@link SecurityConfig} to avoid polluting
 * {@code @WebMvcTest} contexts that do not need webhook support.
 */
public class WebhookSecurityFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(WebhookSecurityFilter.class);

    static final String WEBHOOK_SECRET_HEADER = "X-Webhook-Secret";
    private static final String INTERNAL_PATH_PREFIX = "/internal/";

    private final WebhookProperties webhookProperties;

    public WebhookSecurityFilter(WebhookProperties webhookProperties) {
        this.webhookProperties = webhookProperties;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String secret = request.getHeader(WEBHOOK_SECRET_HEADER);

        if (!webhookProperties.secret().equals(secret)) {
            log.warn(
                    "Webhook request rejected: invalid or missing {}, remoteAddr={}",
                    WEBHOOK_SECRET_HEADER,
                    request.getRemoteAddr());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid webhook secret");
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith(INTERNAL_PATH_PREFIX);
    }
}
