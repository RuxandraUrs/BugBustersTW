package com.smartrestaurant.api_gateway.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges

                        .pathMatchers("/public/**", "/login/**").permitAll()


                        .pathMatchers(HttpMethod.POST, "/restaurant/api/dishes").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/restaurant/api/dishes/{id}").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/restaurant/api/dishes/{id}").hasRole("ADMIN")

                        .pathMatchers(HttpMethod.GET, "/restaurant/api/dishes/{id}").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/restaurant/api/dishes").authenticated()
                        .pathMatchers(HttpMethod.GET, "/restaurant/api/dishes/search").authenticated()
                        .pathMatchers(HttpMethod.GET, "/restaurant/api/dishes/filter").authenticated()
                        .pathMatchers(HttpMethod.GET, "/restaurant/api/dishes/sorted").authenticated()


                        .pathMatchers(HttpMethod.POST, "/restaurant/api/categories").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/restaurant/api/categories/{id}").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/restaurant/api/categories/{id}").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/restaurant/api/categories/{id}").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/restaurant/api/categories").authenticated()


                        .pathMatchers(HttpMethod.POST, "/restaurant/api/ingredients").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/restaurant/api/ingredients").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/restaurant/api/ingredients/{id}").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/restaurant/api/ingredients").authenticated()


                        .pathMatchers(HttpMethod.POST, "/restaurant/api/orders").hasRole("CLIENT")
                        .pathMatchers(HttpMethod.GET, "/restaurant/api/orders").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/restaurant/api/orders/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PATCH, "/restaurant/api/orders/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/restaurant/api/orders/**").hasRole("ADMIN")


                        .pathMatchers(HttpMethod.POST, "/restaurant/api/users/create").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/restaurant/api/users").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/restaurant/api/users/{id}").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/restaurant/api/users/delete/{email}").hasRole("CLIENT")
                        .pathMatchers(HttpMethod.GET, "/restaurant/api/users/clients").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/restaurant/api/users/employees/count").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/restaurant/api/users/search").hasRole("ADMIN")

                        .pathMatchers(HttpMethod.GET, "/api/menu/dashboard/orders").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/menu/dashboard/my-orders").authenticated()
                        .pathMatchers(HttpMethod.GET, "/api/menu/dashboard/users/search").authenticated()
                        .pathMatchers(HttpMethod.GET, "/api/menu/dashboard/users/employees-count").hasRole("ADMIN")
                        .anyExchange().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authenticationSuccessHandler(customAuthenticationSuccessHandler())
                )
                .oauth2Client(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public ServerAuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (webFilterExchange, authentication) -> {
            System.out.println("=== LOGIN SUCCESS ===");
            System.out.println("User: " + authentication.getName());
            System.out.println("Authorities: " + authentication.getAuthorities());

            RedirectServerAuthenticationSuccessHandler redirectHandler = new RedirectServerAuthenticationSuccessHandler("/");
            redirectHandler.setLocation(URI.create("/"));
            return redirectHandler.onAuthenticationSuccess(webFilterExchange, authentication);
        };
    }

    @Bean
    public ReactiveOAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        final OidcReactiveOAuth2UserService delegate = new OidcReactiveOAuth2UserService();

        return (userRequest) -> delegate.loadUser(userRequest).map(oidcUser -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>(oidcUser.getAuthorities());
            String email = oidcUser.getEmail();


            if (email != null) {
                if (email.equals("andreea.vilcu2@gmail.com") ||
                        email.equals("ruxandraurs@gmail.com") ||
                        email.equals("ruxandra@student.unitbv.ro") ||
                        email.equals("adrihuruba22@gmail.com")) {

                    System.out.println("Assigning ADMIN role to: " + email);
                    mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_CLIENT"));
                } else {
                    System.out.println("Assigning CLIENT role to: " + email);
                    mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_CLIENT"));
                }
            }

            return new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
        });
    }
}