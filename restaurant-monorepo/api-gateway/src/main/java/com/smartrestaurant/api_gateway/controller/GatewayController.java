package com.smartrestaurant.api_gateway.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
public class GatewayController {
    @GetMapping("/")
    public Mono<String> home(@AuthenticationPrincipal OidcUser principal) {
        if (principal != null) {
            return Mono.just("Welcome to Smart Restaurant, " + principal.getFullName() +
                    "! \nEmail: " + principal.getEmail() +
                    "\nRoles: " + principal.getAuthorities());
        }
        return Mono.just("Welcome Smart Restaurant! Please login.");
    }

    @GetMapping("/access-token")
    public Mono<String> getAccessToken(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient client) {
        OAuth2AccessToken token = client.getAccessToken();
        return Mono.just("Access Token: " + token.getTokenValue());
    }

    @GetMapping("/id-token")
    public Mono<Map<String, Object>> getIdToken(@AuthenticationPrincipal OidcUser oidcUser) {
        Map<String, Object> info = new HashMap<>();
        if (oidcUser != null) {
            info.put("idToken", oidcUser.getIdToken().getTokenValue());
            info.put("claims", oidcUser.getClaims());
            info.put("authorities", oidcUser.getAuthorities());
        } else {
            info.put("message", "User not logged in");
        }
        return Mono.just(info);
    }
}
