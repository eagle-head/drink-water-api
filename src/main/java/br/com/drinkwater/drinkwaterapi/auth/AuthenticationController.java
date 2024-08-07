package br.com.drinkwater.drinkwaterapi.auth;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("auth")
    public String authenticate(Authentication authentication) {
        return authenticationService.authenticate(authentication);
    }
}
