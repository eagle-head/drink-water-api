package br.com.drinkwater.support;

import com.fasterxml.jackson.annotation.JsonProperty;

record KeycloakToken(@JsonProperty("access_token") String accessToken) {}
