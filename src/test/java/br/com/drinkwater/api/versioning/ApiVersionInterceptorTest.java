package br.com.drinkwater.api.versioning;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

final class ApiVersionInterceptorTest {

    private ApiVersionProperties properties;
    private ApiVersionInterceptor interceptor;

    @BeforeEach
    void setUp() {
        properties =
                new ApiVersionProperties(
                        "v2",
                        List.of(
                                new ApiVersionProperties.VersionConfig(
                                        "v1", true, "2026-12-31", "v2"),
                                new ApiVersionProperties.VersionConfig("v2", false, "", "")));

        interceptor = new ApiVersionInterceptor(properties);
    }

    @Test
    void givenActiveVersion_whenPreHandle_thenSetApiVersionHeader() throws Exception {
        // Given
        var request = new MockHttpServletRequest("GET", "/api/v2/users/me");
        var response = new MockHttpServletResponse();
        var handler = createHandlerForController(ActiveController.class);

        // When
        boolean result = interceptor.preHandle(request, response, handler);

        // Then
        assertThat(result).isTrue();
        assertThat(response.getHeader(ApiVersionInterceptor.HEADER_API_VERSION)).isEqualTo("v2");
        assertThat(response.getHeader(ApiVersionInterceptor.HEADER_API_DEPRECATED)).isNull();
        assertThat(response.getHeader(ApiVersionInterceptor.HEADER_SUNSET)).isNull();
        assertThat(response.getHeader(ApiVersionInterceptor.HEADER_LINK)).isNull();
    }

    @Test
    void givenDeprecatedVersion_whenPreHandle_thenSetDeprecationHeaders() throws Exception {
        // Given
        var request = new MockHttpServletRequest("GET", "/api/v1/users/me");
        var response = new MockHttpServletResponse();
        var handler = createHandlerForController(DeprecatedController.class);

        // When
        boolean result = interceptor.preHandle(request, response, handler);

        // Then
        assertThat(result).isTrue();
        assertThat(response.getHeader(ApiVersionInterceptor.HEADER_API_VERSION)).isEqualTo("v1");
        assertThat(response.getHeader(ApiVersionInterceptor.HEADER_API_DEPRECATED))
                .isEqualTo("true");
        assertThat(response.getHeader(ApiVersionInterceptor.HEADER_SUNSET)).isEqualTo("2026-12-31");
        assertThat(response.getHeader(ApiVersionInterceptor.HEADER_LINK))
                .isEqualTo("</api/v2/users/me>; rel=\"successor-version\"");
    }

    @Test
    void givenDeprecatedVersionFromProperties_whenPreHandle_thenSetDeprecationHeaders()
            throws Exception {
        // Given
        var request = new MockHttpServletRequest("GET", "/api/v1/users");
        var response = new MockHttpServletResponse();
        var handler = createHandlerForController(MinimalDeprecatedController.class);

        // When
        boolean result = interceptor.preHandle(request, response, handler);

        // Then
        assertThat(result).isTrue();
        assertThat(response.getHeader(ApiVersionInterceptor.HEADER_API_VERSION)).isEqualTo("v1");
        assertThat(response.getHeader(ApiVersionInterceptor.HEADER_API_DEPRECATED))
                .isEqualTo("true");
        assertThat(response.getHeader(ApiVersionInterceptor.HEADER_SUNSET)).isEqualTo("2026-12-31");
        assertThat(response.getHeader(ApiVersionInterceptor.HEADER_LINK))
                .isEqualTo("</api/v2/users>; rel=\"successor-version\"");
    }

    @Test
    void givenAnnotationOverridesProperties_whenPreHandle_thenUseAnnotationValues()
            throws Exception {
        // Given
        var request = new MockHttpServletRequest("GET", "/api/v1/data");
        var response = new MockHttpServletResponse();
        var handler = createHandlerForController(AnnotationOverrideController.class);

        // When
        boolean result = interceptor.preHandle(request, response, handler);

        // Then
        assertThat(result).isTrue();
        assertThat(response.getHeader(ApiVersionInterceptor.HEADER_API_VERSION)).isEqualTo("v1");
        assertThat(response.getHeader(ApiVersionInterceptor.HEADER_API_DEPRECATED))
                .isEqualTo("true");
        assertThat(response.getHeader(ApiVersionInterceptor.HEADER_SUNSET)).isEqualTo("2027-06-30");
        assertThat(response.getHeader(ApiVersionInterceptor.HEADER_LINK))
                .isEqualTo("</api/v3/data>; rel=\"successor-version\"");
    }

    @Test
    void givenNoApiVersionAnnotation_whenPreHandle_thenNoHeadersSet() throws Exception {
        // Given
        var request = new MockHttpServletRequest("GET", "/management/health");
        var response = new MockHttpServletResponse();
        var handler = createHandlerForController(UnannotatedController.class);

        // When
        boolean result = interceptor.preHandle(request, response, handler);

        // Then
        assertThat(result).isTrue();
        assertThat(response.getHeader(ApiVersionInterceptor.HEADER_API_VERSION)).isNull();
    }

    @Test
    void givenNonHandlerMethod_whenPreHandle_thenReturnTrue() throws Exception {
        // Given
        var request = new MockHttpServletRequest("GET", "/static/file");
        var response = new MockHttpServletResponse();

        // When
        boolean result = interceptor.preHandle(request, response, "not-a-handler");

        // Then
        assertThat(result).isTrue();
        assertThat(response.getHeader(ApiVersionInterceptor.HEADER_API_VERSION)).isNull();
    }

    @Test
    void givenDeprecatedWithNoSunsetOrSuccessor_whenPreHandle_thenOnlyDeprecatedHeader()
            throws Exception {
        // Given
        var noSunsetProperties =
                new ApiVersionProperties(
                        "v2", List.of(new ApiVersionProperties.VersionConfig("v1", true, "", "")));
        var noSunsetInterceptor = new ApiVersionInterceptor(noSunsetProperties);

        var request = new MockHttpServletRequest("GET", "/api/v1/users");
        var response = new MockHttpServletResponse();
        var handler = createHandlerForController(MinimalDeprecatedController.class);

        // When
        boolean result = noSunsetInterceptor.preHandle(request, response, handler);

        // Then
        assertThat(result).isTrue();
        assertThat(response.getHeader(ApiVersionInterceptor.HEADER_API_DEPRECATED))
                .isEqualTo("true");
        assertThat(response.getHeader(ApiVersionInterceptor.HEADER_SUNSET)).isNull();
        assertThat(response.getHeader(ApiVersionInterceptor.HEADER_LINK)).isNull();
    }

    private HandlerMethod createHandlerForController(Class<?> controllerClass) {
        var handlerMethod = mock(HandlerMethod.class);
        doReturn(controllerClass).when(handlerMethod).getBeanType();
        return handlerMethod;
    }

    @ApiVersion("v2")
    private static class ActiveController {}

    @ApiVersion(value = "v1", deprecated = true, sunset = "2026-12-31", successorVersion = "v2")
    private static class DeprecatedController {}

    @ApiVersion("v1")
    private static class MinimalDeprecatedController {}

    @ApiVersion(value = "v1", deprecated = true, sunset = "2027-06-30", successorVersion = "v3")
    private static class AnnotationOverrideController {}

    private static class UnannotatedController {}
}
