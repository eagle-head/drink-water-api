package br.com.drinkwater.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Component
@Profile("!it-no-containers")
public class TestAuthProvider {

    private static final String GRANT_TYPE = "password";
    private static final String CLIENT_ID = "drinkwaterapp";
    private static final String OAUTH2_TOKEN_ENDPOINT = "/protocol/openid-connect/token";

    private static final String JOHN_DOE_USERNAME = "john.doe@test.com";
    private static final String JOHN_DOE_PASSWORD = "123456";

    private static final String NEW_USER_USERNAME = "new.user@test.com";
    private static final String NEW_USER_PASSWORD = "123456";

    @Autowired
    private OAuth2ResourceServerProperties properties;

    public String getJohnDoeToken() {
        return getTokenForCredentials(JOHN_DOE_USERNAME, JOHN_DOE_PASSWORD);
    }

    public String getNewUserToken() {
        return getTokenForCredentials(NEW_USER_USERNAME, NEW_USER_PASSWORD);
    }

    private String getTokenForCredentials(String username, String password) {
        String issuerUri = properties.getJwt().getIssuerUri();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();

        form.put("grant_type", Collections.singletonList(GRANT_TYPE));
        form.put("client_id", Collections.singletonList(CLIENT_ID));
        form.put("username", Collections.singletonList(username));
        form.put("password", Collections.singletonList(password));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

        String tokenUrl = issuerUri + OAUTH2_TOKEN_ENDPOINT;
        KeycloakToken token = restTemplate.postForObject(tokenUrl, request, KeycloakToken.class);
        if (token == null) {
            throw new IllegalStateException("Failed to obtain the Keycloak token.");
        }

        return token.accessToken();
    }
}