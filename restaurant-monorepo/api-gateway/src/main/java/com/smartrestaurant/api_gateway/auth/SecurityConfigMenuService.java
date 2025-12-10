package com.smartrestaurant.api_gateway.auth;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.cloudresourcemanager.CloudResourceManager;
import com.google.api.services.cloudresourcemanager.model.Binding;
import com.google.api.services.cloudresourcemanager.model.GetIamPolicyRequest;
import com.google.api.services.cloudresourcemanager.model.Policy;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
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
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfigMenuService {

    private final String idProject = "restaurant-480821" ;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/public/**", "/login/**").permitAll()
                        //dishes
                        .pathMatchers(HttpMethod.POST, "/restaurant/api/dishes").hasAuthority("ROLE_ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/restaurant/api/dishes/{id}").hasAuthority("ROLE_ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/restaurant/api/dishes/{id}").hasAuthority("ROLE_ADMIN")
                        .pathMatchers(HttpMethod.GET, "/restaurant/api/dishes").authenticated()
                        .pathMatchers(HttpMethod.GET, "/restaurant/api/dishes/{id}").hasAuthority("ROLE_ADMIN")

                        .pathMatchers(HttpMethod.GET, "/restaurant/api/dishes/search").authenticated()
                        .pathMatchers(HttpMethod.GET, "/restaurant/api/dishes/filter").authenticated()
                        .pathMatchers(HttpMethod.GET, "/restaurant/api/dishes/sorted").authenticated()

                        // categories
                        .pathMatchers(HttpMethod.POST, "/restaurant/api/categories").hasAuthority("ROLE_ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/restaurant/api/categories/{id}").hasAuthority("ROLE_ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/restaurant/api/categories/{id}").hasAuthority("ROLE_ADMIN")
                        .pathMatchers(HttpMethod.GET, "/restaurant/api/categories/{id}").hasAuthority("ROLE_ADMIN")
                        .pathMatchers(HttpMethod.GET, "/restaurant/api/categories").authenticated()

                        // ingredients
                        .pathMatchers(HttpMethod.POST, "/restaurant/api/ingredients").hasAuthority("ROLE_ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/restaurant/api/ingredients").hasAuthority("ROLE_ADMIN")
                        .pathMatchers(HttpMethod.GET, "/restaurant/api/ingredients").authenticated()
                        .pathMatchers(HttpMethod.GET, "/restaurant/api/ingredients/{id}").hasAuthority("ROLE_ADMIN")

                        .anyExchange().authenticated())
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
            try {
                Set<GrantedAuthority> iamRoles = getIamRoles(userRequest, oidcUser);
                mappedAuthorities.addAll(iamRoles);

            }catch (GeneralSecurityException | IOException e) {
                System.out.println(e.getMessage());
                mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_CLIENT"));
            }
            System.out.println("User: " + email + " | Mapped authorities: " + mappedAuthorities);

            return new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
        });
    }
    private Set<GrantedAuthority> getIamRoles(OidcUserRequest userRequest, OidcUser oidcUser) throws GeneralSecurityException, IOException {

        String accessTokenValue = userRequest.getAccessToken().getTokenValue();
        System.out.println("accessTokenValue: " + accessTokenValue);

        AccessToken accessToken = new AccessToken(accessTokenValue, Date.from(userRequest.getAccessToken().getExpiresAt()));
        System.out.println("accessToken: " + accessToken.getTokenValue());

        GoogleCredentials credentials = GoogleCredentials.create(accessToken);
        System.out.println("credentials: " + credentials);

        CloudResourceManager handler = new CloudResourceManager.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("GatewayServer")
                .build();

        GetIamPolicyRequest policyRequest = new GetIamPolicyRequest();
        Policy policy =  handler.projects().getIamPolicy(idProject, policyRequest).execute();
        System.out.println("policy: " + policy);

        String email = oidcUser.getEmail();
        String identifier = "user:" + email;

        return policy.getBindings().stream()
                .filter(binding -> binding.getMembers() != null && binding.getMembers().contains(identifier))
                .map(Binding::getRole)
                .peek(role -> System.out.println("Role is: " + role))
                .map(this::mapIamRolesToApplicationRoles)
                .collect(Collectors.toSet());

    }

    private GrantedAuthority mapIamRolesToApplicationRoles(String role) {
        if("roles/owner".equals(role) || "roles/editor".equals(role))
            return new SimpleGrantedAuthority("ROLE_ADMIN");

        if("roles/viewer".equals(role))
            return new SimpleGrantedAuthority("ROLE_CLIENT");

        return new SimpleGrantedAuthority("ROLE_CLIENT");
    }
}
