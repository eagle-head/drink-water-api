package br.com.drinkwater.config.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

@ExtendWith(MockitoExtension.class)
final class AuthenticatedUserArgumentResolverTest {

    private static final UUID EXPECTED_UUID =
            UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    private final AuthenticatedUserArgumentResolver resolver =
            new AuthenticatedUserArgumentResolver();

    @Mock private MethodParameter parameter;

    @Mock private ModelAndViewContainer mavContainer;

    @Mock private NativeWebRequest webRequest;

    @Mock private WebDataBinderFactory binderFactory;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void supportsParameter_returnsTrue_whenAnnotationAndUuidType() {
        when(parameter.hasParameterAnnotation(AuthenticatedUser.class)).thenReturn(true);
        doReturn(UUID.class).when(parameter).getParameterType();

        boolean result = resolver.supportsParameter(parameter);

        assertThat(result).isTrue();
    }

    @Test
    void supportsParameter_returnsFalse_whenNoAnnotation() {
        when(parameter.hasParameterAnnotation(AuthenticatedUser.class)).thenReturn(false);

        boolean result = resolver.supportsParameter(parameter);

        assertThat(result).isFalse();
    }

    @Test
    void supportsParameter_returnsFalse_whenWrongType() {
        when(parameter.hasParameterAnnotation(AuthenticatedUser.class)).thenReturn(true);
        doReturn(String.class).when(parameter).getParameterType();

        boolean result = resolver.supportsParameter(parameter);

        assertThat(result).isFalse();
    }

    @Test
    void resolveArgument_returnsUuidFromJwtSubject() throws Exception {
        Jwt jwt =
                Jwt.withTokenValue("token")
                        .header("alg", "RS256")
                        .subject(EXPECTED_UUID.toString())
                        .claim("scope", "openid")
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(300))
                        .build();
        JwtAuthenticationToken authToken = new JwtAuthenticationToken(jwt);
        SecurityContextHolder.getContext().setAuthentication(authToken);

        UUID result = resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        assertThat(result).isEqualTo(EXPECTED_UUID);
    }

    @Test
    void resolveArgument_throwsIllegalStateException_whenNotJwtAuthenticationToken() {
        SecurityContextHolder.getContext()
                .setAuthentication(
                        new org.springframework.security.authentication
                                .UsernamePasswordAuthenticationToken("user", "password"));

        assertThatThrownBy(
                        () ->
                                resolver.resolveArgument(
                                        parameter, mavContainer, webRequest, binderFactory))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Expected JwtAuthenticationToken but got:")
                .hasMessageContaining("UsernamePasswordAuthenticationToken");
    }

    @Test
    void resolveArgument_throwsIllegalStateException_whenAuthenticationIsNull() {
        SecurityContextHolder.clearContext();

        assertThatThrownBy(
                        () ->
                                resolver.resolveArgument(
                                        parameter, mavContainer, webRequest, binderFactory))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Expected JwtAuthenticationToken but got:")
                .hasMessageContaining("null");
    }
}
