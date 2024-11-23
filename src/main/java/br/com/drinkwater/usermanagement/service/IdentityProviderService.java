package br.com.drinkwater.usermanagement.service;

import br.com.drinkwater.usermanagement.model.User;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IdentityProviderService {

    private final Keycloak keycloak;
    private final String realm;

    public IdentityProviderService(Keycloak keycloak, @Value("${keycloak.realm}") String realm) {
        this.keycloak = keycloak;
        this.realm = realm;
    }

    public String register(User user, String password) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);

        UserRepresentation keycloakUser = new UserRepresentation();
        keycloakUser.setUsername(user.getEmail());
        keycloakUser.setFirstName(user.getFirstName());
        keycloakUser.setLastName(user.getLastName());
        keycloakUser.setEmail(user.getEmail());
        keycloakUser.setCredentials(List.of(credential));
        keycloakUser.setEnabled(true);
        keycloakUser.setRealmRoles(List.of("user"));
        var response = keycloak.realm(this.realm).users().create(keycloakUser);

        return "";

    }
}