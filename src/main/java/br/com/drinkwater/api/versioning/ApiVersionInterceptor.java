package br.com.drinkwater.api.versioning;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Injects API version headers into every response served by a controller annotated with {@link
 * ApiVersion}. When the version is deprecated, additional headers signal clients to migrate: {@code
 * API-Deprecated}, {@code Sunset}, and {@code Link}.
 *
 * <p>Header reference:
 *
 * <ul>
 *   <li>{@code API-Version} -- the version that served the request
 *   <li>{@code API-Deprecated} -- {@code true} when the version is deprecated
 *   <li>{@code Sunset} -- RFC 7231 date after which the version may be removed
 *   <li>{@code Link} -- {@code rel="successor-version"} pointing to the replacement
 * </ul>
 */
public class ApiVersionInterceptor implements HandlerInterceptor {

    static final String HEADER_API_VERSION = "API-Version";
    static final String HEADER_API_DEPRECATED = "API-Deprecated";
    static final String HEADER_SUNSET = "Sunset";
    static final String HEADER_LINK = "Link";

    private final ApiVersionProperties properties;

    public ApiVersionInterceptor(ApiVersionProperties properties) {
        this.properties = properties;
    }

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) {

        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        var apiVersion = handlerMethod.getBeanType().getAnnotation(ApiVersion.class);
        if (apiVersion == null) {
            return true;
        }

        String version = apiVersion.value();
        response.setHeader(HEADER_API_VERSION, version);

        boolean deprecated = resolveDeprecated(apiVersion, version);
        if (deprecated) {
            response.setHeader(HEADER_API_DEPRECATED, "true");

            String sunset = resolveSunset(apiVersion, version);
            if (!sunset.isBlank()) {
                response.setHeader(HEADER_SUNSET, sunset);
            }

            String successor = resolveSuccessor(apiVersion, version);
            if (!successor.isBlank()) {
                String currentPath = request.getRequestURI();
                String linkPath =
                        currentPath.replaceFirst("/" + version + "/", "/" + successor + "/");
                response.setHeader(
                        HEADER_LINK, "<%s>; rel=\"successor-version\"".formatted(linkPath));
            }
        }

        return true;
    }

    private boolean resolveDeprecated(ApiVersion annotation, String version) {
        if (annotation.deprecated()) {
            return true;
        }
        return properties.isVersionDeprecated(version);
    }

    private String resolveSunset(ApiVersion annotation, String version) {
        String fromAnnotation = annotation.sunset();
        if (!fromAnnotation.isBlank()) {
            return fromAnnotation;
        }
        return properties.getSunsetDate(version);
    }

    private String resolveSuccessor(ApiVersion annotation, String version) {
        String fromAnnotation = annotation.successorVersion();
        if (!fromAnnotation.isBlank()) {
            return fromAnnotation;
        }
        return properties.getSuccessorVersion(version);
    }
}
